package com.discordvtt.Editor.DrawTools;

import com.discordvtt.Data.GameMap;
import com.discordvtt.Engine.Layer;
import com.discordvtt.Engine.SpecialText;

public class TokenRemove extends DrawTool {

    @Override
    public void onDrawStart(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        gameMap.getTokenLayer().editLayer(col, row, null);
    }
}
