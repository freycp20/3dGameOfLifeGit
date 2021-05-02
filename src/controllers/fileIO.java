package controllers;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class fileIO {
    private static Stage stage;
    private boolean trueFirst;
    boolean boardOpened;

    public guiBoard openFile(){
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
    protected guiBoard readFile(File file) {
        guiBoard board = null;
        try {
            ArrayList<Integer> aNeighbors = null;
            ArrayList<Integer> dNeighbors = null;


            FileInputStream readFile = new FileInputStream(file);
            Scanner scn = new Scanner(readFile);

            int xVal = scn.nextInt();
            int yVal = scn.nextInt();
            int zVal = scn.nextInt();
            boolean areRules = scn.nextBoolean();

            if (areRules){
                aNeighbors = new ArrayList<>();
                dNeighbors = new ArrayList<>();
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
            boolean[][][] cellArray = new boolean[yVal][xVal][zVal];
            for (int y = 0; y < yVal; y++) {
                for (int x = 0; x < xVal; x++) {
                    for (int z = 0; z < zVal; z++) {
                        cellArray[y][x][z] = scn.nextBoolean();
                    }
                }
            }
            readFile.close();
            board = new guiBoard(cellArray, aNeighbors, dNeighbors,
                    xVal, yVal, zVal, trueFirst, areRules);
        } catch (IOException ex) {
            Logger.getLogger(
                    fileIO.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
        return board;
    }
    public void saveFile(String content){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            saveTextToFile(content, file);
        }
    }
    protected void saveTextToFile(String content, File file) {
        try {
//            System.out.println("content = " + content);
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.write(content);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(fileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public boolean getBoardOpened(){
        return boardOpened;
    }
    public void setBoardOpened(boolean bdo) {boardOpened = bdo;}
}
