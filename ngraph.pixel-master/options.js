/**
 * This file contains all possible configuration optins for the renderer
 */
module.exports = validateOptions;

var createLayout = require('pixel.layout'); // the default layout

function validateOptions(options) {
  options = options || {};

  /**
   * Where to render the graph? Assume `document.body` by default.
   */
  options.container = options.container || document.body;

  /**
  /* Let the renderer automatically fit the graph to available screen space.
   * Enabled by default.
   * Note: The autofit will only be executed until first user input.
   */
  options.autoFit = options.autoFit !== undefined ? options.autoFit : true;

  /**
   * Background of the scene in hexadecimal form. Default value is 0x000000 (black);
   */
  options.clearColor = typeof options.clearColor === 'number' ? options.clearColor : 0x000000;

  /**
   * Layout algorithm factory. Valid layout algorithms are required to have just two methods:
   * `getNodePosition(nodeId)` and `step()`. See `pixel.layout` module for the
   * reference: https://github.com/anvaka/pixel.layout
   */
  options.createLayout = typeof options.createLayout === 'function' ? options.createLayout : createLayout;

  return options;
}
