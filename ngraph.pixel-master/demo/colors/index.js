var query = require('query-string').parse(window.location.search.substring(1));
var graph = getGraphFromQueryString(query);

var nodeColor = Object.create(null);
var renderGraph = require('../../');

var renderer = renderGraph(graph);
graph.forEachNode(setCustomNodeUI);
graph.forEachLink(setCustomLinkUI);

function setCustomNodeUI(node) {
  // we are going to remember node colors, so that edges can get same color as well:
  var color = nodeColor[node.id] = Math.random() * 0xFFFFFF | 0;
  renderer.nodeColor(node.id, color);
  renderer.nodeSize(node.id, Math.random() * 21 + 10);
}

function setCustomLinkUI(link) {
  var fromColor = nodeColor[link.fromId];
  var toColor = nodeColor[link.toId];
  renderer.linkColor(link.id, fromColor, toColor);
}

function getGraphFromQueryString(query) {
   var graphGenerators = require('ngraph.generators');
  var createGraph = graphGenerators[query.graph] || graphGenerators.grid;
  return createGraph(getNumber(query.n), getNumber(query.m), getNumber(query.k));
}

function getNumber(string, defaultValue) {
  var number = parseFloat(string);
  return (typeof number === 'number') && !isNaN(number) ? number : (defaultValue || 10);
}
