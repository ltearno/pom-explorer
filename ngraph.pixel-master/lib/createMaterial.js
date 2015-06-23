var THREE = require('three');

module.exports = createParticleMaterial;

function createParticleMaterial() {

  var vertexShader = require('./node-vertex.js');
  var fragmentShader = require('./node-fragment.js');

  var attributes = {
    size: {
      type: 'f',
      value: null
    },
    customColor: {
      type: 'c',
      value: null
    }
  };

  var uniforms = {
    color: {
      type: "c",
      value: new THREE.Color(0xffffff)
    },
    texture: {
      type: "t",
      value: THREE.ImageUtils.loadTexture(require('./defaultTexture.js'))
    }
  };

  var material =  new THREE.ShaderMaterial({
    uniforms: uniforms,
    attributes: attributes,
    vertexShader: vertexShader,
    fragmentShader: fragmentShader,
    transparent: true,
    depthTest: false
  });
  return material;
}
