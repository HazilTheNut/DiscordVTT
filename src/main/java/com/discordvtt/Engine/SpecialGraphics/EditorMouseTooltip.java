package com.discordvtt.Engine.SpecialGraphics;

import com.discordvtt.Data.GameMap;
import com.discordvtt.Engine.SpecialText;
import com.discordvtt.Engine.ViewWindow;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jared on 3/24/2018.
 */
public class EditorMouseTooltip implements SpecialGraphics {

    /**
     * EditorMouseTooltip:
     *
     * The SpecialGraphics responsible for displaying the mouse tooltip for the Level Editor.
     */

    private GameMap gamemap;
    private ViewWindow window;
    private int mouseX;
    private int mouseY;
    private int dataX;
    private int dataY;

    public boolean showCoordinate = false;
    public boolean showAdvanced = false;

    public EditorMouseTooltip(GameMap GameMap, ViewWindow viewWindow) {
        gamemap = GameMap;
        window = viewWindow;
    }

    public void updateMousePosition(int x, int y, int dataX, int dataY){
        mouseX = x;
        mouseY = y;
        this.dataX = dataX;
        this.dataY = dataY;
    }

    @Override
    public void paint(Graphics g) {
        g.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        ArrayList<String> tooltipText = getTooltip();
        if (tooltipText.size() > 0) {
            int textWidth = computeTooltipWidth(tooltipText, g.getFontMetrics());
            int lineHeight = g.getFontMetrics().getHeight();
            int textHeight = g.getFontMetrics().getHeight() * tooltipText.size();
            g.setColor(new Color(50, 50, 50));
            int startX = mouseX + 5;
            startX = (startX + textWidth < window.getWidth()) ? startX : startX - textWidth; //Figures out if the tooltip should be moved to the other side of the cursor.
            int startY = mouseY + 15;
            g.drawRect(startX, startY, textWidth + 1, textHeight + 1); //Draw box outline
            g.setColor(new Color(40, 40, 40));
            g.fillRect(startX + 1, startY + 1, textWidth, textHeight); //Fill in box
            g.setColor(Color.WHITE);
            if (gamemap.getBackdrop().isLayerLocInvalid(dataX, dataY))
                g.setColor(new Color(150, 150, 150)); //Gray out if cursor outside of level bounds
            for (int ii = 0; ii < tooltipText.size(); ii++){
                g.drawString(tooltipText.get(ii), startX + 1, startY + (lineHeight * (ii+1)) - 1); //Draw the strings
            }
        }
    }

    //Gets the array of strings to be displayed for the tooltip
    private ArrayList<String> getTooltip(){
        ArrayList<String> output = new ArrayList<>(); //Create new array
        if (showCoordinate || showAdvanced)
            output.add(String.format("[ %1$d, %2$d ]", dataX, dataY)); //Display coordinate
        if (showAdvanced && !gamemap.getBackdrop().isLayerLocInvalid(dataX, dataY)){
            SpecialText backdropText = gamemap.getBackdrop().getSpecialText(dataX, dataY);
            if (backdropText != null) {
                output.add(backdropText.toString()); //Display backdrop SpecialText data.
            }
            SpecialText tokenLayerText = gamemap.getTokenLayer().getSpecialText(dataX, dataY);
            if (tokenLayerText != null) {
                output.add(tokenLayerText.toString()); //Display tokenLayer SpecialText data.
            }
        }
        return output;
    }

    private int computeTooltipWidth(ArrayList<String> strings, FontMetrics metrics){
        int max = 0;
        for (String str : strings) max = Math.max(max, metrics.stringWidth(str));
        return max;
    }
}
