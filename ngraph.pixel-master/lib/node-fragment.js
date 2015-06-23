module.exports = [
'uniform vec3 color;',
'uniform sampler2D texture;',
'',
'varying vec3 vColor;',
'',
'void main() {',
'  gl_FragColor = vec4( color * vColor, 1.0 );',
'  vec4 tColor = texture2D( texture, gl_PointCoord );',
'  gl_FragColor = vec4(gl_FragColor.rgb * tColor.a, tColor.a);',
'}'
].join('\n');
