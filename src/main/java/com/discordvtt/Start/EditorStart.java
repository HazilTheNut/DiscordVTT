package com.discordvtt.Start;

import com.discordvtt.Editor.EditorFrame;
import com.discordvtt.Data.GameMap;
import com.discordvtt.Editor.WindowWatcher;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jared on 2/18/2018.
 */
public class EditorStart {

    public void main (){

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        UIManager.put( "text", new Color( 230, 230, 230) );

        UIManager.getDefaults().put("Button.showMnemonics", true);

        GameMap gamemap = new GameMap();
        gamemap.initialize();

        WindowWatcher watcher = new WindowWatcher();

        new EditorFrame(gamemap, watcher);
    }
}
