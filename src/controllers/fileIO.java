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
    private int yVal;
    private int xVal;
    private int zVal;
    private boolean areRules;
    private boolean rule1;
    private boolean boardOpened;
    ArrayList<Integer> aNeighbors;
    ArrayList<Integer> dNeighbors;


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
    protected void readFile(File file) {
        try {
            FileInputStream readFile = new FileInputStream(file);
            Scanner scn = new Scanner(readFile);
            yVal = scn.nextInt();
            xVal = scn.nextInt();
            zVal = scn.nextInt();
            areRules = scn.nextBoolean();
            if (areRules){
                aNeighbors = new ArrayList<>();
                dNeighbors = new ArrayList<>();
                rule1 = scn.nextBoolean();
                int stop = scn.nextInt();
                System.out.println(stop);
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
//                        System.out.print(cellArray[y][x][z] + " ");
                    }
                }
            }
            readFile.close();
        } catch (IOException ex) {
            Logger.getLogger(
                    fileIO.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
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
    public int getY(){
        return yVal;
    }
    public int getX(){
        return xVal;
    }
    public int getZ(){
        return zVal;
    }
    public boolean areRules(){
        return areRules;
    }
    public boolean getRule1(){
        return rule1;
    }
    public ArrayList<Integer> getRule2(){
        return aNeighbors;
    }
    public ArrayList<Integer> getRule3(){
        return dNeighbors;
    }
    public boolean boardOpened(){return boardOpened;}
    public boolean[][][] getCellArray(){
        return cellArray;
    }
}
