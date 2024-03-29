package com.discordvtt.Engine;

import com.discordvtt.Data.FileIO;
import com.discordvtt.Engine.SpecialGraphics.SpecialGraphics;
import com.discordvtt.Engine.SpecialGraphics.EditorMouseTooltip;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jared on 2/18/2018.
 */
public class ViewWindow extends JComponent implements ComponentListener, MouseInputListener, KeyListener, FocusListener{

    /**
     * ViewWindow:
     *
     * The central JComponent behind the display of SourceryText.
     *
     * It takes in a Layer as input and dumps out its contents onto the screen.
     * It is generally recommended to pair a ViewWindow with a LayerManager for a more organized approach to display.
     */

    private Layer drawnImage;

    public int HOR_SEPARATION = 9;
    public int VER_SEPARATION = 16;
    private int CHAR_SIZE = 15;
    public int HOR_MARGIN = 0;
    public int VER_MARGIN = 0;

    public int RESOLUTION_WIDTH  = 59;
    public int RESOLUTION_HEIGHT = 31;

    private ArrayList<SpecialGraphics> specialGraphicsList;

    public LayerManager manager;

    private Font calculatedFont = new Font("Monospaced", Font.PLAIN, 15);

    public ViewWindow() {
        recalculate();
        specialGraphicsList = new ArrayList<>();
        setFocusable(true);
    }

    void drawImage(Layer image){
        drawnImage = image;
        repaint();
    }

    public void recalculate() {
        double MAX_VER_HOR_SEPARATION_RATIO = 0.6;

        HOR_SEPARATION = (int)Math.floor((double)getWidth() / RESOLUTION_WIDTH);
        VER_SEPARATION = (int)Math.floor((double)getHeight() / RESOLUTION_HEIGHT);
        if (HOR_SEPARATION > VER_SEPARATION * MAX_VER_HOR_SEPARATION_RATIO) HOR_SEPARATION = (int)(VER_SEPARATION * MAX_VER_HOR_SEPARATION_RATIO);
        if (HOR_SEPARATION < VER_SEPARATION) CHAR_SIZE = HOR_SEPARATION;
        else CHAR_SIZE = VER_SEPARATION;

        int displayLength = HOR_SEPARATION * RESOLUTION_WIDTH;
        int displayHeight = VER_SEPARATION * RESOLUTION_HEIGHT;
        HOR_MARGIN = (getWidth() - displayLength) / 2;
        VER_MARGIN = (getHeight() - displayHeight) / 2;

        int fontSizeAdjustment = 4;
        Font newFont = generateFont(CHAR_SIZE + fontSizeAdjustment);
        if (newFont != null)
            calculatedFont = newFont;
    }

    private ArrayList<Font> generatedFonts = new ArrayList<>();

    private Font generateFont(int size){
        for (Font font : generatedFonts)
            if (font.getSize() == size) return font;
        FileIO io = new FileIO();
        try {
            Font newFont = Font.createFont(Font.TRUETYPE_FONT, new File(io.getRootFilePath() + "font.ttf")).deriveFont((float)size);
            generatedFonts.add(newFont);
            return newFont;
        } catch (FontFormatException | IOException ignored) {}
        Font newfont = new Font(Font.MONOSPACED, Font.PLAIN, size);
        generatedFonts.add(newfont);
        return newfont;
    }

    @Override
    public void paintComponent(Graphics g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight()); //Create base background

        g.setFont(calculatedFont);
        FontMetrics metrics = g.getFontMetrics();

        if (drawnImage != null) {
            for (int col = 0; col < RESOLUTION_WIDTH; col++) {
                for (int row = 0; row < RESOLUTION_HEIGHT; row++) {
                    SpecialText text = drawnImage.getSpecialText(col, row);
                    if (text != null) {
                        g.setColor(text.getBkgColor());
                        g.fillRect(col * HOR_SEPARATION + HOR_MARGIN, row * VER_SEPARATION + VER_MARGIN + 1, HOR_SEPARATION, VER_SEPARATION); //Fill background
                    }
                }
            }

            for (int col = 0; col < RESOLUTION_WIDTH; col++) {
                for (int row = 0; row < RESOLUTION_HEIGHT; row++) {
                    SpecialText text = drawnImage.getSpecialText(col, row);
                    if (text != null) {
                        g.setColor(text.getFgColor());
                        g.drawString(text.getStr(), col * HOR_SEPARATION + HOR_MARGIN + (HOR_SEPARATION - metrics.stringWidth(text.getStr())) / 2, VER_SEPARATION * row + VER_MARGIN + (int) (VER_SEPARATION * 0.75)); //Fill foreground (the text)
                    }
                }
            }
        }

        if (!isFocusOwner()) g.setColor(new Color(50, 50, 50)); //Draw margin borders
        else g.setColor(new Color(100, 100, 100));
        g.drawLine(HOR_MARGIN, 0, HOR_MARGIN, getHeight());
        g.drawLine(getWidth() - HOR_MARGIN, 0, getWidth() - HOR_MARGIN, getHeight());
        g.drawLine(0, VER_MARGIN, getWidth(), VER_MARGIN);
        g.drawLine(0, getHeight() - VER_MARGIN, getWidth(), getHeight() - VER_MARGIN);

        for (SpecialGraphics specialGraphics : specialGraphicsList) specialGraphics.paint(g);
    }

    public void addSpecialGraphics(SpecialGraphics graphics) { specialGraphicsList.add(graphics); }

    public void removeSpecialGraphics(SpecialGraphics graphics) { specialGraphicsList.remove(graphics); }

    @Override
    public void componentResized(ComponentEvent e) {
        recalculate();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    private boolean mouseClicking = false;
    private int previousXCharPos = 0;
    private int previousCharYPos = 0;

    @Override
    public void mouseDragged(MouseEvent e) {
        //for (MouseMotionListener listener : getMouseMotionListeners()) listener.mouseDragged(e);
        //System.out.println(String.format("Mouse Current: [%1$d,%2$d] Prev: [%3$d,%4$d]", getSnappedMouseX(e.getX()), getSnappedMouseY(e.getY()), previousXCharPos, previousCharYPos));
        manager.moveCameraPos(getSnappedMouseX(e.getX()) - previousXCharPos, getSnappedMouseY(e.getY()) - previousCharYPos);
        previousXCharPos = getSnappedMouseX(e.getX());
        previousCharYPos = getSnappedMouseY(e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //for (MouseMotionListener listener : getMouseMotionListeners()) listener.mouseMoved(e);
        //System.out.println(String.format("New Mouse Pos: %1$d,%2$d", e.getX(), e.getY()));
    }

    public int getSnappedMouseX(int mouseRawX) { return (mouseRawX - HOR_MARGIN) / HOR_SEPARATION; }

    public int getSnappedMouseY(int mouseRawY) { return (mouseRawY - VER_MARGIN) / VER_SEPARATION; }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP){
            manager.moveCameraPos(0,1);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN){
            manager.moveCameraPos(0,-1);
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT){
            manager.moveCameraPos(1,0);
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            manager.moveCameraPos(-1,0);
        }
        //System.out.println("Key Pressed");
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        //for (MouseListener listener : getMouseListeners()) listener.mousePressed(e);
        previousXCharPos = getSnappedMouseX(e.getX());
        previousCharYPos = getSnappedMouseY(e.getY());
        //System.out.println("Mouse clicked!");
        requestFocusInWindow();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //for (MouseListener listener : getMouseListeners()) listener.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void focusGained(FocusEvent e) {
        repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        repaint();
    }
}
