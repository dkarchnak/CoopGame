#version 130
precision highp float;

uniform mat4 u_ProjMatrix;
uniform sampler2D u_TexColor;

in vec2 v_FragPosition;
in vec4 v_Color;
in vec2 v_TexCoords;
out vec4 out_Color;

void main() {
   vec4 texColor = texture2D(u_TexColor, v_TexCoords.xy);
   out_Color = v_Color * texColor;
}