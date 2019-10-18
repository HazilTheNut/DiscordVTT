package com.discordvtt.Editor.Mapping;

import com.discordvtt.Data.GameMap;
import com.discordvtt.Data.LayerImportances;
import com.discordvtt.Editor.DrawTools.*;
import com.discordvtt.Engine.Layer;
import com.discordvtt.Engine.LayerManager;
import com.discordvtt.Engine.SpecialGraphics.EditorLevelBoundGraphics;
import com.discordvtt.Engine.SpecialText;
import com.discordvtt.Engine.ViewWindow;

import javax.swing.*;
import java.awt.*;

public class GameMapEditorPane extends JPanel {

    private EditorMouseInput mouseInput;

    public GameMapEditorPane(GameMap gameMap, JTabbedPane owner){

        setLayout(new BorderLayout());
        setFocusable(true);

        //Create view window
        ViewWindow viewWindow = new ViewWindow();
        viewWindow.setFocusable(true);
        viewWindow.setEnabled(true);
        addComponentListener(viewWindow);

        viewWindow.addMouseListener(viewWindow);
        viewWindow.addKeyListener(viewWindow);
        owner.addKeyListener(viewWindow);

        SingleTextRenderer activeTextDisplay = new SingleTextRenderer(new SpecialText(' '));
        JPanel topToolbar = new JPanel();
        JPanel toolOptionsPanel = new JPanel();
        toolOptionsPanel.setLayout(new BorderLayout());
        toolOptionsPanel.setBorder(BorderFactory.createEmptyBorder());
        JLabel activeTextLabel = new JLabel(activeTextDisplay);
        DrawToolManager drawToolManager = new DrawToolManager(toolOptionsPanel, activeTextDisplay, activeTextLabel, topToolbar);

        //Create mousee highlight
        Layer mouseHighlight = new Layer(new SpecialText[viewWindow.RESOLUTION_WIDTH*4][viewWindow.RESOLUTION_HEIGHT*4], "mouse", 0, 0, LayerImportances.CURSOR);
        mouseHighlight.fixedScreenPos = true;

        //Send layers to LayerManager, which also needs to be instantiated.
        LayerManager manager = new LayerManager(viewWindow);
        manager.addLayer(gameMap.getBackdrop());
        manager.addLayer(gameMap.getTokenLayer());
        manager.addLayer(gameMap.getHideLayer());
        manager.addLayer(mouseHighlight);

        //Hook everything else up
        viewWindow.addSpecialGraphics(new EditorLevelBoundGraphics(viewWindow, manager, gameMap));
        UndoManager undoManager = new UndoManager(gameMap, null);
        mouseInput = new EditorMouseInput(viewWindow, manager, mouseHighlight, gameMap, undoManager, drawToolManager);
        viewWindow.addMouseListener(mouseInput);
        viewWindow.addMouseMotionListener(mouseInput);

        viewWindow.addKeyListener(drawToolManager);
        viewWindow.requestFocusInWindow();
        addKeyListener(drawToolManager);

        //Assemble user interface.

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEtchedBorder());
        centerPanel.add(viewWindow, BorderLayout.CENTER);
        centerPanel.addMouseListener(viewWindow);
        centerPanel.validate();

        add(centerPanel, BorderLayout.CENTER);

        topToolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));

        topToolbar.add(drawToolManager.generateToolButton(new ExpandRoom(manager), "Icons/expand.png", "Expand Area ; click outside bounds to expand size"));

        topToolbar.add(new JLabel(" | "));

        topToolbar.add(new LayerToggler(gameMap.getBackdrop(), "backdrop"));
        topToolbar.add(activeTextLabel);
        topToolbar.add(drawToolManager.generateToolButton(new ArtBrush(manager), "Icons/brush.png", "(Backdrop) Brush Tool"));
        topToolbar.add(drawToolManager.generateToolButton(new ArtLine(manager), "Icons/line.png", "(Backdrop) Line Tool"));
        topToolbar.add(drawToolManager.generateToolButton(new ArtRectangle(manager), "Icons/rectangle.png", "(Backdrop) Rectangle Tool"));
        topToolbar.add(drawToolManager.generateToolButton(new ArtFill(), "Icons/fill.png", "(Backdrop) Fill Tool"));
        topToolbar.add(toolOptionsPanel);

        topToolbar.add(new JLabel(" | "));

        topToolbar.add(new LayerToggler(gameMap.getTokenLayer(), "tokens"));
        topToolbar.add(drawToolManager.generateToolButton(new TokenPlace(), "Icons/tokenplace.png", "(Tokens) Place Token"));
        topToolbar.add(drawToolManager.generateToolButton(new TokenRemove(), "Icons/tokenremove.png", "(Tokens) Remove Token"));
        topToolbar.add(drawToolManager.generateToolButton(new TokenMove(manager), "Icons/tokenmove.png", "(Tokens) Move Token"));

        topToolbar.add(new JLabel(" | "));

        topToolbar.add(new LayerToggler(gameMap.getHideLayer(), "vision"));
        //topToolbar.add(drawToolManager.generateToolButton(new VisionToggleAll(), "Icons/toggleall.png", "(Vision) Toggle All"));
        JButton toggleButton = drawToolManager.generateGenericToolbarButton("Icons/toggleall.png", "(Vision) Toggle All");
        toggleButton.addActionListener(e -> toggleVisionLayer(gameMap));
        topToolbar.add(toggleButton); //We forgo using the Tools system since it is more intuitive to click the button to toggle everywhere.
        topToolbar.add(drawToolManager.generateToolButton(new VisionRectangle(manager, null), "Icons/showarea.png", "(Vision) Show Area"));
        topToolbar.add(drawToolManager.generateToolButton(new VisionRectangle(manager, new SpecialText(' ', Color.WHITE, MappingTheme.VISION_HIDE_COLOR)), "Icons/hidearea.png", "(Vision) Hide Area"));

        topToolbar.add(new JLabel(" | "));

        topToolbar.add(drawToolManager.generateToolButton(new RenderArea(manager), "Icons/camera.png", "Render Area ; renders a selected area to display on Discord."));

        add(topToolbar, BorderLayout.PAGE_START);

        JPanel bottomToolbar = new JPanel();
        bottomToolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));

        bottomToolbar.add(new EditorFindAndReplacePanel(gameMap, undoManager));
        bottomToolbar.add(Box.createHorizontalGlue());

        bottomToolbar.add(new MappingFilePanel(gameMap));

        add(bottomToolbar, BorderLayout.PAGE_END);
    }

    private void toggleVisionLayer(GameMap gameMap){
        boolean visibleSpaces = false;
        for (int c = 0; c < gameMap.getHideLayer().getCols(); c++) {
            for (int r = 0; r < gameMap.getHideLayer().getRows(); r++) {
                visibleSpaces |= (gameMap.getHideLayer().getSpecialText(c, r) == null);
                gameMap.getHideLayer().editLayer(c, r, new SpecialText(' ', Color.WHITE, MappingTheme.VISION_HIDE_COLOR));
            }
        }
        if (!visibleSpaces)
            gameMap.getHideLayer().clearLayer();
    }
}
