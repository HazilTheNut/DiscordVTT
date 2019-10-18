package com.discordvtt.Data;

/**
 * Created by Jared on 3/28/2018.
 */
public class LayerImportances {

    /*
     LayerImportances

     A static utility object that defines the importances of different types of layer

     Because all relevant layers source their importance number from this stuff, you can effectively shift around the importance of whole groups of layers.
     That's nice when you just want to make sure a certain layers squeezes in between two other groups

     Things are spaced into into 5's and such to conveniently allow for in-between importances

     */

    public static final int BACKDROP          = 0;
    public static final int TOKENS            = 5;
    public static final int HIDE              = 7;
    public static final int CURSOR            = 10;
}
