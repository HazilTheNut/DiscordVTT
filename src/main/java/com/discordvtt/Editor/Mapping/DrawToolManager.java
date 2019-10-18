package com.discordvtt.Editor.Mapping;

import com.discordvtt.Data.FileIO;
import com.discordvtt.Editor.DrawTools.DrawTool;
import com.discordvtt.Engine.SpecialText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DrawToolManager implements KeyListener {

    private ToolButtonPair activeTool;
    private JPanel toolOptionsPanel;
    private SpecialText activeCharacter;
    private SingleTextRenderer renderer;
    private JLabel renderLabel;
    private JPanel toolbarPanel;

    public DrawToolManager(JPanel toolOptionsPanel, SingleTextRenderer renderer, JLabel renderLabel, JPanel toolbarPanel){
        this.toolOptionsPanel = toolOptionsPanel;
        this.renderer = renderer;
        this.renderLabel = renderLabel;
        this.toolbarPanel = toolbarPanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //System.out.println("[DrawToolManager] Input caught!");
        if (!e.isControlDown() && !e.isMetaDown()) {
            activeCharacter = new SpecialText(e.getKeyChar(), MappingTheme.BACKDROP_FONT_COLOR, new Color(0, 0, 0, 0));
            renderer.specText = new SpecialText(activeCharacter.getCharacter(), MappingTheme.BACKDROP_FONT_COLOR, Color.BLACK);
            renderLabel.repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public DrawTool getActiveTool(){
        if (activeTool == null) return null;
        return activeTool.drawTool;
    }

    public SpecialText getActiveCharacter() {
        return activeCharacter;
    }

    public JButton generateToolButton(DrawTool tool, String iconPath, String name){
        JButton btn = generateGenericToolbarButton(iconPath, name);
        ToolButtonPair pair = new ToolButtonPair(tool, btn);
        btn.addActionListener(e -> {
            //Deactivate previous tool
            if (activeTool != null) {
                activeTool.drawTool.onDeactivate(toolOptionsPanel);
                for (Component c : toolOptionsPanel.getComponents()) toolOptionsPanel.remove(c);
                activeTool.button.setEnabled(true);
            }
            //Activate new tool
            activeTool = pair;
            activeTool.drawTool.onActivate(toolOptionsPanel);
            toolOptionsPanel.validate();
            toolOptionsPanel.repaint();
            activeTool.button.setEnabled(false); //Disallows clicking a tool button twice and causing double-initialization, which can be troublesome.
            toolbarPanel.doLayout();
            toolbarPanel.revalidate();
            toolbarPanel.repaint();
        });
        return btn;
    }

    JButton generateGenericToolbarButton(String iconPath, String name){
        FileIO io = new FileIO();
        Icon icon = new ImageIcon(io.getRootFilePath().concat(iconPath));
        JButton btn = new JButton(icon);
        btn.setToolTipText(name);
        btn.setMargin(new Insets(5, 5, 5, 5));
        return btn;
    }

    private class ToolButtonPair{
        private DrawTool drawTool;
        private JButton button;
        private ToolButtonPair(DrawTool drawTool, JButton button){
            this.drawTool = drawTool;
            this.button = button;
        }
    }
}
