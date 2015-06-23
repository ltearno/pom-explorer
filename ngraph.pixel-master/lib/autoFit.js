var flyTo = require('./flyTo.js');
module.exports = createAutoFit;

function createAutoFit(nodeView, camera) {
  return {
    update: update
  };

  function update() {
    var sphere = nodeView.getBoundingSphere();
    var radius = Math.max(sphere.radius, 100);
    flyTo(camera, sphere.center, radius);
  }
}
