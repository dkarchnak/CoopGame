package cz.bcx.coopgame.graphics;

import cz.bcx.coopgame.application.Log;

import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bcx on 11/25/16.
 */
public class BitmapFont {
    private static final String REGEX_INTEGER             = "[+-]?[0-9]+";
    private static final String REGEX_NAMED_GROUP_INTEGER = "(?<%s>" + REGEX_INTEGER + ")"; //Use String.format to fill in the name!
    private static final String REGEX_WHITE_SPACE         = "\\s+";

    private static final String REGEX_NAME_KEY_ID         = "id";
    private static final String REGEX_NAME_KEY_X          = "x";
    private static final String REGEX_NAME_KEY_Y          = "y";
    private static final String REGEX_NAME_KEY_WIDTH      = "width";
    private static final String REGEX_NAME_KEY_HEIGHT     = "height";
    private static final String REGEX_NAME_KEY_X_OFFSET   = "xOffset";
    private static final String REGEX_NAME_KEY_Y_OFFSET   = "yOffset";
    private static final String REGEX_NAME_KEY_X_ADVANCE  = "xAdvance";

    private static final String REGEX_SINGLE_CHAR = "char" + REGEX_WHITE_SPACE +
                                                        "id="       + String.format(REGEX_NAMED_GROUP_INTEGER, REGEX_NAME_KEY_ID)        + REGEX_WHITE_SPACE +
                                                        "x="        + String.format(REGEX_NAMED_GROUP_INTEGER, REGEX_NAME_KEY_X)         + REGEX_WHITE_SPACE +
                                                        "y="        + String.format(REGEX_NAMED_GROUP_INTEGER, REGEX_NAME_KEY_Y)         + REGEX_WHITE_SPACE +
                                                        "width="    + String.format(REGEX_NAMED_GROUP_INTEGER, REGEX_NAME_KEY_WIDTH)     + REGEX_WHITE_SPACE +
                                                        "height="   + String.format(REGEX_NAMED_GROUP_INTEGER, REGEX_NAME_KEY_HEIGHT)    + REGEX_WHITE_SPACE +
                                                        "xoffset="  + String.format(REGEX_NAMED_GROUP_INTEGER, REGEX_NAME_KEY_X_OFFSET)  + REGEX_WHITE_SPACE +
                                                        "yoffset="  + String.format(REGEX_NAMED_GROUP_INTEGER, REGEX_NAME_KEY_Y_OFFSET)  + REGEX_WHITE_SPACE +
                                                        "xadvance=" + String.format(REGEX_NAMED_GROUP_INTEGER, REGEX_NAME_KEY_X_ADVANCE) + REGEX_WHITE_SPACE;



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
                    int x      = Integer.parseInt(matcher.group(REGEX_NAME_KEY_X));
                    int y      = Integer.parseInt(matcher.group(REGEX_NAME_KEY_Y));
                    int width  = Integer.parseInt(matcher.group(REGEX_NAME_KEY_WIDTH));
                    int height = Integer.parseInt(matcher.group(REGEX_NAME_KEY_HEIGHT));

                    Glyph glyph = new Glyph(
                        //Actual char
                        (char)(Integer.parseInt(matcher.group(REGEX_NAME_KEY_ID))),
                        //UV Coords. OpenGL textures Y is flipped!
                        x / (float)fontTexture.getWidth(),
                        1 - ((y + height) / (float)fontTexture.getHeight()),
                        (x + width) / (float)fontTexture.getWidth(),
                        1 - (y / (float)fontTexture.getWidth()),
                        //Glyph dimensions
                        width,
                        height,
                        //Offsets and XAdvance. Calculates correct Y offset.
                        Integer.parseInt(matcher.group(REGEX_NAME_KEY_X_OFFSET)),
                        height + Integer.parseInt(matcher.group(REGEX_NAME_KEY_Y_OFFSET)),
                        Integer.parseInt(matcher.group(REGEX_NAME_KEY_X_ADVANCE))
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