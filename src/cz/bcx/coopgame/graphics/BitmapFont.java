package cz.bcx.coopgame.graphics;

import cz.bcx.coopgame.application.Log;
import cz.bcx.coopgame.application.Main;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bcx on 11/25/16.
 */
public class BitmapFont {
    private static final String REGEX_INTEGER       = "[+-]?[0-9]+";
    private static final String REGEX_INTEGER_GROUP = "(" + REGEX_INTEGER + ")";
    private static final String REGEX_WHITE_SPACE   = "\\s+";

    private static final String REGEX_SINGLE_CHAR = "char" + REGEX_WHITE_SPACE +
                                                        "id=" + REGEX_INTEGER_GROUP + REGEX_WHITE_SPACE +
                                                        "x=" + REGEX_INTEGER_GROUP + REGEX_WHITE_SPACE +
                                                        "y=" + REGEX_INTEGER_GROUP + REGEX_WHITE_SPACE +
                                                        "width=" + REGEX_INTEGER_GROUP + REGEX_WHITE_SPACE +
                                                        "height=" + REGEX_INTEGER_GROUP + REGEX_WHITE_SPACE +
                                                        "xoffset=" + REGEX_INTEGER_GROUP + REGEX_WHITE_SPACE +
                                                        "yoffset=" + REGEX_INTEGER_GROUP + REGEX_WHITE_SPACE +
                                                        "xadvance=" + REGEX_INTEGER_GROUP + REGEX_WHITE_SPACE;

    private static final int REGEX_GROUP_CHAR_ID        = 1;
    private static final int REGEX_GROUP_CHAR_X         = 2;
    private static final int REGEX_GROUP_CHAR_Y         = 3;
    private static final int REGEX_GROUP_CHAR_WIDTH     = 4;
    private static final int REGEX_GROUP_CHAR_HEIGHT    = 5;
    private static final int REGEX_GROUP_CHAR_X_OFFSET  = 6;
    private static final int REGEX_GROUP_CHAR_Y_OFFSET  = 7;
    private static final int REGEX_GROUP_CHAR_X_ADVANCE = 8;


    public class Glyph {
        private Character character;
        private float u, v, u2, v2;
        private int width, height;
        private int offsetX, offsetY;
        private final int xAdvance;

        public Glyph(Character character, float u, float v, float u2, float v2, int width, int height, int offsetX, int offsetY, int xAdvance) {
            this.character = character;

            //UV Coords
            this.u = u; this.v = v;
            this.u2 = u2; this.v2 = v2;

            this.width = width;
            this.height = height;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.xAdvance = xAdvance;
        }

        public Character getCharacter() {
            return character;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + ": [Character='" + character + "'" +
                    ", Width=" + width + ", Height=" + height + ", OffsetX=" + offsetX + ", OffsetY=" + offsetY +
                    ", XAdvance=" + xAdvance + "]";
        }
    }

    private HashMap<Character, Glyph> characterMap;

    private final File file;
    private final Texture fontTexture;

    public BitmapFont(File file, Texture texture) {
        this.file = file;
        this.fontTexture = texture;
    }

    //TODO - Add full support for .fnt files. Kernings, Lineheight, Base, ScaleW/H... Pages?!
    public void load() {
        if (file == null || !file.exists()) {
            throw new RuntimeException("Bitmap font file with path: '" + file.getAbsolutePath() + "' doesn't exist!");
        }

        characterMap = getGlyphMapFromFile(file);
    }

    private HashMap<Character, Glyph> getGlyphMapFromFile(File file) {
        HashMap<Character, Glyph> resultMap = new HashMap<>();

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));

            Pattern pattern = Pattern.compile(REGEX_SINGLE_CHAR);
            Matcher matcher;

            String currentLine;

            int glyphCount = 0;
            while((currentLine = fileReader.readLine()) != null) {
                matcher = pattern.matcher(currentLine);

                if(matcher.find()) {
                    int x      = Integer.parseInt(matcher.group(REGEX_GROUP_CHAR_X));
                    int y      = Integer.parseInt(matcher.group(REGEX_GROUP_CHAR_Y));
                    int width  = Integer.parseInt(matcher.group(REGEX_GROUP_CHAR_WIDTH));
                    int height = Integer.parseInt(matcher.group(REGEX_GROUP_CHAR_HEIGHT));

                    Glyph glyph = new Glyph(
                        //Actual char
                        (char)(Integer.parseInt(matcher.group(REGEX_GROUP_CHAR_ID))),
                        //UV Coords. OpenGL textures Y is flipped!
                        x / (float)fontTexture.getWidth(),
                        1 - ((y + height) / (float)fontTexture.getHeight()),
                        (x + width) / (float)fontTexture.getWidth(),
                        1 - (y / (float)fontTexture.getWidth()),
                        //Glyph dimensions
                        width,
                        height,
                        //Offsets and XAdvance. Calculates correct Y offset.
                        Integer.parseInt(matcher.group(REGEX_GROUP_CHAR_X_OFFSET)),
                        height + Integer.parseInt(matcher.group(REGEX_GROUP_CHAR_Y_OFFSET)),
                        Integer.parseInt(matcher.group(REGEX_GROUP_CHAR_X_ADVANCE))
                    );
                    resultMap.put(glyph.getCharacter(), glyph);
                    glyphCount++;
                }
            }
            Log.debug(getClass(), "Loaded " + glyphCount + " glyphs from file: '" + file.getName() + "'");
        } catch (IOException e) {
            Log.error(getClass(), "Cannot read glyph file: " + file.getName() + "!", e);
        }

        return resultMap;
    }
}