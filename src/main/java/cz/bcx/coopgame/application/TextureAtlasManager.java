package cz.bcx.coopgame.application;

import cz.bcx.coopgame.graphics.Texture;
import cz.bcx.coopgame.graphics.TextureRegion;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bcx on 8/10/16.
 */
public class TextureAtlasManager {
    private enum ParsingState {
        None,
        Texture,
        Region
    }

    private static final String KEYWORD_REGION               = "region";
    private static final String KEYWORD_TEXTURE              = "texture";

    private static final String TEXTURE_PARAM_PATH           = "path";
    private static final String TEXTURE_PARAM_FILTER         = "filter";
    private static final String TEXTURE_FILTER_LINEAR        = "linear";
    private static final String TEXTURE_FILTER_NEAREST       = "nearest";

    private static final String TEXTURE_REGION_PARAM_TEX_KEY = "textureKey";
    private static final String TEXTURE_REGION_PARAM_UV      = "texCoords";

    private static final char   COMMENT_CHAR    = '#';
    private static final char   OPEN_BRACKET    = '{';
    private static final char   CLOSE_BRACKET   = '}';

    private Pattern texCoordinatesPattern = Pattern.compile(getTextureCoordinatesRegex());
    private Pattern texFilterPattern      = Pattern.compile(getTextureFilterRegex());

    private boolean destroyed = false;

    private Map<Integer, Texture> textureMap;
    private Map<Integer, TextureRegion> textureRegionMap;

    public TextureAtlasManager(String textureAtlasPath) {
        textureMap = new HashMap<>();
        textureRegionMap = new HashMap<>();

        try {
            loadAtlasFile(new File(textureAtlasPath));
        } catch (IOException e) {
            throw new RuntimeException("Cannot create Texture Atlas from file: " + textureAtlasPath, e);
        }
    }

    private void loadAtlasFile(File file) throws IOException {
        if(!file.exists() || !file.canRead()) throw new IOException("Cannot find or read file: " + file.getAbsolutePath());

        textureRegionMap = new HashMap<>();
        parseAndStoreAtlasFile(file);
    }

    private String getStartBlockRegex() {
        return "(" + KEYWORD_TEXTURE + "|" + KEYWORD_REGION + ")\\s*(\\d)\\s*\\" + OPEN_BRACKET;
    }

    private String getEndBlockRegex() {
        return "\\s*\\" + CLOSE_BRACKET + "\\s*";
    }

    private String getParameterRegex() {
        return "([A-z0-9_-]{1,})\\s*:\\s*(.{1,})";
    }

    private String getTextureFilterRegex() {
        return "(linear|nearest)\\s+(linear|nearest)";
    }

    private String getTextureCoordinatesRegex() {
        return "(\\d+(?:\\.\\d*)?)\\s*(\\d+(?:\\.\\d*)?)\\s*(\\d+(?:\\.\\d*)?)\\s*(\\d+(?:\\.\\d*)?)";
    }

    private void parseAndStoreAtlasFile(File file) {
        ParsingState currentParsingState = ParsingState.None;
        int currentParsingId = -1;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            Pattern startBlockPattern = Pattern.compile(getStartBlockRegex());
            Pattern parameterPattern = Pattern.compile(getParameterRegex());

            Matcher matcher;

            HashMap<String, String> parameters = new HashMap<>();

            String currLine;
            while ((currLine = bufferedReader.readLine()) != null) {
                //Ignore comments
                if (currLine.trim().isEmpty() || currLine.trim().charAt(0) == COMMENT_CHAR) continue;

                matcher = startBlockPattern.matcher(currLine);

                switch (currentParsingState) {
                    case None:
                        if (matcher.find()) { //Matched "texture|region id {"
                            //Set current parsing state according to the match
                            if (matcher.group(1).equals(KEYWORD_TEXTURE)) currentParsingState = ParsingState.Texture;
                            else if (matcher.group(1).equals(KEYWORD_REGION)) currentParsingState = ParsingState.Region;
                            else throw new RuntimeException("Invalid file format!");

                            currentParsingId = Integer.parseInt(matcher.group(2));
                        }
                        break;
                    case Texture:
                        if (currLine.matches(getEndBlockRegex())) {
                            createTexture(currentParsingId, parameters);
                            currentParsingState = ParsingState.None;
                            parameters.clear();
                        }

                        matcher = parameterPattern.matcher(currLine);
                        if (matcher.find()) {
                            parameters.put(matcher.group(1), matcher.group(2));
                        }
                        break;
                    case Region:
                        if (currLine.matches(getEndBlockRegex())) {
                            createRegion(currentParsingId, parameters);
                            currentParsingState = ParsingState.None;
                            parameters.clear();
                        }

                        matcher = parameterPattern.matcher(currLine);
                        if (matcher.find()) {
                            parameters.put(matcher.group(1), matcher.group(2));
                        }
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTexture(int id, HashMap<String, String> parameters) {
        String texturePath;

        if((texturePath = parameters.get(TEXTURE_PARAM_PATH)) == null) throw new RuntimeException("Cannot load texture from path: " + texturePath);

        String textureFilter;
        Texture.TextureFilter minFilter = Texture.TextureFilter.Linear;
        Texture.TextureFilter magFilter = Texture.TextureFilter.Linear;

        if((textureFilter = parameters.get(TEXTURE_PARAM_FILTER)) != null) {
            Matcher m = texFilterPattern.matcher(textureFilter);

            if(m.find()) {
                String inputMinFilter = m.group(1);
                if(inputMinFilter.equals(TEXTURE_FILTER_NEAREST)) minFilter = Texture.TextureFilter.Nearest;
                else if(inputMinFilter.equals(TEXTURE_FILTER_LINEAR)) minFilter = Texture.TextureFilter.Linear;

                String inputMagFilter = m.group(2);
                if(inputMagFilter.equals(TEXTURE_FILTER_NEAREST)) magFilter = Texture.TextureFilter.Nearest;
                else if(inputMagFilter.equals(TEXTURE_FILTER_LINEAR)) magFilter = Texture.TextureFilter.Linear;
            }
        }

        textureMap.put(id, new Texture(texturePath, minFilter, magFilter));
    }


    private void createRegion(int id, HashMap<String, String> parameters) {
        String textureKey;
        if((textureKey = parameters.get(TEXTURE_REGION_PARAM_TEX_KEY)) == null) throw new RuntimeException("Texture region doesn't have any texture key!");

        String uv;
        if((uv = parameters.get(TEXTURE_REGION_PARAM_UV)) == null) throw new RuntimeException("Texture region doesn't have texture coordinates!");
        uv.trim();

        Matcher matcher = texCoordinatesPattern.matcher(uv);

        if(matcher.find()) {
            textureRegionMap.put(
                id,
                new TextureRegion(textureMap.get(Integer.parseInt(textureKey)),
                    Float.parseFloat(matcher.group(1)),
                    Float.parseFloat(matcher.group(2)),
                    Float.parseFloat(matcher.group(3)),
                    Float.parseFloat(matcher.group(4))
                )
            );
        }
        else throw new RuntimeException("Wrong format of texture coordinates!");
    }

    public TextureRegion getTextureRegion(int regionKey) {
        if(isDestroyed()) throw new ResourceDestroyedException("Texture Atlas Manager has been destroyed!");
        return textureRegionMap.get(regionKey);
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        destroyed = true;

        textureMap.values().forEach(Texture::destroy);

        textureMap = null;
        textureRegionMap = null;
    }
}