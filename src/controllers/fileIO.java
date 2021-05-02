package controllers;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
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
    private final Desktop desktop = Desktop.getDesktop();
    private boolean areRules;
    private boolean trueFirst;
    private boolean boardOpened;
    private int xVal;
    private int yVal;
    private int zVal;

    private boolean[][][] cellArray;

    public void openFile(){
        stage = new Stage();
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            readFile(file);
            boardOpened = true;
        }
    }
    protected guiBoard readFile(File file) {
        guiBoard board = null;
        try {
            ArrayList<Integer> aNeighbors = null;
            ArrayList<Integer> dNeighbors = null;


            FileInputStream readFile = new FileInputStream(file);
            Scanner scn = new Scanner(readFile);

            yVal = scn.nextInt();
            xVal = scn.nextInt();
            zVal = scn.nextInt();
            areRules = scn.nextBoolean();

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
            cellArray = new boolean[yVal][xVal][zVal];
            for (int y = 0; y < yVal; y++) {
                for (int x = 0; x < xVal; x++) {
                    for (int z = 0; z < zVal; z++) {
                        cellArray[y][x][z] = scn.nextBoolean();
                    }
                }
            }
            readFile.close();
            board = new guiBoard(cellArray, aNeighbors, dNeighbors,
                    xVal, yVal, zVal, trueFirst);
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

    public int getxVal() {
        return xVal;
    }
    public int getyVal() {
        return yVal;
    }
    public int getzVal() {
        return zVal;
    }
    public boolean areRules(){
        return areRules;
    }
//    public boolean getTrueFirst(){
//        return trueFirst;
//    }
//    public ArrayList<Integer> getRule2(){
//        return aNeighbors;
//    }
//    public ArrayList<Integer> getRule3(){
//        return dNeighbors;
//    }
//    public void setBoardOpened(boolean bdo){boardOpened = bdo;}
//    public boolean boardOpened(){return boardOpened;}
//    public boolean[][][] getCellArray(){
//        return cellArray;
//    }
}
