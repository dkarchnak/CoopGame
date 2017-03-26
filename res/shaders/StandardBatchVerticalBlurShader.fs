#version 130
precision highp float;

uniform mat4 u_ProjMatrix;
uniform mat4 u_ViewMatrix;
uniform sampler2D u_TexColor; //TODO texture id or something
uniform int u_BlurMatrixSize; //in pixels
uniform float u_PixelSize;

in vec2 v_FragPosition;
in vec4 v_Color;
in vec2 v_TexCoords;
out vec4 out_Color;

vec4 color;

void main() {
    color = vec4(0);

    for(int i = -u_BlurMatrixSize; i <= u_BlurMatrixSize; i++) {
        color += texture2D(u_TexColor, vec2(v_TexCoords.x, v_TexCoords.y + i * u_PixelSize)) * (1.0 / (u_BlurMatrixSize * 2 + 1));
    }

   out_Color = v_Color * color;
}