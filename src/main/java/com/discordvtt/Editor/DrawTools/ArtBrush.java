package com.discordvtt.Editor.DrawTools;

import com.discordvtt.Data.Coordinate;
import com.discordvtt.Data.GameMap;
import com.discordvtt.Engine.Layer;
import com.discordvtt.Engine.LayerManager;
import com.discordvtt.Engine.SpecialText;

/**
 * Created by Jared on 2/25/2018.
 */
public class ArtBrush extends ArtLine {

    /**
     * ArtBrush:
     *
     * The 'basic' art tool.
     *
     * It inherits the ArtLine tool in order to handle fast mouse movements. It uses ArtLine's line-drawing method to fill in any gaps.
     */

    public ArtBrush(LayerManager manager) {
        super(manager);
        name = "Brush Tool";
        label = "Size: ";
    }

    private Coordinate prevPos;

    @Override
    public void onDraw(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        drawLine(gameMap.getBackdrop(), col, row, prevPos.getX(), prevPos.getY(), text);
        prevPos = new Coordinate(col, row);
    }

    @Override
    public void onDrawStart(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        prevPos = new Coordinate(col, row);
        drawBrush(gameMap.getBackdrop(), col, row, text);
    }

    @Override
    public void onDrawEnd(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) { }
}
