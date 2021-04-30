package controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;


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
    public CheckMenuItem showAxis;
    public VBox buttonVbox;
    public MenuBar mainMenuBar;
    public Label sizeLabel = null;

    protected int layerCount;
    protected int xVal;
    protected int yVal;
    protected int zVal;
    private int oldSliderVal;
    private double onionPercent = 75.7;
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
    public boolean rule1;
    public ArrayList<Integer> aNeighbors;
    public ArrayList<Integer> dNeighbors;

    /**
     * sceneBuilder methods
     */
    @FXML
    public void initialize() throws MalformedURLException {
        layerCount = 1;
        validateAxis(x);
        validateAxis(y);
        validateAxis(z);
        handlers();
        VBox vbox = new VBox();
        Label label = new Label("Welcome");
        label.setStyle("-fx-font: 24 arial;");
        vbox.getChildren().add(label);
        /**
         * CALEBS TEST
         */
        File mediaFile = new File("/Users/calebfrey/IdeaProjects/3dGameOfLifeGit/animation.mp4");
        Media media = new Media(mediaFile.toURI().toURL().toString());

        MediaPlayer mediaPlayer = new MediaPlayer(media);

        MediaView mediaView = new MediaView(mediaPlayer);
        mediaPlayer.play();
        vbox.getChildren().add(new Pane(mediaView));
        ZoomableScrollPane zScroll = new ZoomableScrollPane(vbox);
        zScroll.setStyle("-fx-background-color: #2c2c2c");
        mainBorderPane.setCenter(zScroll);
    }
    public void handlers(){
        mainBorderPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                ctrlPressed = true;
                mainBorderPane.setOnKeyPressed(f -> {
                    if (f.getCode() == KeyCode.F){
                        fillLayerC();
                    } else if (f.getCode() == KeyCode.C){
                        clearLayerC();
                    } else {
                        mainBorderPane.setOnKeyPressed(null);
                        handlers();
                    }
                });
            }
            if (e.getCode() == KeyCode.ALT) {
                altPressed = true;
            }
            if (e.getCode() == KeyCode.W){
                if (checkAxisFilledBase()) {
                    upLayerC();
                }
            }
            if (e.getCode() == KeyCode.S){
                if (checkAxisFilledBase()) {
                    downLayerC();
                }
            }
        });
        mainBorderPane.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                ctrlPressed = false;
            }
            if (e.getCode() == KeyCode.ALT) {
                altPressed = false;
            }
        });
    }
    public void switchSceneC() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/output.fxml"));
        Parent outputRoot = loader.load();
//        Parent loader = FXMLLoader.load(getClass().getResource("/resources/output.fxml"));
        Scene scene = new Scene(outputRoot,mainBorderPane.getWidth(),mainBorderPane.getHeight());
        scene.getStylesheets().add("/resources/outputStyle.css");
        Stage output = (Stage) mainBorderPane.getScene().getWindow();
        output.setTitle("Output");
        output.setScene(scene);
        output.show();
        outputController outputC = loader.getController();
        outputC.mainBorderPane.requestFocus();
        if (checkAxisFilledBase()){
            saveC();
            outputC.init(this.xVal*10, alive);
//        outputC.setSize(this.xVal*10);
//        System.out.println("xVal = " + xVal);
//        outputC.setCube(this.xVal*10);
//        outputC.setBoard("test.txt");
            outputC.yVal = yVal;
            outputC.xVal = xVal;
            outputC.zVal = zVal;
            outputC.boardMade = true;
            if (areRules){
                outputC.areRules = areRules;
                outputC.rule1 = rule1;
                outputC.aNeighbors = aNeighbors;
                outputC.dNeighbors = dNeighbors;
            }
        }
    }

    public void saveC() {
        aliveString = ""; // don't add to already made alive
        String tempCell;
        for (int y = 0; y < yVal; y++) {
            for (int i = 0; i < zVal; i++) {
                for (int j = i; j < xVal * zVal; j += zVal) {
//                    System.out.println(layers.get(y).getChildren().get(j).getId().equals("1") + " = " + y + "," + j/zVal + "," + j%xVal);

                    alive[y][j / zVal][j % xVal] = layers.get(y).getChildren().get(j).getId().equals("1"); // y = y | x = j/xVal | z = j%zVal
                    aliveString += alive[y][j / zVal][j % xVal] + " ";
                }
                aliveString += "\n";
            }
            aliveString += "\n";
        }

        // set 3d visualization
    }

    @FXML
    public void upLayerC() {
        if (layerCount != yVal) {
            layerCount++;
            layerNumLabel.setText("Layer #: " + layerCount);
            slider.setValue(layerCount);
            for (int i = 0; i < (layerCount) - 1; i++) {
                GridPane layer = layers.get(i);
                layer.setOpacity(layer.getOpacity() * 0.5);
            }
            layers.get(layerCount - 1).setVisible(true);
            layers.get(layerCount - 1).setOpacity(1);
        }
    }

    @FXML
    public void downLayerC() {
        if (slider.getValue() == 0) ; // this makes things work don't delete it
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

    @FXML
    public void sliderPressed() {
        oldSliderVal = (int) slider.getValue();
    }

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
    private boolean checkAxisFilledBase(){
        if (!x.getText().equals("") && !y.getText().equals("") && !z.getText().equals("")){
            return true;
        }
        return false;
    }

    @FXML
    public void xC() {
//        x.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!(x.getText().equals(""))) {
//                if (oldVal && !newVal) {
                    xVal = Integer.parseInt(x.getText());
                    checkValidRange(x);
                    checkAxisFilled();
//                }
            }
//        });

    }

    @FXML
    public void yC() {
//            y.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!(y.getText().equals(""))) {
//                    if (oldVal && !newVal) {
                        yVal = Integer.parseInt(y.getText());
                        layerNumLabel.setText("Layer #: 1");
                        slider.setBlockIncrement(10);
                        slider.setMax(yVal);
                        slider.setMin(1);
                        slider.setShowTickMarks(true);
                        slider.setMinorTickCount(1);
                        slider.setMajorTickUnit(2);
                        checkValidRange(y);
                        checkAxisFilled();
                    }
//                }
//            });
    }

    @FXML
    public void zC() {
//        z.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!(z.getText().equals(""))) {
//                if (oldVal && !newVal) {
                    zVal = Integer.parseInt(z.getText());
                    checkValidRange(z);
                    checkAxisFilled();
//                }
            }
//        });
    }

    @FXML
    public void openTemplateC() {
        fileIO ifio = new fileIO();
        ifio.openFile();
        yVal = ifio.getY();
        xVal = ifio.getX();
        zVal = ifio.getZ();
        y.setText(String.valueOf(yVal));
        x.setText(String.valueOf(xVal));
        z.setText(String.valueOf(zVal));
        xC();
        yC();
        zC();
        if (ifio.areRules()){
            areRules = ifio.areRules();
            rule1 = ifio.getRule1();
            aNeighbors = ifio.getRule2();
            dNeighbors = ifio.getRule3();
        }
        setLayer(ifio.getCellArray());
    }

    @FXML
    public void saveTemplateC() {
        saveC();
        String content = String.format("%d %d %d %b\n%s", yVal, xVal, zVal, true, aliveString);
        new fileIO().saveFile(content);
    }

    @FXML
    public void showAxisC() {
        axisShown = !axisShown;
        if (newAxisLayer) {
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
        newAxisLayer = false;
    }

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

    public void aboutC() {
        Stage stage = new Stage();
        stage.setTitle("key-binds");
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

    /**
     * non-scenebuilder methods
     */
    @FXML
    void fillLayer() { // originally initialize
        mapCount = 0;
        empty3dArray();
        layerMap = new HashMap<>();
        stackPane = new StackPane();
        layers.clear();
        for (int y = 0; y < yVal; y++) {
            gridPane = new GridPane();
            for (int x = 0; x < zVal; x++) {
                for (int z = 0; z < xVal; z++) {
                    Rectangle rectangle = new Rectangle(30, 30);
                    rectangle.setStroke(Color.SILVER);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setId("0");

                    setRectangles(rectangle);
//                    System.out.println("mainBorderPane.getOnKeyPressed() = " + mainBorderPane.getOnKeyPressed());
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

    @FXML
    void setLayer(boolean[][][] cellArray) {
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

    private void validateAxis(TextField tf) {
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue.matches("\\d*"))) {
                tf.setText(oldValue); //newValue.replaceAll("[^\\d]", "")
            }
        });
    }

    private void checkValidRange(TextField tf) {
        int MIN_XYZ = 1;
        int MAX_XYZ = 50;
        if (!(Integer.parseInt(tf.getText()) <= MAX_XYZ && Integer.parseInt(tf.getText()) >= MIN_XYZ)) {
            String sizeError = String.format("Size: (must be under %d)", MAX_XYZ);
            sizeLabel.setText(sizeError);
            tf.setText("");
        }
    }

    private void checkAxisFilled() {
        if (!x.getText().equals("") && !y.getText().equals("") && !z.getText().equals("")) {
            fillLayer();
            newAxisLayer = true;
            axisShown = false;
            showAxis.setSelected(false);
//            mainBorderPane.requestFocus();
        }
    }

    public void visualizeC() throws IOException {
        switchSceneC();
    }
}
