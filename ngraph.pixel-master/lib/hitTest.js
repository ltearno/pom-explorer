/**
 * Gives an index of a node under mouse coordinates
 */
var eventify = require('ngraph.events');
var THREE = require('three');

module.exports = createHitTest;

function createHitTest(domElement) {
  var particleSystem;
  var lastIntersected;
  var postponed = true;

  var raycaster = new THREE.Raycaster();

  // This defines sensitivity of raycaster. TODO: Should it depend on node size?
  raycaster.params.PointCloud.threshold = 10;
  domElement = domElement || document.body;

  // we will store mouse coordinates here to process on next RAF event (`update()` method)
  var mouse = {
    x: 0,
    y: 0
  };

  // store DOM coordinates as well, to let clients know where mouse is
  var domMouse = {
    down: false,
    x: 0,
    y: 0,
    nodeIndex: undefined
  };
  var singleClickHandler;

  domElement.addEventListener('mousemove', onMouseMove, false);
  domElement.addEventListener('mousedown', onMouseDown, false);
  domElement.addEventListener('mouseup', onMouseUp, false);
  domElement.addEventListener('touchstart', onTouchStart, false);
  domElement.addEventListener('touchend', onTouchEnd, false);

  var api = {
    /**
     * This should be called from RAF. Initiates process of hit test detection
     */
    update: update,

    /**
     * Reset all caches. Most likely underlying scene changed
     * too much.
     */
    reset: reset,

    /**
     * Hit tester should not emit events until mouse moved
     */
    postpone: postpone
  };

  // let us publish events
  eventify(api);
  return api;

  function postpone() {
    // postpone processing of hit testing until next mouse movement
    // this gives opportunity to avoid race conditions.
    postponed = true;
  }

  function reset() {
    particleSystem = null;
  }

  function onMouseUp() {
    domMouse.down = false;
    postponed = true;
  }

  function onMouseDown() {
    postponed = false;
    domMouse.down = true;
    domMouse.nodeIndex = lastIntersected;

    if (singleClickHandler) {
      // If we were able to get here without clearing single click handler,
      // then we are dealing with double click.

      // No need to fire single click event anymore:
      clearTimeout(singleClickHandler);
      singleClickHandler = null;

      // fire double click instead:
      api.fire('nodedblclick', domMouse);
    } else {
      // Wait some time before firing event. It can be a double click...
      singleClickHandler = setTimeout(function() {
        api.fire('nodeclick', domMouse);
        singleClickHandler = undefined;
      }, 300);
    }
  }

  function onTouchStart(e) {
    if (!e.touches || e.touches.length !== 1) {
      postponed = true;
      return;
    }

    postponed = false;
    setMouseCoordinates(e.touches[0]);
  }

  function onTouchEnd(e) {
    if (e.touches && e.touches.length === 1) {
      setMouseCoordinates(e.touches[0]);
    }
    setTimeout(function() {
      postponed = false;
      api.fire('nodeclick', domMouse);
    }, 0);
  }

  function onMouseMove(e) {
    setMouseCoordinates(e);
    postponed = false; // mouse moved, we are free.
  }

  function setMouseCoordinates(e) {
    mouse.x = (e.clientX / domElement.clientWidth) * 2 - 1;
    mouse.y = -(e.clientY / domElement.clientHeight) * 2 + 1;

    domMouse.x = e.clientX;
    domMouse.y = e.clientY;
  }

  function update(scene, camera) {
    // We need to stop processing any events until user moves mouse.
    // this is to avoid race conditions between search field and scene
    if (postponed) return;

    if (!particleSystem) {
      scene.children.forEach(function(child) {
        if (child.name === 'nodes') {
          particleSystem = child;
        }
      });
      if (!particleSystem) return;
    }

    raycaster.setFromCamera(mouse, camera);
    var newIntersected = getIntersects(camera);

    if (newIntersected !== undefined) {
      if (lastIntersected !== newIntersected) {
        lastIntersected = newIntersected;
        notifySelected(lastIntersected);
      }
    } else if (typeof lastIntersected === 'number') {
      // there is no node under mouse cursor. Let it know to UI:
      lastIntersected = undefined;
      notifySelected(undefined);
    }
  }

  function getIntersects() {
    var geometry = particleSystem.geometry;
    var attributes = geometry.attributes;
    var positions = attributes.position.array;
    return raycast(positions, raycaster.ray, particleSystem.matrixWorld.elements);
  }

  function raycast(positions, ray, worldMatrix) {
    var pointCount = positions.length / 3;
    var minDistance = Number.POSITIVE_INFINITY;
    var minIndex = -1;

    var ox = ray.origin.x;
    var oy = ray.origin.y;
    var oz = ray.origin.z;

    var dx = ray.direction.x;
    var dy = ray.direction.y;
    var dz = ray.direction.z;
    var pt = {
      x: 0,
      y: 0,
      z: 0
    };

    for (var i = 0; i < pointCount; i++) {
      testPoint(positions[3 * i], positions[3 * i + 1], positions[3 * i + 2], i);
    }

    if (minIndex !== -1) {
      return minIndex;
    }

    function testPoint(x, y, z, idx) {
      var distance = distanceTo(x, y, z);
      if (distance < 10) {
        var ip = nearestTo(x, y, z); // intersect point
        applyMatrix(ip, worldMatrix);
        distance = Math.sqrt((ox - ip.x) * (ox - ip.x) + (oy - ip.y) * (oy - ip.y) + (oz - ip.z) * (oz - ip.z));
        if (distance < minDistance) {
          minDistance = distance;
          minIndex = idx;
        }
      }
    }

    function applyMatrix(pt, e) {
      var x = pt.x;
      var y = pt.y;
      var z = pt.z;
      pt.x = e[0] * x + e[4] * y + e[8] * z + e[12];
      pt.y = e[1] * x + e[5] * y + e[9] * z + e[13];
      pt.z = e[2] * x + e[6] * y + e[10] * z + e[14];
    }

    function nearestTo(x, y, z) {
      var directionDistance = (x - ox) * dx + (y - oy) * dy + (z - oz) * dz;
      if (directionDistance < 0) {
        pt.x = ox;
        pt.y = oy;
        pt.z = oz;
      } else {
        pt.x = dx * directionDistance + ox;
        pt.y = dy * directionDistance + oy;
        pt.z = dz * directionDistance + oz;
      }
      return pt;
    }

    function distanceTo(x, y, z) {
      var directionDistance = (x - ox) * dx + (y - oy) * dy + (z - oz) * dz;
      if (directionDistance < 0) {
        // point behind ray
        return Math.sqrt((ox - x) * (ox - x) + (oy - y) * (oy - y) + (oz - z) * (oz - z));
      }
      var vx = dx * directionDistance + ox;
      var vy = dy * directionDistance + oy;
      var vz = dz * directionDistance + oz;

      return Math.sqrt((vx - x) * (vx - x) + (vy - y) * (vy - y) + (vz - z) * (vz - z));
    }
  }

  function notifySelected(index) {
    domMouse.nodeIndex = index;
    api.fire('nodeover', domMouse);
  }
}
