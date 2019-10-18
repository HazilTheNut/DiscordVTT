package com.discordvtt.Editor.Mapping;

import com.discordvtt.Data.FileIO;
import com.discordvtt.Data.GameMap;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MappingFilePanel extends JPanel {

    String previousFilePath;

    public MappingFilePanel(GameMap gameMap){

        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createTitledBorder("File"));

        JButton quickSaveButton = new JButton("Quick Save");
        quickSaveButton.addActionListener(e -> quickSaveMap(gameMap));

        JButton saveButton = new JButton("Save As");
        saveButton.addActionListener(e -> saveMapAs(gameMap));

        JButton openButton = new JButton("Open");
        openButton.addActionListener(e -> openMap(gameMap));

        add(quickSaveButton);
        add(saveButton);
        add(openButton);
    }

    private String getMapsFolder(FileIO io){ return io.getRootFilePath().concat("Maps");}

    private void saveMapAs(GameMap gameMap){
        FileIO io = new FileIO();
        String startPath = getMapsFolder(io);
        previousFilePath = io.serializeGameMap(gameMap, startPath);
    }

    private void quickSaveMap(GameMap gameMap){
        if (previousFilePath == null || previousFilePath.equals("")){
            saveMapAs(gameMap);
        } else {
            FileIO io = new FileIO();
            io.quickSerializeGameMap(gameMap, previousFilePath);
        }
    }

    private void openMap(GameMap gameMap){
        FileIO io = new FileIO();
        File mapFile;
        if (previousFilePath != null)
            mapFile = io.chooseGameMap(previousFilePath);
        else
            mapFile = io.chooseGameMap(getMapsFolder(io));
        if (mapFile != null) {
            previousFilePath = mapFile.getPath();
            GameMap loadedMap = io.openLevel(mapFile);
            if (loadedMap != null) {
                gameMap.setAllData(loadedMap);
            }
        }
    }
}
