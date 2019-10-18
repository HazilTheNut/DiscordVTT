package com.discordvtt.Data;

import com.discordvtt.Engine.Layer;

import java.io.Serializable;

public class GameMap implements Serializable {

    private static final long serialVersionUID = SerializationVersion.SERIALIZATION_VERSION;

    private Layer backdrop;
    private Layer tokenLayer;
    private Layer hideLayer;

    public Layer getBackdrop() {
        return backdrop;
    }

    public Layer getTokenLayer() {
        return tokenLayer;
    }

    public Layer getHideLayer() { return hideLayer; }

    public GameMap copy(){
        GameMap copy = new GameMap();
        copy.backdrop = backdrop.copy();
        copy.tokenLayer = tokenLayer.copy();
        copy.hideLayer = hideLayer.copy();
        return copy;
    }

    public void setAllData(GameMap other){
        backdrop.transpose(other.getBackdrop());
        tokenLayer.transpose(other.getTokenLayer());
        hideLayer.transpose(other.getHideLayer());
    }

    public void initialize(int width, int height){
        backdrop = new Layer(width, height, "backdrop", 0, 0, LayerImportances.BACKDROP);
        tokenLayer = new Layer(width, height, "tokenLayer", 0, 0, LayerImportances.TOKENS);
        hideLayer = new Layer(width, height, "hide", 0, 0, LayerImportances.HIDE);
    }

    public void initialize() { initialize(51, 27); }

    public void resize(int newWidth, int newHeight, int offsetX, int offsetY){
        //Due to pointer-y things, the following workaround must exist:
        GameMap newMap = new GameMap(); //We create a new GameMap that this GameMap will eventually take on its properties
        newMap.initialize(newWidth, newHeight); //We have to do this since the layer in the LayerManager doesn't change if we write over a new object onto this object's fields
        Coordinate offset = new Coordinate(offsetX, offsetY);
        //System.out.printf("[GameMap.resize] Offset: %1$s\n", offset);
        //After making a new-sized GameMap, we now insert this GameMap's layers into it
        newMap.getBackdrop().insert(backdrop, offset);
        newMap.getTokenLayer().insert(tokenLayer, offset);
        newMap.getHideLayer().insert(hideLayer, offset);
        //Lastly, now that we have built our new layers, we make this GameMap take on those layers as its own.
        backdrop.transpose(newMap.getBackdrop());
        tokenLayer.transpose(newMap.getTokenLayer());
        hideLayer.transpose(newMap.getHideLayer());
    }
}
