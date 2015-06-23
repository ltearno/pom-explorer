var THREE = require('three');
var particleMaterial = require('./createMaterial.js')();

module.exports = nodeView;

function nodeView(scene) {
  var total;
  var positions;
  var colors, points, sizes;
  var geometry, particleSystem;
  var colorDirty, sizeDirty;

  return {
    initPositions: initPositions,
    update: update,
    needsUpdate: needsUpdate,
    getBoundingSphere: getBoundingSphere,
    color: color,
    size: size
  };

  function needsUpdate() {
    return colorDirty || sizeDirty;
  }

  function color(idx, hexColor) {
    var idx3 = idx * 3;
    if (hexColor === undefined) {
      var r = colors[idx3    ];
      var g = colors[idx3 + 1];
      var b = colors[idx3 + 2];
      return (r << 16) | (g << 8) | b;
    }
    colors[idx3    ] = (hexColor >> 16) & 0xff;
    colors[idx3 + 1] = (hexColor >>  8) & 0xff;
    colors[idx3 + 2] = (hexColor      ) & 0xff;
    colorDirty = true;
  }

  function size(idx, sizeValue) {
    if (sizeValue === undefined) {
      return sizes[idx];
    }
    sizes[idx] = sizeValue;
    sizeDirty = true;
  }

  function update() {
    for (var i = 0; i < total; ++i) {
      setNodePosition(i * 3, positions[i]);
    }
    geometry.getAttribute('position').needsUpdate = true;
    if (colorDirty) {
      geometry.getAttribute('customColor').needsUpdate = true;
      colorDirty = false;
    }
    if (sizeDirty) {
      geometry.getAttribute('size').needsUpdate = true;
      sizeDirty = false;
    }
  }

  function getBoundingSphere() {
    if (!geometry) return;
    geometry.computeBoundingSphere();
    return geometry.boundingSphere;
  }

  function setNodePosition(nodeIdx, pos) {
    points[nodeIdx + 0] = pos.x;
    points[nodeIdx + 1] = pos.y;
    points[nodeIdx + 2] = pos.z;
  }

  function initPositions(nodePositions) {
    total = nodePositions.length;
    positions = nodePositions;
    points = new Float32Array(total * 3);
    var colorsInitialized = colors !== undefined && colors.length === total * 3;
    if (!colorsInitialized) colors = new Float32Array(total * 3);

    var sizesInitialized = sizes !== undefined && sizes.length === total;
    if (!sizesInitialized) sizes = new Float32Array(total);

    geometry = new THREE.BufferGeometry();

    geometry.addAttribute('position', new THREE.BufferAttribute(points, 3));
    geometry.addAttribute('customColor', new THREE.BufferAttribute(colors, 3));
    geometry.addAttribute('size', new THREE.BufferAttribute(sizes, 1));

    if (particleSystem) {
      scene.remove(particleSystem);
    }

    particleSystem = new THREE.PointCloud(geometry, particleMaterial);
    particleSystem.name = 'nodes';
    particleSystem.frustumCulled = false;

    scene.add(particleSystem);

    for (var i = 0; i < total; ++i) {
      setNodePosition(i * 3, positions[i]);
      if (!colorsInitialized) color(i, 0xffffff);
      if (!sizesInitialized) size(i, 15);
    }
  }
}
