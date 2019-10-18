package com.discordvtt.Editor.Mapping;

import com.discordvtt.Data.GameMap;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Jared on 3/20/2018.
 */
public class UndoManager {

    /**
     * UndoManager:
     *
     * Stores a list of previous GameMap's and substitutes them into the current GameMap when called upon to do so.
     *
     * Calling 'undo' moves a pointer backwards through its list of GameMap's
     * Calling 'redo' moves that pointer forwards.
     */

    private ArrayList<GameMap> pastGameMap = new ArrayList<>();
    private GameMap currentGameMap;

    private JFrame editorFrame;

    private static final int MAX_UNDO_HISTORY = 200;
    private int historyPointer;

    public UndoManager(GameMap gamemap, JFrame editorFrame){
        currentGameMap = gamemap;
        this.editorFrame = editorFrame;
        System.out.printf("[UndoManager] Level history size: %1$d\n", pastGameMap.size());
        recordGameMap();
    }

    void recordGameMap(){
        for (int ii = pastGameMap.size()-1; ii > historyPointer; ii--){ //Get rid of the history ahead of the pointer, now on an older branch of the timeline.
            pastGameMap.remove(ii);
        }
        pastGameMap.add(currentGameMap.copy());
        if (pastGameMap.size() > MAX_UNDO_HISTORY)
            pastGameMap.remove(0); //Undo history housekeeping
        historyPointer = pastGameMap.size()-1;
        System.out.printf("[UndoManager.recordGameMap] Level history size: %1$d\n", pastGameMap.size());
        System.out.printf("[UndoManager.recordGameMap] Level history pointer: %1$d\n", historyPointer);
        addFrameAsterisk();
    }

    void doUndo(){
        historyPointer--;
        if (historyPointer < 0) historyPointer = 0;
        System.out.printf("[UndoManager.doUndo] Level history pointer: %1$d\n", historyPointer);
        GameMap pastData = pastGameMap.get(historyPointer);
        currentGameMap.setAllData(pastData);
        addFrameAsterisk();
    }

    void doRedo(){
        historyPointer++;
        if (historyPointer > pastGameMap.size()-1) historyPointer = pastGameMap.size()-1;
        System.out.printf("[UndoManager.doRedo] Level history pointer: %1$d\n", historyPointer);
        GameMap pastData = pastGameMap.get(historyPointer);
        currentGameMap.setAllData(pastData);
        addFrameAsterisk();
    }

    private void addFrameAsterisk(){
        if (editorFrame != null && !editorFrame.getTitle().contains("*")){
            editorFrame.setTitle(editorFrame.getTitle().concat("*"));
        }
    }
}
