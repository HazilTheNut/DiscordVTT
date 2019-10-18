package com.discordvtt.Start;

/**
 * Main class of both the SourceryText game and level editor.
 *
 * Command line options:
 *   help     shows these command line options
 *   editor   runs the level editor
 *   anything else, including nothing, runs the game
 *
 * Created by Riley on 1/14/2019.
 */

public class JarStart {

    public static void main (String[] args){
            EditorStart editor = new EditorStart();
            editor.main();
    }

}
