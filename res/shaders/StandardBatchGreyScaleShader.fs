#version 130
precision highp float;

uniform mat4 u_ProjMatrix;
uniform mat4 u_ViewMatrix;
uniform sampler2D u_TexColor;
uniform float greyStrength;

in vec2 v_FragPosition;
in vec4 v_Color;
in vec2 v_TexCoords;
out vec4 out_Color;

void main() {
   vec4 color = v_Color * texture2D(u_TexColor, v_TexCoords.xy);
   float grey = dot(color.rgb, vec3(0.299, 0.587, 0.114));
   float colorStrength = 1 - greyStrength;
   out_Color = vec4((greyStrength * grey) + (colorStrength * color.r), (greyStrength * grey) + (colorStrength * color.g), (greyStrength * grey) + (colorStrength * color.b), color.w);
}