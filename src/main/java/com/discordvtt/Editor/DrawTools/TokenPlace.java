package com.discordvtt.Editor.DrawTools;

import com.discordvtt.Data.GameMap;
import com.discordvtt.Engine.Layer;
import com.discordvtt.Engine.SpecialText;

import java.awt.*;

public class TokenPlace extends DrawTool {

    @Override
    public void onDrawStart(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        SpecialText tokenText = new SpecialText(' ');
        if (!gameMap.getTokenLayer().isLayerLocInvalid(col, row)) tokenText = new SpecialText(text.getCharacter(), new Color(200, 225, 255), text.getBkgColor());
        gameMap.getTokenLayer().editLayer(col, row, tokenText);
    }
}
