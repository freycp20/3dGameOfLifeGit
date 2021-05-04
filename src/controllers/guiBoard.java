package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

public class guiBoard {
    boolean[][][] cells = null;
    boolean[][][] startingPos = null;
    private LinkedHashSet<Integer> aliveNlist;
    private LinkedHashSet<Integer> deadNlist;
    private boolean trueFirst;
    private int xVal;
    private int yVal;
    private int zVal;
    private int size;
    private boolean areRules;

    public guiBoard(boolean[][][] cellArray,
                    LinkedHashSet<Integer> aliveNlist,
                    LinkedHashSet<Integer> deadNlist,
                    int xVal, int yVal, int zVal,
                    boolean trueFirst, boolean areRules) {
        this.areRules = areRules;
        this.cells = new boolean[cellArray.length][cellArray.length][cellArray.length];
        this.startingPos = new boolean[cellArray.length][cellArray.length][cellArray.length];
        this.size = cellArray.length;
        if (aliveNlist == null) {
            this.aliveNlist = new LinkedHashSet<>(Arrays.asList(5, 6, 7));
        } else {
            this.aliveNlist = aliveNlist;
        }
        if (deadNlist == null) {
            this.deadNlist = new LinkedHashSet<>(Arrays.asList(6));
        } else {
            this.deadNlist = deadNlist;
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
        this.cells = new guiBoard(startingPos,aliveNlist, deadNlist, xVal, yVal, zVal, trueFirst, areRules).getCells();
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
    public boolean areRules(){
        return areRules;
    }
    public boolean getTrueFirst() {
        return trueFirst;
    }
    public LinkedHashSet<Integer> getAliveNlist() {
        return aliveNlist;
    }
    public LinkedHashSet<Integer> getDeadNlist() {
        return deadNlist;
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
    public void setAliveNlist(LinkedHashSet<Integer> aliveNlist) {
        this.aliveNlist = aliveNlist;
    }
    public void setDeadNlist(LinkedHashSet<Integer> deadNlist) {
        this.deadNlist = deadNlist;
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