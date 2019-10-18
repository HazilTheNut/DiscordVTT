package com.discordvtt.Editor.DrawTools;

import com.discordvtt.Data.Coordinate;
import com.discordvtt.Data.GameMap;
import com.discordvtt.Editor.Mapping.ExportWindow;
import com.discordvtt.Engine.Layer;
import com.discordvtt.Engine.LayerManager;
import com.discordvtt.Engine.SpecialText;

import java.awt.*;

public class RenderArea extends DrawTool {

    private final int VIEW_WIDTH = 51;
    private final int VIEW_HEIGHT = 27;

    private LayerManager lm;
    private ExportWindow exportWindow;

    public RenderArea(LayerManager layerManager){
        lm = layerManager;
    }

    private void drawViewRectangle(Coordinate corner, GameMap gameMap, Layer highlightLayer){
        Coordinate offset = corner.add(gameMap.getBackdrop().getPos().subtract(lm.getCameraPos()));
        Coordinate viewBounds = new Coordinate(VIEW_WIDTH, VIEW_HEIGHT).add(new Coordinate(-1, -1));
        highlightLayer.fillLayer(new SpecialText(' ', Color.WHITE, new Color(150, 255, 150, 100)), offset, offset.add(viewBounds));
    }

    @Override
    public void onDraw(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        highlight.clearLayer();
        Coordinate offset = new Coordinate(-1 * VIEW_WIDTH / 2, -1 * VIEW_HEIGHT / 2);
        Coordinate origin = offset.add(new Coordinate(col, row));
        drawViewRectangle(origin, gameMap, highlight);
    }

    @Override
    public void onDrawStart(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        Coordinate offset = new Coordinate(-1 * VIEW_WIDTH / 2, -1 * VIEW_HEIGHT / 2);
        Coordinate origin = offset.add(new Coordinate(col, row));
        drawViewRectangle(origin, gameMap, highlight);
    }

    @Override
    public void onDrawEnd(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        highlight.clearLayer();
        Coordinate offset = new Coordinate(-1 * VIEW_WIDTH / 2, -1 * VIEW_HEIGHT / 2);
        Coordinate origin = offset.add(new Coordinate(col, row));
        if (exportWindow == null)
            exportWindow = new ExportWindow(gameMap, origin, new Coordinate(VIEW_WIDTH, VIEW_HEIGHT));
        else {
            exportWindow.updateRender(gameMap, origin, new Coordinate(VIEW_WIDTH, VIEW_HEIGHT));
            exportWindow.setVisible(true);
        }
    }

    @Override
    public void onCancel(Layer highlight, int col, int row) {
        highlight.clearLayer();
    }
}
