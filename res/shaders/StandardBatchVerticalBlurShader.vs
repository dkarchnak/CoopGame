#version 130
in vec2 in_Position;
in vec4 in_Color;
in vec2 in_TexCoords;

uniform mat4 u_ProjMatrix;
uniform mat4 u_ViewMatrix;
uniform sampler2D u_TexColor; //TODO texture id or something
uniform int u_BlurMatrixSize;
uniform float u_PixelSize;

out vec2 v_FragPosition;
out vec4 v_Color;
out vec2 v_TexCoords;

void main() {
   v_Color = in_Color;
   v_TexCoords = in_TexCoords;
   v_FragPosition = in_Position;
   gl_Position = u_ProjMatrix * u_ViewMatrix * vec4(in_Position, 0, 1);
}