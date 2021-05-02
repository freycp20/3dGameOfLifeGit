package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class guiBoard {
    boolean[][][] cells = null;
    boolean[][][] startingPos = null;
    private int aliveNeighbors;
    private int deadNeighbors;
    private ArrayList<Integer> aliveNlist;
    private ArrayList<Integer> deadNlist;
    private boolean trueFirst;
    private int xVal;
    private int yVal;
    private int zVal;
    private int size;

    public guiBoard(boolean[][][] cellArray,
                    ArrayList<Integer> aliveNlist,
                    ArrayList<Integer> deadNList,
                    int xVal, int yVal, int zVal,
                    boolean trueFirst) {

        this.cells = new boolean[cellArray.length][cellArray.length][cellArray.length];
        this.startingPos = new boolean[cellArray.length][cellArray.length][cellArray.length];
        this.size = cellArray.length;
        if (aliveNlist == null) {
            aliveNlist = new ArrayList<>(Arrays.asList(5, 6, 7));
        } else {
            this.aliveNlist = aliveNlist;
        }
        if (deadNlist == null) {
            deadNlist = new ArrayList<>(Arrays.asList(6));
        } else {
            this.deadNlist = deadNList;
        }
        this.xVal = xVal;
        this.yVal = yVal;
        this.zVal = zVal;
        this.trueFirst = trueFirst;
        for (int i = 0; i < cellArray.length; i++) {
            for (int j = 0; j < cellArray[i].length; j++) {
                for (int k = 0; k < cellArray[i][j].length; k++) {
                    this.cells[i][j][k] = cellArray[i][j][k];
                    this.startingPos[i][j][k] = cellArray[i][j][k];
                }
            }
        }
    }

    public void printCells() {
        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells.length; x++) {
                for (int z = 0; z < cells.length; z++) {
                    if (cells[y][x][z]) {
                        System.out.print(cells[y][x][z] + " ");
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public void nextStep() {
        boolean[][][] board = new boolean[size][size][size];
        int livingNeighbors = 0;

        for (int layer = 1; layer < board.length - 1; layer++) {
            for (int row = 1; row < board[layer].length - 1; row++) {
                for (int col = 1; col < board[layer][row].length - 1; col++) {
                    livingNeighbors = Collections.frequency(surroundingCells(cells, layer, row, col), true);
                    if (cells[layer][row][col]) {
                        if (aliveNlist.contains(livingNeighbors)) {
                            board[layer][row][col] = trueFirst;
                        } else {
                            board[layer][row][col] = !trueFirst;
                        }

                    } else if (deadNlist.contains(livingNeighbors)) {
                        board[layer][row][col] = true;
                    }
                }
            }
        }
        cells = board;
    }

    public ArrayList<Boolean> surroundingCells(boolean[][][] board, int layer, int row, int col) {
        final int NUM_NEIGHBORS = 27;
        ArrayList<Boolean> surrounding = new ArrayList<>(NUM_NEIGHBORS);
        for (boolean[][] i : new boolean[][][]{board[layer - 1], board[layer], board[layer + 1]}) {
            for (boolean[] j : new boolean[][]{i[row - 1], i[row], i[row + 1]}) {
                surrounding.add(j[col - 1]);
                surrounding.add(j[col]);
                surrounding.add(j[col + 1]);
            }
        }
        surrounding.remove(13);
        return surrounding;
    }

    public void reset() {
        this.cells = new guiBoard(startingPos).getCells();
    }

    public String arrayToString(boolean[][][] arr) {
        String cellString = "";
        for (boolean[][] booleans : arr) {
            for (boolean[] aBoolean : booleans) {
                for (boolean b : aBoolean) {
                    cellString += b + " ";
                }
                cellString += "\n";
            }
            cellString += "\n";
        }
        return cellString;
    }

    public boolean getTrueFirst() {
        return trueFirst;
    }
    public ArrayList<Integer> getAliveNlist() {
        return aliveNlist;
    }
    public ArrayList<Integer> getDeadNlist() {
        return deadNlist;
    }
    public void setAliveNeighbors(int aliveNeighbors) {
        this.aliveNeighbors = aliveNeighbors;
    }
    public void setDeadNeighbors(int deadNeighbors) {
        this.deadNeighbors = deadNeighbors;
    }
    public void setTrueFirst(boolean trueFirst) {
        this.trueFirst = trueFirst;
    }
    public boolean[][][] getCells() {
        return cells;
    }
    public boolean[][][] getStartingPos() {
        return startingPos;
    }
    public void setAliveNlist(ArrayList<Integer> aliveNlist) {
        this.aliveNlist.clear();
        this.aliveNlist = aliveNlist;
//        System.out.println("Given: " + aliveNlist.toString() + "\nTaken: " + this.aliveNlist.toString());
    }
    public void setDeadNlist(ArrayList<Integer> deadNlist) {
        this.deadNlist.clear();
        this.deadNlist = deadNlist;
//        System.out.println("Given: " + deadNlist.toString() + "\nTaken: " + this.deadNlist.toString());
    }
    public void setxVal(int xVal) {
        this.xVal = xVal;
    }
    public void setyVal(int yVal) {
        this.yVal = yVal;
    }
    public void setzVal(int zVal) {
        this.zVal = zVal;
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
}