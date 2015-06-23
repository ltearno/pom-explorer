module.exports = intersect;

/**
 * Find intersection point on a sphere surface with radius `r` and center in the `to`
 * with a ray [to, from)
 */
function intersect(from, to, r) {
  // we are using Cartesian to Spherical coordinates transformation to find
  // theta and phi:
  // https://en.wikipedia.org/wiki/Spherical_coordinate_system#Coordinate_system_conversions
  var dx = from.x - to.x;
  var dy = from.y - to.y;
  var dz = from.z - to.z;
  var r1 = Math.sqrt(dx * dx + dy * dy + dz * dz);
  var teta = Math.acos(dz / r1);
  var phi = Math.atan2(dy, dx);

  // And then based on sphere radius we transform back to Cartesian:
  return {
    x: r * Math.sin(teta) * Math.cos(phi) + to.x,
    y: r * Math.sin(teta) * Math.sin(phi) + to.y,
    z: r * Math.cos(teta) + to.z
  };
}
