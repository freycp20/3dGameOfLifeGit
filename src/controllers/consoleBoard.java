package controllers;//package sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class consoleBoard {
    boolean[][][] cells = null;
    boolean[][][] cellsWithBorder = null;
    private int x;
    private int y;
    private int z;
    private int size;

    /**
     * Creates a new board with the given dimensions
     * @param xdim the x dimension of the board
     * @param ydim the y dimension of the board
     * @param zdim the z dimension of the board
     */
    public consoleBoard(int xdim, int ydim, int zdim) {
        this.x = xdim;
        this.y = ydim;
        this.z = zdim;
        cells = new boolean[x][y][z];

    }

    /**
     * Creates a cubic board with sides of a given length
     * @param size the length of each side of the cube
     */
    public consoleBoard(int size) {
        this.size = size;
        x = size;
        y = size;
        z = size;
        cells = new boolean[size][size][size];
    }

    /**
     *
     * @param cellArray
     */
    public consoleBoard (boolean[][][] cellArray) {
        this.cells = new boolean[cellArray.length][cellArray.length][cellArray.length];
        for (int i = 0; i < cellArray.length; i++) {
            for (int j = 0; j < cellArray[i].length; j++) {
                for (int k = 0; k < cellArray[i][j].length; k++) {
                    this.cells[i][j][k] = cellArray[i][j][k];
                }
            }
        }
    }

    /**
     * Constructs a new board based on a given input file
     * @param file A file given by the user containing board template
     */
    public consoleBoard(File file) {
        try {
            FileInputStream readFile = new FileInputStream(file);
            Scanner scn = new Scanner(readFile);
            size = scn.nextInt();
            scn.nextInt();
            scn.nextInt();
            cells = new boolean[size][size][size];
            for (int y = 0; y < cells.length; y++) {
                for (int x = 0; x < cells.length; x++) {
                    for (int z = 0; z < cells.length; z++) {
                        cells[y][x][z] = scn.nextBoolean();
                    }
                }
            }
            readFile.close();
        } catch (IOException ex) {
            System.out.println("An error occurred");
        }

    }

    /**
     * moves the gameboard to the next step based on the given rules
     */
    public void nextStep() {
        boolean[][][] board = addBorder();
        int livingNeighbors = 0;
        for (int layer = 1; layer < board.length-1; layer++) {
            for (int row = 1; row < board[layer].length-1; row++) {
                for (int col = 1; col < board[layer][row].length-1; col++) {
                    livingNeighbors = Collections.frequency(surroundingCells(board,layer,row,col), true);
                    if (board[layer][row][col]) {
                        if (livingNeighbors == 1) {
                            board[layer][row][col] = true;
                        } else {
                            board[layer][row][col] = false;
                        }
                    } else if (livingNeighbors==1) {
                        board[layer][row][col] = true;
                    }
                }
            }
        }
        cells = removeBorder(board);
    }

    /**
     * Finds all cells surrounding a cell at a given location
     * @param board the current board
     * @param layer the layer the desired cell is in
     * @param row the row the desired cell is in
     * @param col the column the desired cell is in
     * @return a boolean array of the surrounding cells
     */
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

    /**
     *
     * @return
     */
    public boolean[][][] getCells() {
        return cells;
    }





    /**
     * makes the cell at a given set of coordinates alive
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public void editCell(int x, int y, int z) {
        cells[x][y][z] = true;
    }

    /**
     * compares if two boards are equivalent or not
     * @param other the board to be compared to
     * @return whether the two boards are the same or different
     */
    public boolean isSame(consoleBoard other) {
        return (this.toString().equals(other.toString()));
    }

    public boolean[][][] addBorder() {
        x = cells.length;
        y = cells[0].length;
        z = cells[0][0].length;
        cellsWithBorder = new boolean[x+2][y+2][z+2];
        for (int i = 1; i < y+1; i++) {
            for (int j = 1; j < x+1; j++) {
                for (int k = 1; k < z+1; k++) {
                    cellsWithBorder[i][j][k] = cells[i-1][j-1][k-1];
                }
            }
        }
        return cellsWithBorder;
    }

    /**
     * removes the dead border from a board
     * @param board The board with a dead border around it
     * @return the new board without any border around it
     */
    public boolean[][][] removeBorder(boolean[][][] board) {
        boolean[][][] borderLess = new boolean[board.length-2][board[0].length-2][board[0][0].length-2];
        for (int i=1; i<board.length-1; i++) {
            for (int j=1; j<board[0].length-1; j++) {
                for (int k=1; k<board[0][0].length-1; k++ ) {
                    borderLess[i-1][j-1][k-1] = board[i][j][k];
                }
            }
        }
        return borderLess;
    }
}
