package com.discordvtt.Editor.DrawTools;

import com.discordvtt.Data.GameMap;
import com.discordvtt.Engine.Layer;
import com.discordvtt.Engine.LayerManager;
import com.discordvtt.Engine.SpecialText;

import javax.swing.*;

/**
 * Created by Jared on 3/4/2018.
 */
public class ExpandRoom extends DrawTool {

    /**
     * ExpandRoom:
     *
     * The Tool that expands the size of a Level, allowing levels to get really big.
     */

    private LayerManager lm;

    public ExpandRoom(LayerManager manager){
        lm = manager;
    }

    @Override
    public void onActivate(JPanel panel) {
        TOOL_TYPE = TYPE_TILE;
    }

    @Override
    public void onDrawStart(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        //System.out.println("Draw pos: " + col + "," + row);
        //System.out.println("Layer dim: " + layer.getCols() + "x"  + layer.getRows());
        int newWidth = (col < 0) ? gameMap.getBackdrop().getCols() - col: Math.max(col+1, gameMap.getBackdrop().getCols());
        int newHeight = (row < 0) ? gameMap.getBackdrop().getRows() - row: Math.max(row+1, gameMap.getBackdrop().getRows());
        //System.out.println("New dim: " + newWidth + "x"  + newHeight);
        int offsetX = (col < 0) ? -1 * col : 0;
        int offsetY = (row < 0) ? -1 * row : 0;
        //System.out.println("Offset: x+" + offsetX + ", y+"  + offsetY);
        gameMap.resize(newWidth, newHeight, offsetX, offsetY); //This one line of code encapsulates a much, much larger algorithm that ExpandRoom doesn't need to care about. How nice.
        if (col < 0)
            lm.moveCameraPos(-col, 0); //Moves the camera to appear as if the level moved instead of you.
        if (row < 0)
            lm.moveCameraPos(0, -row); //It's all a lie!
    }
}
