package com.discordvtt.Editor.Mapping;

import com.discordvtt.Data.GameMap;
import com.discordvtt.Editor.DrawTools.DrawTool;
import com.discordvtt.Engine.Layer;
import com.discordvtt.Engine.LayerManager;
import com.discordvtt.Engine.SpecialGraphics.EditorMouseTooltip;
import com.discordvtt.Engine.SpecialText;
import com.discordvtt.Engine.ViewWindow;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Created by Jared on 2/24/2018.
 */
public class EditorMouseInput implements MouseInputListener, MouseWheelListener{

    /**
     * EditorMouseInput:
     *
     * The object responsible for handling input for the Level Editor.
     *
     * The input functionality is not as refined as GameMouseInput, but instead trades that out for a more specialized application to the LevelEditor
     */

    private ViewWindow window;
    private LayerManager manager;
    private Layer highlightLayer;
    private EditorMouseTooltip cursorTooltip;

    private UndoManager undoManager;

    private Layer backdropLayer;

    private DrawToolManager drawToolManager;

    private GameMap gamemap;

    private boolean movingCamera = false;
    private boolean drawing = false;

    public EditorMouseInput(ViewWindow viewWindow, LayerManager layerManager, Layer highlight, GameMap GameMap, UndoManager undoManager, DrawToolManager drawToolManager){
        window = viewWindow;
        manager = layerManager;
        highlightLayer = highlight;
        originalResolutionWidth = window.RESOLUTION_WIDTH;
        originalResolutionHeight = window.RESOLUTION_HEIGHT;
        gamemap = GameMap;
        backdropLayer = gamemap.getBackdrop();
        this.undoManager = undoManager;
        cursorTooltip = new EditorMouseTooltip(GameMap, window);
        this.drawToolManager = drawToolManager;
        window.addSpecialGraphics(cursorTooltip);
    }

    private int getLayerMousePosX(int mouseX){
        return window.getSnappedMouseX(mouseX) + manager.getCameraPos().getX() - backdropLayer.getX();
    }

    private int getLayerMousePosY(int mouseY){
        return window.getSnappedMouseY(mouseY) + manager.getCameraPos().getY() - backdropLayer.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        DrawTool drawTool = drawToolManager.getActiveTool();
        if (e.getButton() == MouseEvent.BUTTON3) { //If right-click
            if (drawing){ //Cancels drawing if that was what was going on.
                drawTool.onCancel(highlightLayer, getLayerMousePosX(e.getX()), getLayerMousePosY(e.getY()));
                drawing = false;
            } else { //Otherwise, move the camera
                previousCharXPos = window.getSnappedMouseX(e.getX());
                previousCharYPos = window.getSnappedMouseY(e.getY());
                movingCamera = true;
                highlightLayer.editLayer(window.getSnappedMouseX(e.getX()), window.getSnappedMouseY(e.getY()), null);
            }
        } else if (e.getButton() == MouseEvent.BUTTON1 && !movingCamera && drawTool != null){ //Left-click starts drawing
            drawTool.onDrawStart(gamemap, highlightLayer, getLayerMousePosX(e.getX()), getLayerMousePosY(e.getY()), drawToolManager.getActiveCharacter());
            drawing = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        DrawTool drawTool = drawToolManager.getActiveTool();
        if (drawing && e.getButton() == MouseEvent.BUTTON1 && drawTool != null) { //Should tell the DrawTool to end
            drawTool.onDrawEnd(gamemap, highlightLayer, getLayerMousePosX(e.getX()), getLayerMousePosY(e.getY()), drawToolManager.getActiveCharacter());
            undoManager.recordGameMap(); //That must have done something, so better get the UndoManager to record that.
        }
        movingCamera = false;
        drawing = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private int previousCharXPos = 0;
    private int previousCharYPos = 0;

    @Override
    public void mouseDragged(MouseEvent e) {
        DrawTool drawTool = drawToolManager.getActiveTool();
        if (movingCamera) { //Do some moving camera business, if in camera-moving-mode
            manager.moveCameraPos(previousCharXPos - window.getSnappedMouseX(e.getX()), previousCharYPos - window.getSnappedMouseY(e.getY()));
            previousCharXPos = window.getSnappedMouseX(e.getX());
            previousCharYPos = window.getSnappedMouseY(e.getY());
        } else if (drawing){ //If drawing, tell the DrawTool that you are drawing.
            updateMouseCursorPos(e.getX(), e.getY());
            drawTool.onDraw(gamemap, highlightLayer, getLayerMousePosX(e.getX()), getLayerMousePosY(e.getY()), drawToolManager.getActiveCharacter());
        } else {
            updateMouseCursorPos(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //The mouse moved, so it's probably good to update the visuals on a couple of things to stay consistent with the mouse position.
        updateMouseCursorPos(e.getX(), e.getY());
        cursorTooltip.updateMousePosition(e.getX(), e.getY(), getLayerMousePosX(e.getX()), getLayerMousePosY(e.getY()));
    }

    //Relocates the position of the mouse cursor and changes color depended on whether the cursor was out of bounds.
    private void updateMouseCursorPos(int rawX, int rawY){
        highlightLayer.editLayer(previousCharXPos, previousCharYPos, null);
        if (!backdropLayer.isLayerLocInvalid(window.getSnappedMouseX(rawX) + manager.getCameraPos().getX() - backdropLayer.getX(), window.getSnappedMouseY(rawY) + manager.getCameraPos().getY() - backdropLayer.getY()))
            highlightLayer.editLayer(window.getSnappedMouseX(rawX), window.getSnappedMouseY(rawY), new SpecialText(' ', Color.WHITE, new Color(255, 255, 255, 120)));
        else
            highlightLayer.editLayer(window.getSnappedMouseX(rawX), window.getSnappedMouseY(rawY), new SpecialText(' ', Color.WHITE, new Color(255, 255, 255, 40)));
        previousCharXPos = window.getSnappedMouseX(rawX);
        previousCharYPos = window.getSnappedMouseY(rawY);
    }

    public void toggleCoordinateDisplay(){
        cursorTooltip.showCoordinate = !cursorTooltip.showCoordinate;
    }

    public void toggleAdvancedDisplay(){
        cursorTooltip.showAdvanced = !cursorTooltip.showAdvanced;
    }

    private int originalResolutionWidth;
    private int originalResolutionHeight;
    int zoomAmount = 100;

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //System.out.println(zoomAmount);
        zoomAmount += e.getPreciseWheelRotation() * -10;
        updateZoom();
        updateMouseCursorPos(e.getX(), e.getY());
    }

    public void updateZoom(){
        if (zoomAmount < 20) zoomAmount = 20;
        if (zoomAmount > 200) zoomAmount = 200;
        int prevWidth = window.RESOLUTION_WIDTH;
        int prevHeight = window.RESOLUTION_HEIGHT;
        window.RESOLUTION_WIDTH = (int)(originalResolutionWidth / ((float)zoomAmount / 100));
        window.RESOLUTION_HEIGHT = (int)(originalResolutionHeight / ((float)zoomAmount / 100));
        manager.moveCameraPos((prevWidth - window.RESOLUTION_WIDTH)/2, (prevHeight - window.RESOLUTION_HEIGHT)/2);
        window.recalculate();
    }
}
