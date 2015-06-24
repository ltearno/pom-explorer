module.exports = pixel;
var THREE = require('three');
var eventify = require('ngraph.events');
var createNodeView = require('./lib/nodeView.js');
var createEdgeView = require('./lib/edgeView.js');
var createTooltipView = require('./lib/tooltip.js');
var createAutoFit = require('./lib/autoFit.js');
var createInput = require('./lib/input.js');
var validateOptions = require('./options.js');
var flyTo = require('./lib/flyTo.js');

function pixel(graph, options) {
  // This is our public API.
  var api = {
    /**
     * Set or get size of a node
     *
     * @param {string} nodeId identifier of a node in question
     * @param {number+} size if undefined, then current node size is returned;
     * Otherwise the new value is set.
     */
    nodeSize: nodeSize,

    /**
     * Set or get color of a node
     *
     * @param {string} nodeId identifier of a node in question
     * @param {number+|Array} color rgb color hex code. If not specified, then current
     * node color is returned. Otherwise the new color is assigned to the node.
     * This value can also be an array of three arguments, in that case each element
     * of the array is considered to be [r, g, b]
     */
    nodeColor: nodeColor,

    /**
     * Sets color of a link
     *
     * @param {string} linkId identifier of a link.
     * @param {number} fromColorHex - rgb color hex code of a link start
     * @param {number+} toColorHex - rgb color hex code of theh link end. If not
     * specified the same value as `fromColorHex` is used.
     */
    linkColor: linkColor,

    /**
     * attempts to fit graph into available screen size
     */
    autoFit: autoFit,

    /**
     * Returns current layout manager
     */
    layout: getLayout,

    /**
     * Gets or sets value which indicates whether layout is stable. When layout
     * is stable, then no additional layout iterations are required. The renderer
     * will stop calling `layout.step()`, which in turn will save CPU cycles.
     *
     * @param {boolean+} stableValue if this value is not specified, then current
     * value of `isStable` will be returned. Otherwise the simulator stable flag
     * will be forcefully set to the given value.
     */
    stable: stable,

    /**
     * Gets or sets graph that is rendered now
     *
     * @param {ngraph.graph+} graphValue if this value is not specified then current
     * graph is returned. Otherwise renderer destroys current scene, and starts
     * render new graph.
     */
    graph: graphInternal,

    /**
     * Attempts to give keyboard input focuse to the scene
     */
    focus: focus,

    /**
     * Requests renderer to move camera and focus on given node id.
     *
     * @param {string} nodeId identifier of the node to show
     */
    showNode: showNode,

    /**
     * Allows clients to provide a callback function, which is invoked before
     * each rendering frame
     *
     * @param {function} newBeforeFrameCallback the callback function. This
     * argument is not chained, and any new value overwrites the old one
     */
    beforeFrame: beforeFrame,

    /**
     * Returns instance of the three.js camera
     */
    camera: getCamera,

    /**
     * Allows clients to set/get current clear color of the scene (the background)
     *
     * @param {number+} color if specified, then new color is set. Otherwise
     * returns current clear color.
     */
    clearColor: clearColor,

    /**
     * Synonmim for `clearColor`. Sets the background color of the scene
     *
     * @param {number+} color if specified, then new color is set. Otherwise
     * returns current clear color.
     */
    background: clearColor
  };

  eventify(api);

  options = validateOptions(options);

  var beforeFrameCallback;
  var container = options.container;
  var layout = options.createLayout(graph, options);
  if (layout && typeof layout.on === 'function') {
    layout.on('reset', layoutReset);
  }
  var isStable = false;
  var nodeIdToIdx = Object.create(null);
  var edgeIdToIdx = Object.create(null);
  var nodeIdxToId = [];

  var scene, camera, renderer;
  var nodeView, edgeView, autoFitController, input;
  var nodePositions, edgePositions;
  var tooltipView = createTooltipView(container);

  init();
  run();
  focus();

  return api;

  function layoutReset() {
    initPositions();
    stable(false);
  }

  function getCamera() {
    return camera;
  }

  function clearColor(newColor) {
    newColor = normalizeColor(newColor);
    if (typeof newColor !== 'number') return renderer.getClearColor();

    renderer.setClearColor(newColor);
  }

  function run() {
    requestAnimationFrame(run);

    if (beforeFrameCallback) {
      beforeFrameCallback();
    }
    if (!isStable) {
      if( window.animatedGraph )
        isStable = layout.step();
      nodeView.update();
      edgeView.update();
    } else {
      // we may not want to change positions, but colors/size could be changed
      // at this moment, so let's take care of that:
      if (nodeView.needsUpdate()) nodeView.update();
      if (edgeView.needsUpdate()) edgeView.update();
    }

    if (isStable) api.fire('stable', true);

    input.update();
    if (autoFitController) {
      autoFitController.update();
    }
    renderer.render(scene, camera);
  }

  function beforeFrame(newBeforeFrameCallback) {
    beforeFrameCallback = newBeforeFrameCallback;
  }

  function init() {
    initScene();
    initPositions();
    listenToGraph();
  }

  function listenToGraph() {
    // TODO: this is not efficient at all. We are recriating view from scratch on
    // every single change.
    graph.on('changed', initPositions);
  }

  function initPositions() {
    var idx = 0;
    edgePositions = [];
    nodePositions = [];
    graph.forEachNode(addNodePosition);
    graph.forEachLink(addEdgePosition);

    nodeView.initPositions(nodePositions);
    edgeView.initPositions(edgePositions);

    if (input) input.reset();

    function addNodePosition(node) {
      var position = layout.getNodePosition(node.id);
      if (typeof position.z !== 'number') position.z = 0;
      nodePositions.push(position);
      nodeIdToIdx[node.id] = idx;
      nodeIdxToId[idx] = node.id;
      idx += 1;
    }

    function addEdgePosition(edge) {
      var edgeOffset = edgePositions.length;
      edgeIdToIdx[edge.id] = edgeOffset;
      edgePositions.push(nodePositions[nodeIdToIdx[edge.fromId]], nodePositions[nodeIdToIdx[edge.toId]]);
    }
  }

  function initScene() {
    scene = new THREE.Scene();
    scene.sortObjects = false;

    camera = new THREE.PerspectiveCamera(75, container.clientWidth / container.clientHeight, 0.1, 20000);
    camera.position.x = 0;
    camera.position.y = 0;
    camera.position.z = 200;

    scene.add(camera);
    nodeView = createNodeView(scene);
    edgeView = createEdgeView(scene);

    if (options.autoFit) autoFitController = createAutoFit(nodeView, camera);

    renderer = new THREE.WebGLRenderer({
      antialias: false
    });

    renderer.setClearColor(options.clearColor, 1);
    renderer.setSize(container.clientWidth, container.clientHeight);
    container.appendChild(renderer.domElement);

    input = createInput(camera, graph, renderer.domElement);
    input.on('move', stopAutoFit);
    input.on('nodeover', setTooltip);
    input.on('nodeclick', passthrough('nodeclick'));
    input.on('nodedblclick', passthrough('nodedblclick'));

    window.addEventListener('resize', onWindowResize, false);
  }

  function nodeColor(nodeId, color) {
    var idx = getNodeIdxByNodeId(nodeId);
    return nodeView.color(idx, normalizeColor(color));
  }

  function nodeSize(nodeId, size) {
    var idx = getNodeIdxByNodeId(nodeId);
    return nodeView.size(idx, size);
  }

  function linkColor(linkId, fromColorHex, toColorHex) {
    var idx = edgeIdToIdx[linkId];
    var idxValid = (0 <= idx && idx < edgePositions.length);
    if (!idxValid) throw new Error('Link index is not valid ' + linkId);
    return edgeView.color(idx, normalizeColor(fromColorHex), normalizeColor(toColorHex));
  }

  function getNodeIdxByNodeId(nodeId) {
    var idx = nodeIdToIdx[nodeId];
    if (idx === undefined) throw new Error('Cannot find node with id ' + nodeId);
    var idxValid = (0 <= idx && idx < graph.getNodesCount());
    if (!idxValid) throw new Error('Node index is out of range' + nodeId);

    return idx;
  }

  function setTooltip(e) {
    var node = getNodeByIndex(e.nodeIndex);
    if (node) {
      tooltipView.show(e, node);
    } else {
      tooltipView.hide(e);
    }
    api.fire('nodehover', node);
  }

  function passthrough(name) {
    return function (e) {
      var node = getNodeByIndex(e.nodeIndex);
      if (node) api.fire(name, node);
    };
  }

  function getNodeByIndex(nodeIndex) {
    return nodeIndex && graph.getNode(nodeIdxToId[nodeIndex]);
  }

  function stopAutoFit() {
    input.off('move');
    autoFitController = null;
  }

  function onWindowResize() {
    camera.aspect = container.clientWidth / container.clientHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(container.clientWidth, container.clientHeight);
  }

  function autoFit() {
    if (autoFitController) return; // we are already auto-fitting the graph.
    // otherwise fire and forget autofit:
    createAutoFit(nodeView, camera).update();
  }

  function getLayout() {
    return layout;
  }

  function stable(stableValue) {
    if (stableValue === undefined) return isStable;
    isStable = stableValue;
    api.fire('stable', isStable);
  }

  function graphInternal(newGraph) {
    if (newGraph !== undefined) throw new Error('Not implemented, Anvaka, do it!');
    return graph;
  }

  function normalizeColor(color) {
    if (color === undefined) return color;
    var colorType = typeof color;
    if (colorType === 'number') return color;
    if (colorType === 'string') return parseStringColor(color);
    if (color.length === 3) return (color[0] << 16) | (color[1] << 8) | (color[2]);
    throw new Error('Unrecognized color type: ' + color);
  }

  function parseStringColor(color) {
    if (color[0] === '#') {
      return Number.parseInt(color.substring(1), 16);
    }
    return Number.parseInt(color, 16);
  }

  function focus() {
    var sceneElement = renderer && renderer.domElement;
    if (sceneElement && typeof sceneElement.focus === 'function') sceneElement.focus();
  }

  function showNode(nodeId, stopDistance) {
    stopDistance = typeof stopDistance === 'number' ? stopDistance : 100;
    flyTo(camera, layout.getNodePosition(nodeId), stopDistance);
  }
}
