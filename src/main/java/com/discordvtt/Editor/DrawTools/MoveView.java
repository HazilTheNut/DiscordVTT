package com.discordvtt.Editor.DrawTools;

import com.discordvtt.Data.Coordinate;
import com.discordvtt.Data.GameMap;
import com.discordvtt.Engine.Layer;
import com.discordvtt.Engine.LayerManager;
import com.discordvtt.Engine.SpecialText;

public class MoveView extends DrawTool {

    private LayerManager lm;
    private Coordinate dragStartPos;
    private Coordinate originalCameraPos;

    public MoveView(LayerManager layerManager){
        lm = layerManager;
    }

    @Override
    public void onDrawStart(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        dragStartPos = lm.getCameraPos().subtract(new Coordinate(col, row)); //We have to work in screen coordinates so that moving the camera doesn't interfere with itself.
        originalCameraPos = lm.getCameraPos().copy();
    }

    @Override
    public void onDraw(GameMap gameMap, Layer highlight, int col, int row, SpecialText text) {
        Coordinate current = lm.getCameraPos().subtract(new Coordinate(col, row));
        lm.setCameraPos(originalCameraPos.getX() - (dragStartPos.getX() - current.getX()), originalCameraPos.getY() - (dragStartPos.getY() - current.getY()));
        System.out.printf("DrawStart: %1$s Current: %2$s Origin: %3$s\n", dragStartPos, new Coordinate(col, row), originalCameraPos);
    }
}
