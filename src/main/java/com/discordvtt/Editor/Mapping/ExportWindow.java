package com.discordvtt.Editor.Mapping;

import com.discordvtt.Data.Coordinate;
import com.discordvtt.Data.GameMap;
import com.discordvtt.Engine.Layer;
import com.discordvtt.Engine.SpecialText;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemListener;

public class ExportWindow extends JFrame {

    private JTextArea outputArea;
    private JCheckBox cboxIncludeDots;
    private JCheckBox cboxIncludeRuler;

    private Coordinate renderOrigin;
    private Coordinate renderViewbounds;

    public ExportWindow(GameMap gameMap, Coordinate origin, Coordinate viewBounds){
        setSize(new Dimension(300, 300));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());

        outputArea = new JTextArea();

        JScrollPane scrollPane = new JScrollPane(outputArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        Container c = getContentPane();
        c.add(scrollPane, BorderLayout.CENTER);

        cboxIncludeDots = new JCheckBox("Include Dots");
        cboxIncludeDots.setSelected(true);
        ItemListener updateListener = e -> updateRender(gameMap, renderOrigin, renderViewbounds);
        cboxIncludeDots.addItemListener(updateListener);

        cboxIncludeRuler = new JCheckBox("Include Ruler");
        cboxIncludeRuler.setSelected(true);
        cboxIncludeRuler.addItemListener(updateListener);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));

        topPanel.add(cboxIncludeDots);
        topPanel.add(cboxIncludeRuler);

        topPanel.add(Box.createHorizontalGlue());

        JButton copyButton = new JButton("Copy");
        copyButton.addActionListener(e -> {
            String text = outputArea.getText();
            StringSelection selection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        });
        topPanel.add(copyButton);

        topPanel.validate();

        c.add(topPanel, BorderLayout.PAGE_START);

        c.validate();

        updateRender(gameMap, origin, viewBounds);

        setTitle("Export Render");
        setVisible(true);
    }

    public void updateRender(GameMap gameMap, Coordinate origin, Coordinate viewBounds){
        renderOrigin = origin;
        renderViewbounds = viewBounds;
        outputArea.setText(renderToDiscordFriendly(gameMap, renderOrigin, renderViewbounds));
    }

    private String renderToDiscordFriendly(GameMap gameMap, Coordinate origin, Coordinate viewBounds){
        Layer bkg = drawRenderBackground(viewBounds);
        bkg = drawMapOntoBackground(bkg, gameMap, origin, viewBounds);
        //Transcribe to text
        StringBuilder builder = new StringBuilder("```java\n");
        for (int row = 0; row < bkg.getRows(); row++) {
            for (int col = 0; col < bkg.getCols(); col++) {
                SpecialText specialText = bkg.getSpecialText(col, row);
                if (specialText != null)
                    builder.append(specialText.getCharacter());
                else
                    builder.append(' ');
            }
            builder.append("\n");
        }
        builder.append("```");
        return builder.toString();
    }

    private Layer drawRenderBackground(Coordinate viewBounds){
        char[] chars = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
        boolean rulerEnabled = cboxIncludeRuler.isSelected();
        int hor_margin = (rulerEnabled) ? 2 + 1 : 0;
        int ver_margin = (rulerEnabled) ? 1 + 1 : 0;
        int layerWidth = (rulerEnabled) ? viewBounds.getX() + hor_margin + 1 : viewBounds.getX() + hor_margin;
        int layerHeight = (rulerEnabled) ? viewBounds.getY() + ver_margin + 1 : viewBounds.getY() + ver_margin;
        Layer bkg = new Layer(layerWidth, layerHeight, "", 0,0,0);
        Coordinate TLCorner = new Coordinate(hor_margin - 1, ver_margin - 1);
        Coordinate BRCorner = new Coordinate(viewBounds.getX() + hor_margin, viewBounds.getY() + ver_margin);
        Coordinate TRCorner = new Coordinate(BRCorner.getX(), TLCorner.getY());
        Coordinate BLCorner = new Coordinate(TLCorner.getX(), BRCorner.getY());
        if (rulerEnabled) {
            //Draw horizontal coordinates
            for (int x = 0; x < viewBounds.getX(); x++) {
                bkg.editLayer(x + hor_margin, 0, chars[x % chars.length]);
            }
            //Draw vertical coordinates
            for (int y = 0; y < viewBounds.getY(); y++) {
                bkg.inscribeString(String.valueOf(y), 0, y + ver_margin);
            }
            //Draw box
            bkg.editLayer(TLCorner, new SpecialText('+'));
            bkg.editLayer(TRCorner, new SpecialText('+'));
            bkg.editLayer(BLCorner, new SpecialText('+'));
            bkg.editLayer(BRCorner, new SpecialText('+'));
            bkg.fillLayer(new SpecialText('|'), TLCorner.add(new Coordinate(0, 1)), BLCorner.add(new Coordinate(0, -1)));
            bkg.fillLayer(new SpecialText('|'), TRCorner.add(new Coordinate(0, 1)), BRCorner.add(new Coordinate(0, -1)));
            bkg.fillLayer(new SpecialText('-'), TLCorner.add(new Coordinate(1, 0)), TRCorner.add(new Coordinate(-1, 0)));
            bkg.fillLayer(new SpecialText('-'), BLCorner.add(new Coordinate(1, 0)), BRCorner.add(new Coordinate(-1, 0)));
        }
        //Draw dots
        if (!cboxIncludeDots.isSelected()) return bkg;
        int x = hor_margin + 1;
        while (x < bkg.getCols()){
            bkg.fillLayer(new SpecialText('.'), new Coordinate(x, TLCorner.getY() + 1), new Coordinate(x, BLCorner.getY() - 1));
            x += 6;
        }
        int y = ver_margin + 1;
        while (y < bkg.getCols()){
            bkg.fillLayer(new SpecialText('.'), new Coordinate(TLCorner.getX() + 1, y), new Coordinate(TRCorner.getX() - 1, y));
            y += 6;
        }
        return bkg;
    }

    private Layer drawMapOntoBackground(Layer bkg, GameMap gameMap, Coordinate origin, Coordinate viewBounds){
        //Coordinate renderMarginOffset = new Coordinate(2 + 1 + 2, 1 + 1 + 2);
        Coordinate renderMarginOffset = new Coordinate(2 + 1, 1 + 1);
        for (int x = 0; x < viewBounds.getX(); x++) {
            for (int y = 0; y < viewBounds.getY(); y++) {
                Coordinate renderPos = new Coordinate(x, y);
                Coordinate mapLoc = origin.add(renderPos);
                if (!gameMap.getHideLayer().getVisible() || gameMap.getHideLayer().getSpecialText(mapLoc) == null){ //The Vision layer filters out hidden sections of the level
                    SpecialText token = (gameMap.getTokenLayer().getVisible()) ? gameMap.getTokenLayer().getSpecialText(mapLoc) : null;
                    if (token != null) bkg.editLayer(renderPos.add(renderMarginOffset), token);
                    else {
                        SpecialText art = (gameMap.getBackdrop().getVisible()) ? gameMap.getBackdrop().getSpecialText(mapLoc) : null; //I find this variable name amusing.
                        if (specialTextOpaque(art)) bkg.editLayer(renderPos.add(renderMarginOffset), art);
                    }
                }
            }
        }
        return bkg;
    }

    private boolean specialTextOpaque(SpecialText specialText){
        return specialText != null && (specialText.getCharacter() != ' ' || specialText.getBkgColor().getAlpha() > 0);
    }
}
