package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
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
    public Label sizeLabel = null;
    public BorderPane mainBorderPane = null;
    public CheckMenuItem showAxis;
    public VBox buttonVbox;

    private int layerCount;
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

    /**
     * sceneBuilder methods
     */
    @FXML
    public void initialize() {
        layerCount = 1;
        validateAxis(x);
        validateAxis(y);
        validateAxis(z);
        mainBorderPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                ctrlPressed = true;
            }
            if (e.getCode() == KeyCode.ALT) {
                altPressed = true;
            }
            if (e.getCode() == KeyCode.W){
                upLayerC();
                e.consume();
            }
            if (e.getCode() == KeyCode.S){
                downLayerC();
                e.consume();
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
        Stage output = (Stage) mainBorderPane.getScene().getWindow();
        output.setScene(scene);
        output.show();
        if (checkAxisFilledBase()){
            saveC();
            outputController outputC = loader.getController();
            outputC.init(this.xVal*10, alive);
//        outputC.setSize(this.xVal*10);
//        System.out.println("xVal = " + xVal);
//        outputC.setCube(this.xVal*10);
//        outputC.setBoard("test.txt");
            outputC.yVal = yVal;
            outputC.xVal = xVal;
            outputC.zVal = zVal;
        }
    }

    public void saveC() {
        aliveString = ""; // don't add to already made alive
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
    }
    public void fillLayerC(){
        if (checkAxisFilledBase()) {
            int mapCount = 0;
            for (int x = 0; x < xVal; x++) {
                for (int z = 0; z < zVal; z++) {
                    layerMap.get((layerCount-1)*(xVal*zVal)+mapCount).setFill(Color.BLACK);
                    layerMap.get((layerCount-1)*(xVal*zVal)+mapCount).setId("1");
                    mapCount++;
                }
            }
        }
    }
    private boolean checkAxisFilledBase(){
        if (!x.getText().equals("") && !y.getText().equals("") && !z.getText().equals("")){
            return true;
        }
        return false;
    }

    @FXML
    public void xC() {
        if (!(x.getText().equals(""))) {
            xVal = Integer.parseInt(x.getText());
            checkValidRange(x);
            checkAxisFilled();
        }
    }

    @FXML
    public void yC() {
        if (!(y.getText().equals(""))) {
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
    }

    @FXML
    public void zC() {
        if (!(z.getText().equals(""))) {
            zVal = Integer.parseInt(z.getText());
            checkValidRange(z);
            checkAxisFilled();
        }
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
        setLayer(ifio.getCellArray());
    }

    @FXML
    public void saveTemplateC() {
        saveC();
        String content = String.format("%d %d %d\n%s", yVal, xVal, zVal, aliveString);
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
            axisY.setOpacity(0.7);
            axisX.setOpacity(0.7);
            stackPane.getChildren().addAll(axisY, axisX);
        }
        axisY.setVisible(axisShown);
        axisX.setVisible(axisShown);
        newAxisLayer = false;
    }

    /**
     * non-scenebuilder methods
     */
    @FXML
    void fillLayer() { // originally initialize
        int mapCount = 0;
        layerMap = new HashMap<>();
        empty3dArray();
        stackPane = new StackPane();
        stackPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                ctrlPressed = true;
            }
            if (e.getCode() == KeyCode.ALT) {
                altPressed = true;
            }
        });
        stackPane.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                ctrlPressed = false;
            }
            if (e.getCode() == KeyCode.ALT) {
                altPressed = false;
            }
        });
        layers.clear();
        for (int y = 0; y < yVal; y++) {
            gridPane = new GridPane();
            for (int x = 0; x < zVal; x++) {
                for (int z = 0; z < xVal; z++) {
                    Rectangle rectangle = new Rectangle(30, 30);
                    rectangle.setStroke(Color.BLUE);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setId("0");

                    layerMap.put(mapCount,rectangle);
                    mapCount++;


                    rectangle.setOnMouseClicked(e -> {
                        if (rectangle.getFill().equals(Color.TRANSPARENT)) {
                            rectangle.setFill(Color.BLACK);
                            rectangle.setId("1");
                        } else {
                            rectangle.setFill(Color.TRANSPARENT);
                            rectangle.setId("0");
                        }
                    });
                    rectangle.setOnMouseEntered(t -> {
                        if (altPressed) {
                            rectangle.setFill(Color.BLACK);
                            rectangle.setId("1");

                        } else if (ctrlPressed) {
                            rectangle.setFill(Color.TRANSPARENT);
                            rectangle.setId("0");
                        }
                    });
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
        mainBorderPane.setCenter(zScroll);
    }

    @FXML
    void setLayer(boolean[][][] cellArray) {
        empty3dArray();
        StackPane stackPane = new StackPane();
        layers.clear();
        for (int y = 0; y < yVal; y++) {
            gridPane = new GridPane();
            for (int x = 0; x < zVal; x++) {
                for (int z = 0; z < xVal; z++) {
                    Rectangle rectangle = new Rectangle(30, 30);
                    rectangle.setStroke(Color.BLUE);
                    if (cellArray[y][x][z]) {
                        rectangle.setFill(Color.BLACK);
                        rectangle.setId("1");
                    } else {
                        rectangle.setFill(Color.TRANSPARENT);
                        rectangle.setId("0");
                    }
                    rectangle.setOnMouseClicked(e -> {
                        if (rectangle.getFill().equals(Color.TRANSPARENT)) {
                            rectangle.setFill(Color.BLACK);
                            rectangle.setId("1");
                        } else {
                            rectangle.setFill(Color.TRANSPARENT);
                            rectangle.setId("0");
                        }
                    });
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
        mainBorderPane.setCenter(zScroll);
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
        }
    }

    public void visualizeC() throws IOException {
        switchSceneC();
    }
}
