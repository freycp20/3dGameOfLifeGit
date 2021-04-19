package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Board {
    boolean[][][] cells = null;
    boolean[][][] startingPos = null;
    private int aliveNeighbors;
    private int deadNeighbors;
    private boolean falseFirst;

    private int size;

    public Board(int size) {
        this.size = size;
        cells = new boolean[size][size][size];
        startingPos = new boolean[size][size][size];
    }
    public void printCells() {
        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells.length; x++) {
                for (int z = 0; z < cells.length; z++) {
//                        System.out.println("maybe?");
                    System.out.print("cells[y][x][z] = " + cells[y][x][z] + " ");
                    if (cells[y][x][z]){

//                            System.out.print(cells[y][x][z] + " ");

                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }
    public void setAliveNeighbors(int aliveNeighbors) {
        this.aliveNeighbors = aliveNeighbors;
    }

    public void setDeadNeighbors(int deadNeighbors) {
        this.deadNeighbors = deadNeighbors;
    }

    public void setFalseFirst(boolean falseFirst) {
        this.falseFirst = falseFirst;
    }

    public Board(File file) {
        try {
            FileInputStream readFile = new FileInputStream(file);
            Scanner scn = new Scanner(readFile);
            size = scn.nextInt();
            scn.nextInt();
            scn.nextInt();
            cells = new boolean[size][size][size];
            startingPos = new boolean[size][size][size];

            System.out.println("here?");
            for (int y = 0; y < cells.length; y++) {
                for (int x = 0; x < cells.length; x++) {
                    for (int z = 0; z < cells.length; z++) {
//                        System.out.println("maybe?");
                        cells[y][x][z] = scn.nextBoolean();
                        if (cells[y][x][z]){

//                            System.out.print(cells[y][x][z] + " ");

                        }
                    }
                }
            }
            readFile.close();
        } catch (IOException ex) {
            System.out.println("uh oh problem");
        }
    }
    public void nextStep() {
        System.out.println("nextstep is getting called");
        boolean[][][] board = new boolean[size][size][size];
        int livingNeighbors = 0;
        for (int layer = 1; layer < board.length-1; layer++) {
            for (int row = 1; row < board[layer].length-1; row++) {
                for (int col = 1; col < board[layer][row].length-1; col++) {
                    livingNeighbors = Collections.frequency(surroundingCells(cells,layer,row,col), true);
//                    if (newBoard[layer][row][col]) {
//                        if (livingNeighbors == aliveNeighbors) {
//                            board[layer][row][col] = trueFirst;
//                        } else {
//                            board[layer][row][col] = !trueFirst;
//                        }
//                    } else if (livingNeighbors==deadNeighbors) {
//                        board[layer][row][col] = true;
//                    }
//                    System.out.println("cells[layer][row][col] = " + cells[layer][row][col]);
//                    System.out.println("aliveNeighbors = " + aliveNeighbors);
//                    System.out.println("deadNeighbors = " + deadNeighbors);

                    if (cells[layer][row][col]) {
                        System.out.println("made it here");
                        System.out.println("livingNeighbors = " + livingNeighbors);
                        if (falseFirst) {
                            if (livingNeighbors == aliveNeighbors) {
                                board[layer][row][col] = false;
                            } else {
                                board[layer][row][col] = true;
                            }
                        } else {
                            if (livingNeighbors == aliveNeighbors) {
                                board[layer][row][col] = true;
                            } else {
                                board[layer][row][col] = false;
                            }
                        }
                    } else if (livingNeighbors==deadNeighbors) {
                        board[layer][row][col] = true;
                    }
                }
            }
        }
//        System.out.println("woh");
        cells = board;
    }
    public ArrayList<Boolean> surroundingCells(boolean[][][] board, int layer, int row, int col) {
        final int NUM_NEIGHBORS = 27;
        ArrayList<Boolean> surrounding = new ArrayList<>(NUM_NEIGHBORS);
        for (boolean[][] i : new boolean[][][]{board[layer-1],board[layer],board[layer+1]}) {
            for (boolean[] j : new boolean[][]{i[row-1],i[row],i[row+1]}) {
                surrounding.add(j[col-1]);
                surrounding.add(j[col]);
                surrounding.add(j[col+1]);
            }
        }
        surrounding.remove(13);
        return surrounding;
    }

    public boolean[][][] getCells() {
        return cells;
    }

    public Board (boolean[][][] cellArray) {
        System.out.println("here");
        this.cells = new boolean[cellArray.length][cellArray.length][cellArray.length];
        this.startingPos = new boolean[cellArray.length][cellArray.length][cellArray.length];
        this.size = cellArray.length;
        for (int i = 0; i < cellArray.length; i++) {
            for (int j = 0; j < cellArray[i].length; j++) {
                for (int k = 0; k < cellArray[i][j].length; k++) {
                    this.cells[i][j][k] = cellArray[i][j][k];
                    this.startingPos[i][j][k] = cellArray[i][j][k];
                }
            }
        }
    }
    public void reset() {
        System.out.println("here");
        System.out.println(this.cells);
        System.out.println(this.startingPos);
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                for (int k = 0; k < cells[i][j].length; k++) {
//                    System.out.println("this.cells[i][j][k] = " + this.cells[i][j][k]);
//                    System.out.println("this.startingPos[i][j][k] = " + this.startingPos[i][j][k]);
                }
            }
        }
        this.cells = new Board(startingPos).getCells();
    }
}
