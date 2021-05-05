package controllers;


import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class consoleInput {

    Scanner console;
    Board gameBoard;
    boolean boardMade;
    ArrayList<Board> boards;
    int xdim;
    int ydim;
    int zdim;

    /**
     * constructor
     */
    public consoleInput() {
        boardMade = false;
        boards = new ArrayList<>();
    }

    /**
     * Handles user input and commands
     * @return the final board
     */
    public Board runConsole() {
        console = new Scanner(System.in);

        System.out.println("Enter a command (or 'help' to see a list of commands): ");

        while(console.hasNextLine()) {
            String command = console.nextLine();
            switch(command) {
                case "help":
                    printHelp();
                    break;
                case "new":
                    handleNew(console);
                    break;
                case "display":
                    display();
                    break;
                case "next":
                    next();
                    break;
                case "cycle":
                    printCycle();
                    break;
                case "run":
                    run(console);
                    break;
            }
            runConsole();

        }

        console.close();
        return gameBoard;
    }


    /**
     * runs the board through as many steps as the user specifies
     * @param console a scanner to get user input on how many steps the board should run through
     */
    private void run(Scanner console) {
        if (!boardMade) {
            System.out.println("Please create a board first");
            return;
        }
        System.out.println("How many steps would you like the board to run through? ");
        int numSteps=0;
        try {
            numSteps = Integer.parseInt(console.next());
        } catch (Exception e) {
            run(console);
        }
        for (int i=0;i<numSteps;i++) {
            next();
        }
    }


    /**
     * Tells the user a list of console commands
     */
    public void printHelp() {
        System.out.println("Commands:");
        System.out.println("'new' creates a new board from either an input file or by manually defining living cells");
        System.out.println("'display' prints out the current board the the console layer by layer");
        System.out.println("'next' moves the current board to the next step");
        System.out.println("'run' decided how many times you would like the board to run");
        System.out.println("'cycle' tells whether there is a cycle, and if so what the index where it begins to cycle is");
    }

    /**
     * creates a new board using an input file
     * @param userFile a file storing board data from the user
     */
    public void processFile(File userFile) {
        fileIO ifio = new fileIO();
        gameBoard = ifio.openFile();
    }


    /**
     * creates a new board using the users input either from a file or from individually placed coordinates of living cells
     * @param console scanner to get user inputs
     */
    public void handleNew(Scanner console) {
        System.out.println("Enter the dimensions of the board in the form 'x,y,z'");

        String[] size = console.nextLine().split(",");
        try {
            xdim = Integer.parseInt(size[0]);
            ydim = Integer.parseInt(size[1]);
            zdim = Integer.parseInt(size[2]);
        } catch (Exception e) {
            handleNew(console);
        }
        gameBoard = new Board(xdim,ydim,zdim);

        createNewBoard();

        boardMade = true;
    }

    /**
     * creates a new board based on the users input
     */
    public void createNewBoard() {
        System.out.println("Enter the name of a text file or enter coordinates for living cells in the form 'x,y,z' or enter done to stop");
        String input = console.next();
        if (input.contains(".txt")) {
            try {
                File userFile = new File(input);
                processFile(userFile);
            } catch (Exception e) {
                System.out.println("Invalid file name");
            }
        } else if (!input.equals("done")){
            int x;
            int y;
            int z;
            String[] coordinates = input.split(",");
            try {
                x = Integer.parseInt(coordinates[0])-1;
                y = Integer.parseInt(coordinates[1])-1;
                z = Integer.parseInt(coordinates[2])-1;
                gameBoard.editCell(x,y,z);
                createNewBoard();
            } catch (Exception e) {
                System.out.println("Invalid file name or coordinates");
                createNewBoard();
            }
        }
    }


    /**
     * prints out the current board to the console
     */
    public void display() {
        if (!boardMade) {
            System.out.println("Please create a board first");
            return;
        }
        String outString = "";
        int layerNum = 1;
        for (boolean[][] layer : gameBoard.cells) {
            outString += "Layer " + layerNum + "\n";
            for (boolean[] row : layer) {
                for (boolean cell : row) {
                    if (cell) {
                        outString += "X";
                    } else {
                        outString += ".";
                    }
                }
                outString += "\n";
            }
            layerNum++;
            outString += "\n\n";
        }

        System.out.println(outString);
    }

    /**
     * moves the current board to the next step
     */
    public void next() {
        if (!boardMade) {
            System.out.println("Please create a board first");
            return;
        }
        boards.add(gameBoard);
        gameBoard.nextStep();

    }

    /**
     * @return the index of a cycle in a series of boards if there is one
     */
    public int findCycle() {
        int cycleIndex = -1;
        for (Board board1 : boards) {
            if (cycleIndex==-1){
                for (Board board2 : boards) {
                    if (boards.indexOf(board2) != boards.indexOf(board1)) {
                        if (board1.isSame(board2)) {
                            if (boards.indexOf(board1) < boards.indexOf(board2)) {
                                cycleIndex = boards.indexOf(board2);
                            } else {
                                cycleIndex = boards.indexOf(board1);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return cycleIndex;
    }


    /**
     * prints to the user if there is a cycle or not
     */
    public void printCycle() {
        if (this.findCycle() != -1) {
            System.out.println("Cycle found on step " + this.findCycle());
        } else {
            System.out.println("No cycle found");
        }
    }
}