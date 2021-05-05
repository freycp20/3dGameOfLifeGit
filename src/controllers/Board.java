package controllers;

import java.util.*;

public class Board {

    // variables regarding the current board and its rules
    boolean[][][] cells;
    boolean[][][] cellsWithBorder = null;
    boolean[][][] startingPos = null;
    private LinkedHashSet<Integer> aliveNlist;
    private LinkedHashSet<Integer> deadNlist;
    private boolean trueFirst;
    private boolean areRules;

    // Size of the board
    private int xVal;
    private int yVal;
    private int zVal;
    private int size;

    /**
     * Initializes the board class
     * @param cellArray the 3d array of values
     * @param aliveNlist the list containing the alive rules
     * @param deadNlist the list containing the dead rules
     * @param xVal the size of the x dimension
     * @param yVal the size of the y dimension
     * @param zVal the size of the z dimension
     * @param trueFirst the value of the trueFirst Rule
     * @param areRules holds if there are given rules or not
     */
    public Board(boolean[][][] cellArray,
                 LinkedHashSet<Integer> aliveNlist,
                 LinkedHashSet<Integer> deadNlist,
                 int xVal, int yVal, int zVal,
                 boolean trueFirst, boolean areRules) {
        this.areRules = areRules;
        this.cells = new boolean[cellArray.length][cellArray.length][cellArray.length];
        this.startingPos = new boolean[cellArray.length][cellArray.length][cellArray.length];
        this.size = cellArray.length;
        this.aliveNlist = Objects.requireNonNullElseGet(aliveNlist, () -> new LinkedHashSet<>(Arrays.asList(5, 6, 7)));
        this.deadNlist = Objects.requireNonNullElseGet(deadNlist, () -> new LinkedHashSet<>(Collections.singletonList(6)));
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

    /**
     * Constructor based off size
     * @param xdim
     * @param ydim
     * @param zdim
     */
    public Board(int xdim, int ydim, int zdim) {
        this.xVal = xdim;
        this.yVal = ydim;
        this.zVal = zdim;
        cells = new boolean[xdim][ydim][zdim];

    }

    public void editCell(int x, int y, int z) {
        cells[x][y][z] = true;
    }

    /**
     * Checks if the current board is the same as another
     * @param other the other board being checked
     * @return true or false depending on result of the check
     */
    public boolean isSame(Board other) {
        return (this.toString().equals(other.toString()));
    }

    /**
     * method for debugging, prints out the current board
     */
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

    /**
     * Handles the logic for determining the next generation of the current board
     */
    public void nextStep() {
        // Creates a read only board and a mutable board with a border
        boolean[][][] boardReadOnly = addBorder(cells);
        boolean[][][] mutableBoard = addBorder(cells);
        int livingNeighbors;

        for (int layer = 1; layer < boardReadOnly.length - 1; layer++) {
            for (int row = 1; row < boardReadOnly[layer].length - 1; row++) {
                for (int col = 1; col < boardReadOnly[layer][row].length - 1; col++) {
                    livingNeighbors = Collections.frequency(surroundingCells(boardReadOnly, layer, row, col), true);
                    if (boardReadOnly[layer][row][col]) {
                        if (aliveNlist.contains(livingNeighbors)) {
                            mutableBoard[layer][row][col] = trueFirst;
                        } else {
                            mutableBoard[layer][row][col] = !trueFirst;
                        }
                    } else if (deadNlist.contains(livingNeighbors)) {
                        mutableBoard[layer][row][col] = true;
                    }
                }
            }
        }
        cells = removeBorder(mutableBoard);
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
     * Resets the board to the original generation
     */
    public void reset() {
        this.cells = new Board(startingPos,aliveNlist, deadNlist, xVal, yVal, zVal, trueFirst, areRules).getCells();
    }

    /**
     * Returns a string version of the board
     * @param arr the board to be converted to a string
     * @return the string version
     */
    public String arrayToString(boolean[][][] arr) {
        StringBuilder cellString = new StringBuilder();
        for (boolean[][] booleans : arr) {
            for (boolean[] aBoolean : booleans) {
                for (boolean b : aBoolean) {
                    cellString.append(b).append(" ");
                }
                cellString.append("\n");
            }
            cellString.append("\n");
        }
        return cellString.toString();
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
    public boolean[][][] addBorder(boolean[][][] cells) {
        int x = cells.length;
        int y = cells[0].length;
        int z = cells[0][0].length;
        cellsWithBorder = new boolean[x+2][y+2][z+2];

        for (int i = 0; i < x+2; i++) {
            for (int j = 0; j < y+2; j++) {
                for (int k = 0; k < z+2; k++) {
                    if (i == 0 || j == 0 || k == 0 || i == x+1 || j == y+1 || k==z+1) {
                        cellsWithBorder[i][j][k] = false;
                    } else {
                        cellsWithBorder[i][j][k] = cells[i-1][j-1][k-1];
                    }
                }
            }
        }
//        for (int i = 1; i < x+1; i++) {
//            for (int j = 1; j < y+1; j++) {
//                for (int k = 1; k < z+1; k++) {
//                    cellsWithBorder[i][j][k] = cells[i-1][j-1][k-1];
//                }
//            }
//        }
        return cellsWithBorder;
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