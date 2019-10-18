package com.discordvtt.Editor.DrawTools;

import com.discordvtt.Data.GameMap;
import com.discordvtt.Engine.Layer;
import com.discordvtt.Engine.LayerManager;
import com.discordvtt.Engine.SpecialText;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jared on 2/25/2018.
 */
public class VisionRectangle extends DrawTool {

    /**
     * ArtRectangle:
     *
     * A Tool that draws / fills a rectangle based on two corner points
     */

    private int startX;
    private int startY;

    private int previousX;
    private int previousY;

    private SpecialText startHighlight = new SpecialText(' ', Color.WHITE, new Color(255, 255, 79, 120));
    private SpecialText textToDraw;
    LayerManager lm;

    public VisionRectangle(LayerManager manager, SpecialText textToDraw) {
        lm = manager;
        this.textToDraw = textToDraw;
    }

    @Override
    public void onActivate(JPanel panel) {
        TOOL_TYPE = TYPE_ART;
    }

    @Override
    public void onDrawStart(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        startX = col;
        startY = row;
    }

    @Override
    public void onDraw(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        int xOffset = -lm.getCameraPos().getX() + gameMap.getHideLayer().getX(); //Mentioned in ArtLine, but the layers don't change its position, but JUST IN CASE.....
        int yOffset = -lm.getCameraPos().getY() + gameMap.getHideLayer().getY();
        drawRect(highlight, startX + xOffset, startY + yOffset, previousX + xOffset, previousY + yOffset, null); //Clear the previous rectangle
        drawRect(highlight, startX + xOffset, startY + yOffset, col + xOffset, row + yOffset, startHighlight); //Make a new one
        previousX = col;
        previousY = row;
        //System.out.println("[ArtLine] onDraw");
    }

    @Override
    public void onDrawEnd(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        highlight.clearLayer();
        drawRect(gameMap.getHideLayer(), startX, startY, col, row, textToDraw);
    }

    @Override
    public void onCancel(Layer highlight, int col, int row) {
        highlight.clearLayer();
    }

    private void drawRect(Layer layer, int x1, int y1, int x2, int y2, SpecialText text){
        int colSign = (x2 > x1) ? 1 : -1; //The two corners can be in any position in relation to each other, so that better be accounted for.
        int rowSign = (y2 > y1) ? 1 : -1;
        //Do the filled rectangle
        for (int col = x1; col*colSign <= x2*colSign; col+=colSign){
            for (int row = y1; row*rowSign <= y2*rowSign; row+=rowSign){
                layer.editLayer(col, row, text);
            }
        }
    }
}
