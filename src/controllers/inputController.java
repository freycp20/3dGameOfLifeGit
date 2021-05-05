package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;


public class inputController {
    public Button upLayer = null;
    public Button downLayer = null;
    public Slider slider = null;
    public TextField x = null;
    public TextField y = null;
    public TextField z = null;
    public Label layerNumLabel = null;
    public Button visualize = null;
    public BorderPane mainBorderPane = null;
    public CheckMenuItem showAxis = null;
    public VBox buttonVbox = null;
    public MenuBar mainMenuBar = null;
    public Label sizeLabel = null;
    public Slider weightSlider = null;
    public CustomMenuItem customSliderWeight = null;
    protected int layerCount;
    protected int xVal;
    protected int yVal;
    protected int zVal;
    private int oldSliderVal;
    private boolean[][][] alive;
    private String aliveString = "";
    GridPane gridPane;
    ArrayList<GridPane> layers = new ArrayList<>();
    private boolean ctrlPressed = false;
    private boolean altPressed = false;
    private boolean axisShown = false;
    private StackPane stackPane;
    private Line axisY;
    private Line axisX;
    private boolean newAxisLayer = true;
    private HashMap<Integer, Rectangle> layerMap;
    private int mapCount;
    public boolean areRules;
    public boolean trueFirst;
    public LinkedHashSet<Integer> aNeighbors;
    public LinkedHashSet<Integer> dNeighbors;
    private int randWeight;

//     sceneBuilder methods and any non-general helper methods specific to common node types
//     These are the various buttons, sliders, menu-items, and any other nodes throughout input

    /**
     * sets various node values as they could not be set through scenebuilder
     * sets slider incrementation to block ten, sets to ticks, and sets major tick units
     * calls axis validation to check for input errors
     * calls handlers to set up necessary listeners
     * sets up welcome message
     */
    @FXML
    public void initialize() {
        customSliderWeight.setContent(weightSlider);
        customSliderWeight.setHideOnClick(false);
        weightSlider.setValue(50);
        randWeight = 50;
        layerCount = 1;
        slider.setBlockIncrement(10);
        slider.setShowTickMarks(true);
        slider.setMinorTickCount(1);
        slider.setMajorTickUnit(2);
        validateAxis(x);
        validateAxis(y);
        validateAxis(z);
        handlers();
        VBox vbox = new VBox();
        Label label = new Label("Welcome");
        label.setStyle("-fx-font: 24 arial;");
        vbox.getChildren().add(label);
        ZoomableScrollPane zScroll = new ZoomableScrollPane(vbox);
        zScroll.setStyle("-fx-background-color: #2c2c2c");
        mainBorderPane.setCenter(zScroll);
    }

    /**
     * switches from input to output and passes all the various rules, boolean vals, sizes,
     * and whatever board that may have been created.
     **/
    @FXML
    public void switchSceneC() throws IOException {
        // get fxml file and separate loader from Parent to utilize both. Combined line is commented below
        // Parent loader = FXMLLoader.load(getClass().getResource("/resources/output.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/output.fxml"));
        Parent outputRoot = loader.load();
        // set new scene to output
        Scene scene = new Scene(outputRoot,mainBorderPane.getWidth(),mainBorderPane.getHeight());
        scene.getStylesheets().add("/resources/outputStyle.css");
        // get current stage by passing through mainBoarderPane (though the node is arbitrary), and set scene to stage
        Stage output = (Stage) mainBorderPane.getScene().getWindow();
        output.setScene(scene);
        output.show();
        // title is set after showing stage should the program lag and set title before actually visually switching the scene
        output.setTitle("Output");
        outputController outputC = loader.getController();
        outputC.mainBorderPane.requestFocus();
        // if Axis is filled, all rules, sizes, and board values are passed to output
        if (checkAxisFilledBase()){
            saveC();
            outputC.init(this.xVal*10, alive, xVal, yVal, zVal);
            outputC.yVal = yVal;
            outputC.xVal = xVal;
            outputC.zVal = zVal;
            outputC.boardMade = true;
            if (areRules){
                outputC.areRules = true;
                outputC.trueFirst = trueFirst;
                outputC.aNeighbors = aNeighbors;
                outputC.dNeighbors = dNeighbors;
                outputC.booleanBox.setText(String.valueOf(trueFirst));
                outputC.deadNeighbor.setText(String.valueOf(dNeighbors));
                outputC.aliveNeighbor.setText(String.valueOf(aNeighbors));
            }
        }
    }

    /**
     * button that calls switchSceneC
     */
    @FXML
    public void visualizeC() throws IOException {
        switchSceneC();
    }

    /**
     * checks if axis is filled and saves all board values to boolean array alive.
     */
    @FXML
    public void saveC() {
        aliveString = ""; // don't add to already made alive
        for (int y = 0; y < yVal; y++) {
            for (int i = 0; i < zVal; i++) {
                for (int j = i; j < xVal * zVal; j += zVal) {
                    // System.out.println(layers.get(y).getChildren().get(j).getId().equals("1") + " = " + y + "," + j/zVal + "," + j%xVal);
                    // manipulate layers data to come in yxz form. It is in yxz so we can read in the layers first and because I wasn't thinking at the time of coding
                    alive[y][j / zVal][j % xVal] = layers.get(y).getChildren().get(j).getId().equals("1"); // y = y | x = j/xVal | z = j%zVal
                    aliveString += alive[y][j / zVal][j % xVal] + " ";
                }
                aliveString += "\n";
            }
            aliveString += "\n";
        }
    }

    /**
     * goes up one layer relative to current layer, sets all necessary values to past and current board rectangles
     */
    @FXML
    public void upLayerC() {
        // yVal comparisons keeps user from breaking things. checkAxisFilledBase would also work.
        if (layerCount != yVal && yVal != 0) {
            // maintain layerCount for text shown and slider vals
            layerCount++;
            layerNumLabel.setText("Layer #: " + layerCount);
            slider.setValue(layerCount);
            // set opacity of past layers
            for (int i = 0; i < (layerCount) - 1; i++) {
                GridPane layer = layers.get(i);
                layer.setOpacity(layer.getOpacity() * 0.5);
            }
            layers.get(layerCount - 1).setVisible(true);
            layers.get(layerCount - 1).setOpacity(1);
        }
    }

    /**
     * goes down one layer relative to current layer, sets all necessary values to past and current board rectangles
     */
    @FXML
    public void downLayerC() {
        // bodiless if statement maintains layerCount should the user try and break it
        if (slider.getValue() == 0); // this makes things work don't delete it
        else if (layerCount != 1) {
            layerCount--;
            layerNumLabel.setText("Layer #: " + layerCount);
            slider.setValue(layerCount);
            layers.get((layerCount - 1)).setVisible(true);
            layers.get((layerCount - 1)).setOpacity(1);
            for (int i = 0; i < (layerCount) - 1; i++) {
                GridPane layer = layers.get(i);
                layer.setOpacity(layer.getOpacity() * 2);
            }
            layers.get(layerCount).setVisible(false);
        }
    }

    /**
     * get old slider value at exact time the slider was clicked
     */
    @FXML
    public void sliderPressed() {
        oldSliderVal = (int) slider.getValue();
    }

    /**
     * set the new slider val at the exact time the slider was released
     * get difference between old and new slider vals, compare to 0, and run upL, or downL accordingly
     */
    @FXML
    public void sliderC() {
        int newSliderVal = (int) slider.getValue();
        layerNumLabel.setText("Layer #: " + newSliderVal);
        int sliderDiff = oldSliderVal - newSliderVal;
        if (sliderDiff < 0) {
            for (int i = 0; i < (sliderDiff * -1); i++) {
                upLayerC();
            }
        } else if (sliderDiff > 0) {
            for (int i = 0; i < sliderDiff; i++) {
                downLayerC();
            }
        }
    }

    /**
     * gets and clears current layer in layers layerMap
     */
    @FXML
    public void clearLayerC() {
        if (checkAxisFilledBase()) {
            int mapCount = 0;
            for (int x = 0; x < xVal; x++) {
                for (int z = 0; z < zVal; z++) {
                    layerMap.get((layerCount-1)*(xVal*zVal)+mapCount).setFill(Color.TRANSPARENT);
                    layerMap.get((layerCount-1)*(xVal*zVal)+mapCount).setId("0");
                    mapCount++;
                }
            }
        }
        handlers();
    }

    /**
     * gets and fills current layer in layers layerMap
     */
    @FXML
    public void fillLayerC(){
        if (checkAxisFilledBase()) {
            int mapCount = 0;
            for (int x = 0; x < xVal; x++) {
                for (int z = 0; z < zVal; z++) {
                    layerMap.get((layerCount-1)*(xVal*zVal)+mapCount).setFill(Color.SILVER);
                    layerMap.get((layerCount-1)*(xVal*zVal)+mapCount).setId("1");
                    mapCount++;
                }
            }
        }
        handlers();
    }

    /**
     * clears all layers from board by calling clearLayerC yVal number of times
     */
    @FXML
    public void clearAllLayersC() {
        int stop = layerCount;
        layerCount = 1;
        for (int i = 0; i < yVal; i++) {
            clearLayerC();
            layerCount++;
        }
        layerCount = stop;
        handlers();
    }

    /**
     * checks if values are valid and sets xVal to textField input.
     * Checks if axis filled using checkAxisFilled, which will display board to user
     */
    @FXML
    public void xC() {
        if (!(x.getText().equals(""))) {
            xVal = Integer.parseInt(x.getText());
            checkValidRange(x);
            checkAxisFilled();

        }
    }

    /**
     * checks if values are valid and sets yVal to textField input
     * sets slider val and position to current layer
     * sets slider max and min to yVal max and one respectively
     * Checks if axis filled using checkAxisFilled, which will display board to user
     */
    @FXML
    public void yC() {
        if (!(y.getText().equals(""))) {
            yVal = Integer.parseInt(y.getText());
            layerNumLabel.setText("Layer #: 1");
            slider.setMax(yVal);
            slider.setMin(1);
            checkValidRange(y);
            checkAxisFilled();
        }
    }

    /**
     * checks if values are valid and sets zVak to textField input.
     * Checks if axis filled using checkAxisFilled, which will display board to user
     */
    @FXML
    public void zC() {
        if (!(z.getText().equals(""))) {
            zVal = Integer.parseInt(z.getText());
            checkValidRange(z);
            checkAxisFilled();
        }
    }

    /**
     * makes new instance if fileIO
     * sets received boardValues to sizes, textValues, and rules
     * calls xC, yC, and zC to properly set up slider
     * displays opened board
     */
    @FXML
    public void openTemplateC() {
        fileIO ifio = new fileIO();
        guiBoard board = ifio.openFile();
        if (ifio.getBoardOpened()) {
            yVal = board.getyVal();
            xVal = board.getxVal();
            zVal = board.getzVal();
            y.setText(String.valueOf(yVal));
            x.setText(String.valueOf(xVal));
            z.setText(String.valueOf(zVal));
            xC();
            yC();
            zC();
            if (board.areRules()) {
                // set rules so they can pass to output should the user visualize opened board
                areRules = board.areRules();
                trueFirst = board.getTrueFirst();
                aNeighbors = board.getAliveNlist();
                dNeighbors = board.getDeadNlist();
            }
            // use set layer to display board
            setLayer(board.getStartingPos());
        }
    }

    /**
     * calls saveC and runs sizes and board through fileIO which will save it in a new text file
     */
    @FXML
    public void saveTemplateC() {
        saveC();
        // alive string is boolean array in string form. The seemingly random false lets the file system know that there are no rules
        String content = String.format("%d %d %d %b\n%s", yVal, xVal, zVal, false, aliveString);
        new fileIO().saveFile(content);
    }

    /**
     * a toggleable menu-item that displays axis on the board
     */
    @FXML
    public void showAxisC() {
        // axisShown allows for showAxis to be toggleable
        axisShown = !axisShown;
        // newAxisLayer allows for axis to be seen regardless of current layer
        if (newAxisLayer) {
            // setting axis length, width, color, and visibility
            axisY = new Line(stackPane.getWidth() / 2, stackPane.getHeight(), stackPane.getWidth() / 2, 2);
            axisX = new Line(0, stackPane.getHeight() / 2, stackPane.getWidth() - 2, stackPane.getHeight() / 2);
            axisY.setStrokeWidth(2.5);
            axisX.setStrokeWidth(2.5);
            axisY.setFill(Color.LIGHTGREY);
            axisX.setFill(Color.LIGHTGREY);
            axisY.setVisible(axisShown);
            axisX.setVisible(axisShown);
            stackPane.getChildren().addAll(axisY, axisX);
            axisY.toBack();
            axisX.toBack();
        }
        axisY.setVisible(axisShown);
        axisX.setVisible(axisShown);
        // sets newAxisLayer to identify current layer as having an axis
        newAxisLayer = false;
    }

    /**
     * creates random board with randomness set to slider weight, which is set to 50% by default
     * if board size is not already set, x, y, and z, are given a random number from 10-30
     */
    @FXML
    public void randomC() {
        Random rand = new Random();
        if (!checkAxisFilledBase()){
            String cubeSize = String.valueOf(rand.nextInt(20)+10);
            x.setText(cubeSize);
            y.setText(cubeSize);
            z.setText(cubeSize);
            xC();
            yC();
            zC();
        }
        boolean[][][] randomCArray = new boolean[yVal][xVal][zVal];
        for (int x = 0; x < xVal; x++) {
            for (int y = 0; y < yVal; y++) {
                for (int z = 0; z < zVal; z++) {
                    int weightedBoo = rand.nextInt(100);
                    if (weightedBoo > randWeight){
                        randomCArray[x][y][z] = true;
                    } else {
                        randomCArray[x][y][z] = false;
                    }
                }
            }
            setLayer(randomCArray);
        }
    }

    /**
     * sets randomWeight to current val of the slider
     */
    @FXML
    public void randomWeightC() {
        randWeight = (int) weightSlider.getValue();
    }

    /**
     * displays all key-binds in new stage
     */
    @FXML
    public void keybindsC() {
        Stage stage = new Stage();
        VBox vb = new VBox();
        stage.setTitle("key-binds");
        vb.setAlignment(Pos.CENTER);
        Label w = new Label("up layer: w");
        Label s = new Label("down layer: s");
        Label fill = new Label("fill layer: control + f");
        Label clear = new Label("clear layer: control + c");
        Label altM = new Label("draw: alt + mouse hover");
        Label ctrlM = new Label("erase: control + mouse hover");
        vb.getChildren().addAll(w,s,fill,clear,altM,ctrlM);
        vb.setStyle("-fx-background-color: #3b3f41");
        Scene scene = new Scene(vb,200,150);
        scene.getStylesheets().add("/resources/outputStyle.css");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    /**
     * displays information about the program in a new stage
     */
    @FXML
    public void aboutC() {
        Stage stage = new Stage();
        stage.setTitle("about");
        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        Label l1 = new Label("Inspired by Conways Game of Life, this program provides a easy and streamlined " +
                "method of input and output for 3D Cellular Automata. The default rules are 5 6 7 for alive, " +
                "and 6 for dead. The cellular neighborhood we use to calculate alive and dead cells is moore's type. " +
                "With moore's neighborhood, 26 possible values are read in, and a/d cells are accounted for. To use various key-binds," +
                " go to help->key-binds.");
        Label l2 = new Label("\nAuthors: Caleb Frey, Keegan Woodburn, Michael Gomez");
        Insets ins = new Insets(15,15,15,15);
        l1.setPadding(ins);
        l1.setStyle("-fx-font: 15 arial;");
        l2.setStyle("-fx-font: 13 arial;");
        l1.setWrapText(true);
        vb.getChildren().addAll(l1,l2);
        vb.setStyle("-fx-background-color: #3b3f41");
        Scene scene = new Scene(vb,450,250);
        scene.getStylesheets().add("/resources/outputStyle.css");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

//     non-sceneBuilder methods and any general helper methods used by many different nodes or classes
//     These are the checkers, setters, and other various background logic for the input interface

    /**
     * Fills board with given size and displays it in mainBoardPane-CENTER
     */
    void fillLayer() {
        mapCount = 0;
        // makes empty board
        empty3dArray();
        // makes new map and stackpane
        layerMap = new HashMap<>();
        stackPane = new StackPane();
        // clears all previous layers
        layers.clear();
        for (int y = 0; y < yVal; y++) {
            gridPane = new GridPane();
            for (int x = 0; x < zVal; x++) {
                for (int z = 0; z < xVal; z++) {
                    // makes new rectangle according to size
                    Rectangle rectangle = new Rectangle(30, 30);
                    rectangle.setStroke(Color.SILVER);
                    rectangle.setFill(Color.TRANSPARENT);
                    // Id is set to 0 if false, and to 1 if true
                    rectangle.setId("0");
                    // set rectangle listeners
                    setRectangles(rectangle);
                    // add rectangle to layer at x,z
                    gridPane.add(rectangle, x, z);
                }
            }
            // add all gridpane layers of rectangles to stackpane
            stackPane.getChildren().add(gridPane);
            layers.add(gridPane);
        }
        for (int l = 1; l < layers.size(); l++) {
            layers.get(l).setVisible(false);
        }
        // make zoomable scrollPane
        ZoomableScrollPane zScroll = new ZoomableScrollPane(stackPane);
        zScroll.setStyle("-fx-background-color: #2c2c2c");
        // set mainBoarderPane-CENTER to zoomableScrollPane
        mainBorderPane.setCenter(zScroll);
    }

    /**
     * literally does the same thing as fill layer, but with a predetermined board
     * @param cellArray
     */
    void setLayer(boolean[][][] cellArray) {
        // I'm not commenting this, go read fillLayer
        StackPane stackPane = new StackPane();
        layerMap = new HashMap<>();
        layers.clear();
        mapCount = 0;
        for (int y = 0; y < yVal; y++) {
            gridPane = new GridPane();
            for (int x = 0; x < zVal; x++) {
                for (int z = 0; z < xVal; z++) {
                    Rectangle rectangle = new Rectangle(30, 30);
                    rectangle.setStroke(Color.SILVER);
                    // sets values of rectangles according to given board values
                    if (cellArray[y][x][z]) {
                        rectangle.setFill(Color.SILVER);
                        rectangle.setId("1");
                    } else {
                        rectangle.setFill(Color.TRANSPARENT);
                        rectangle.setId("0");
                    }
                    setRectangles(rectangle);
                    gridPane.add(rectangle, x, z);
                }
            }
            stackPane.getChildren().add(gridPane);
            layers.add(gridPane);
        }
        for (int l = 1; l < layers.size(); l++) {
            layers.get(l).setVisible(false);
        }
        ZoomableScrollPane zScroll = new ZoomableScrollPane(stackPane);
        zScroll.setStyle("-fx-background-color: #2c2c2c");
        mainBorderPane.setCenter(zScroll);
    }

    /**
     * sets given rectangle up with necessary listeners and aggressively forces it into layerMap
     * @param rct
     */
    private void setRectangles(Rectangle rct){
        layerMap.put(mapCount,rct);
        mapCount++;
        rct.setOnMouseClicked(e -> {
            if (rct.getFill().equals(Color.TRANSPARENT)) {
                rct.setFill(Color.SILVER);
                rct.setId("1");
            } else {
                rct.setFill(Color.TRANSPARENT);
                rct.setId("0");
            }
        });
        rct.setOnMouseEntered(t -> {
            if (altPressed) {
                rct.setFill(Color.SILVER);
                rct.setId("1");

            } else if (ctrlPressed) {
                rct.setFill(Color.TRANSPARENT);
                rct.setId("0");
            }
        });
    }

    /**
     * makes an empty 3d array of size x,y,z
     */
    private void empty3dArray() {
        alive = new boolean[yVal][xVal][zVal];
        for (int y = 0; y < yVal; y++) {
            for (int x = 0; x < xVal; x++) {
                for (int z = 0; z < zVal; z++) {
                    alive[y][x][z] = false;
                }
            }
        }
    }

    /**
     * checks if given textFields have non-integer values
     * @param tf
     */
    private void validateAxis(TextField tf) {
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.matches("\\d*"))) {
                tf.setText(oldValue); //newValue.replaceAll("[^\\d]", "")
            }
        });
    }

    /**
     * checks if given textField has values between 1-50
     * @param tf
     */
    private void checkValidRange(TextField tf) {
        int MIN_XYZ = 1;
        int MAX_XYZ = 50;
        if (!(Integer.parseInt(tf.getText()) <= MAX_XYZ && Integer.parseInt(tf.getText()) >= MIN_XYZ)) {
            String sizeError = String.format("Size: (must be under %d)", MAX_XYZ);
            sizeLabel.setText(sizeError);
            tf.setText("");
        }
    }

    /**
     * checks if all axis have been filled, set various boolean values to necessary values
     */
    private void checkAxisFilled() {
        if (!x.getText().equals("") && !y.getText().equals("") && !z.getText().equals("")) {
            // once all textFields have been filled, fillLayer is run
            fillLayer();
            // gets rid of old axis if shown
            newAxisLayer = true;
            axisShown = false;
            showAxis.setSelected(false);
        }
    }

    /**
     * return boolean value if all axis are filled
     */
    private boolean checkAxisFilledBase(){
        return !x.getText().equals("") && !y.getText().equals("") && !z.getText().equals("");
    }

    /**
     * sets up various listeners for drawing, hot-keys, and pane-clicking
     */
    public void handlers(){
        // sets new listeners when ctrl is pressed for key-binds
        mainBorderPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                ctrlPressed = true;
                // request focus so erasing still works
                mainBorderPane.requestFocus();
                mainBorderPane.setOnKeyPressed(f -> {
                    if (f.getCode() == KeyCode.F){
                        fillLayerC();
                    } else if (f.getCode() == KeyCode.C){
                        clearLayerC();
                    } else {
                        // stops listener and resets handlers
                        mainBorderPane.setOnKeyPressed(null);
                        handlers();
                    }
                });
            }
            // drawing
            if (e.getCode() == KeyCode.ALT) {
                mainBorderPane.requestFocus();
                altPressed = true;
            }
            // up layer
            if (e.getCode() == KeyCode.W){
                if (checkAxisFilledBase()) {
                    upLayerC();
                }
            }
            // down layer
            if (e.getCode() == KeyCode.S){
                if (checkAxisFilledBase()) {
                    downLayerC();
                }
            }
        });
        // sets ctrl and alt to false so key-binds and drawing mechanics are stopped
        mainBorderPane.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                ctrlPressed = false;
            }
            if (e.getCode() == KeyCode.ALT) {
                altPressed = false;
            }
        });
    }
}