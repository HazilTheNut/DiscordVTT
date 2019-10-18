package com.discordvtt.Editor;

import com.discordvtt.Data.FileIO;
import com.discordvtt.Data.GameMap;
import com.discordvtt.Editor.Mapping.GameMapEditorPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;

/**
 * Created by Jared on 2/18/2018.
 */
public class EditorFrame extends JFrame {

    public EditorFrame(GameMap gamemap, WindowWatcher watcher){

        Container c = getContentPane();

        setLayout(new BorderLayout());

        JTabbedPane mainPane = new JTabbedPane();
        mainPane.addTab("Mapping Tool", new GameMapEditorPane(gamemap, mainPane));
        mainPane.setFocusable(true);

        c.add(mainPane);

        c.validate();

        setSize(new Dimension(850, 790));

        setTitle("DnDiscord - Discord Dungeons and Dragons Gameplay Assistant");
        FileIO io = new FileIO();
        ImageIcon icon = new ImageIcon(io.getRootFilePath().concat("Icons/icon.png"));
        setIconImage(icon.getImage());

        setVisible(true);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        watcher.update(1);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {watcher.update(-1);}
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('c'), "out");
        getRootPane().getActionMap().put("out", new Action() {
            @Override
            public Object getValue(String key) {
                return null;
            }

            @Override
            public void putValue(String key, Object value) {

            }

            @Override
            public void setEnabled(boolean b) {

            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void addPropertyChangeListener(PropertyChangeListener listener) {

            }

            @Override
            public void removePropertyChangeListener(PropertyChangeListener listener) {

            }

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("[EditorFrame] Input test");
            }
        });
    }

}
