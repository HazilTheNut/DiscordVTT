package com.discordvtt.Editor.DrawTools;

import com.discordvtt.Data.Coordinate;
import com.discordvtt.Data.GameMap;
import com.discordvtt.Engine.Layer;
import com.discordvtt.Engine.LayerManager;
import com.discordvtt.Engine.SpecialText;

import java.awt.*;

public class TokenMove extends DrawTool {

    private Coordinate startLoc;
    private SpecialText chosenToken;
    private LayerManager lm;

    private final Color highlightColor = new Color(255, 150, 50, 100);

    public TokenMove(LayerManager layerManager){
        lm = layerManager;
    }

    @Override
    public void onDrawStart(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        startLoc = new Coordinate(col, row);
        chosenToken = gameMap.getTokenLayer().getSpecialText(col, row);
        highlight.editLayer(col - lm.getCameraPos().getX(), row - lm.getCameraPos().getY(), new SpecialText(' ', Color.WHITE, highlightColor));
    }

    @Override
    public void onDraw(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        highlight.clearLayer();
        Coordinate screenLoc = startLoc.subtract(lm.getCameraPos());
        //Draw measurement lines
        int xdir = (int)Math.signum(col - startLoc.getX());
        int ydir = (int)Math.signum(row - startLoc.getY());
        for (int i = 0; i <= Math.abs(col - startLoc.getX()); i++){
            int x = startLoc.getX() + (i * xdir);
            Color color = ((x + row) % 2 == 0) ? new Color(255, 255, 255, 100) : new Color(150, 150, 150, 100);
            highlight.editLayer(x - lm.getCameraPos().getX(), row - lm.getCameraPos().getY(), new SpecialText(' ', Color.WHITE, color));
        }
        for (int y = startLoc.getY() + ydir; y != row; y += ydir){
            Color color = ((startLoc.getX() + y) % 2 == 0) ? new Color(255, 255, 255, 100) : new Color(150, 150, 150, 100);
            highlight.editLayer(startLoc.getX() - lm.getCameraPos().getX(), y - lm.getCameraPos().getY(), new SpecialText(' ', Color.WHITE, color));
        }
        highlight.editLayer(screenLoc.getX(), screenLoc.getY(), new SpecialText(' ', Color.WHITE, highlightColor));
    }

    @Override
    public void onDrawEnd(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        gameMap.getTokenLayer().editLayer(startLoc, null);
        gameMap.getTokenLayer().editLayer(col, row, chosenToken);
        highlight.clearLayer();
    }

    @Override
    public void onCancel(Layer highlight, int col, int row) {
        highlight.clearLayer();
    }
}
