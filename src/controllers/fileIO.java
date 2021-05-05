package controllers;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class fileIO {
    private static Stage stage;
    private boolean trueFirst;
    boolean boardOpened;

    /**
     * displays file explorer
     * @return guiBoard or null
     */
    public Board openFile(){
        // create new stage and set scene to system file explorer
        stage = new Stage();
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            boardOpened = true;
            return readFile(file);
        } else {
            return null;
        }
    }

    /**
     * reads selected file and returns board with all values
     * @param file
     * @return guiBoard
     */
    protected Board readFile(File file) {
        Board board = null;
        try {
            LinkedHashSet<Integer> aNeighbors = null;
            LinkedHashSet<Integer> dNeighbors = null;
            Scanner scn = new Scanner(file);
            int xVal = scn.nextInt();
            int yVal = scn.nextInt();
            int zVal = scn.nextInt();
            boolean areRules = scn.nextBoolean();

            // checks if rules are saved
            if (areRules){
                aNeighbors = new LinkedHashSet<>();
                dNeighbors = new LinkedHashSet<>();
                trueFirst = scn.nextBoolean();
                int stop = scn.nextInt();
                for (int i = 0; i < stop; i++) {
                    aNeighbors.add(scn.nextInt());
                }
                stop = scn.nextInt();
                for (int i = 0; i < stop; i++) {
                    dNeighbors.add(scn.nextInt());
                }
            }
            // assign read values to boolean array
            boolean[][][] cellArray = new boolean[yVal][xVal][zVal];
            for (int y = 0; y < yVal; y++) {
                for (int x = 0; x < xVal; x++) {
                    for (int z = 0; z < zVal; z++) {
                        cellArray[y][x][z] = scn.nextBoolean();
                    }
                }
            }
            // set up new board with all given values
            board = new Board(cellArray, aNeighbors, dNeighbors,
                    xVal, yVal, zVal, trueFirst, areRules);
        } catch (IOException ex) {
            Logger.getLogger(
                    fileIO.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
        return board;
    }

    /**
     * open file explorer and allow user to make new text file where the size, rules, and board will be stored
     * @param content
     */
    public void saveFile(String content){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"); // presets file type to txt
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(stage); // set file explorer to file stage

        if (file != null) {
            saveTextToFile(content, file);
        }
    }

    /**
     * size, rules, and board into chosen text file
     * @param content
     * @param file
     */
    protected void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.write(content);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(fileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // return if board has been opened to avoid file system errors
    public boolean getBoardOpened(){
        return boardOpened;
    }
    public void setBoardOpened(boolean bdo) {boardOpened = bdo;}
}
