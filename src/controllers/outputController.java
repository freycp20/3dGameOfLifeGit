//package sample;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//public class Main extends Application {
//
//    @Override
//    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
//    }
//
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class outputController {
    private SimpleIntegerProperty randomness = new SimpleIntegerProperty(100);

    // size of graph
    private int size;
    private int width;
    private int cubeSize;
    private int stepSpeed = 250;

    // variables for mouse interaction
    private double mousePosX, mousePosY;
    private double mouseOldX, mouseOldY;
    private final Rotate rotateX = new Rotate(20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(-45, Rotate.Y_AXIS);
    private boolean modelRunning = false;
    private Board board;
    public VBox buttonVbox;
    public BorderPane mainBorderPane;
    public MenuBar mainMenuBar;
    public Button booleanBox;
    public TextField aliveNeighbor = null;
    public TextField deadNeighbor = null;
    public Button runButton;
    public Button resetButton;
    public StackPane subScenePane;
    private boolean falseBoxVal = false;
    private Timeline timeline;
    private Group cube;
    protected int yVal=0;
    protected int xVal=0;
    protected int zVal=0;
    Color lineColor;

    public void booleanBoxC() {
        falseBoxVal = !falseBoxVal;
        setFalseFirst(falseBoxVal);
        booleanBox.setText(String.valueOf(falseBoxVal));
    }
    public void aliveNC() {
        if (!aliveNeighbor.getText().equals("")){
            setAliveNeighbors(Integer.parseInt(aliveNeighbor.getText()));
        }
//        System.out.println("aliveNeighbors = " + aliveNeighbors);
    }
    public void deadNC() {
        if (!deadNeighbor.getText().equals("")){
            setDeadNeighbors(Integer.parseInt(deadNeighbor.getText()));
        }
//        System.out.println("deadNeighbor = " + deadNeighbors);
    }
    public void runButtonC() {
        if (!modelRunning){
//            System.out.println("Working!");
            timeline.play();
            runButton.setText("Stop");
            modelRunning = true;
        } else {
            timeline.pause();
            runButton.setText("Start");
            modelRunning = false;
        }
    }
    public void resetButtonC() {
//        System.out.println("here");
        if (modelRunning){
            runButtonC();
        }
        removeChildren(cube);
        board.reset();
        addValsToGroup(cube, board.getCells());
        timeline.pause();
    }


    public void init(int size, boolean[][][] alive) {
        lineColor = Color.web("#adacac");
//        lineColor = Color.TRANSPARENT;
        this.size = size;
        cube = createCube(size);
        width = (size/10);
        cubeSize = (size/(size/10));
        // initial cube rotation
        cube.getTransforms().addAll(rotateX, rotateY);
        cube.setTranslateZ(-size);
        board = new Board(alive);
//        System.out.println("board.getCells() = " + board.getCells());

        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setLightOn(true);
        StackPane root = new StackPane();
        root.getChildren().add(cube);
        setStyleDark();

        root.getChildren().add(ambientLight);

        addValsToGroup(cube, board.getCells());


        // scene
        SubScene subScene = new SubScene(root, subScenePane.getLayoutBounds().getMaxX(), subScenePane.getLayoutBounds().getMaxY(), true, SceneAntialiasing.BALANCED);
        subScene.heightProperty().bind(subScenePane.heightProperty());
        subScene.widthProperty().bind(subScenePane.widthProperty());
//        subScene.widthProperty().bind();
//        subScenePane.prefHeightProperty().bind(mainBorderPane.getCenter());
//        mainBorderPane.set;
//        subScenePane.setStyle("-fx-background-color: #000000;");
        subScene.setStyle("-fx-background-color: #000000;");
//        subScenePane.setMaxSize(mainBorderPane.getCenter().getLayoutBounds().getMaxX(), mainBorderPane.getCenter().getLayoutBounds().getMaxY());
        subScenePane.getChildren().addAll(subScene);
        subScenePane.setId("subScenePane");
        root.setId("cubeGridPane");
//        System.out.println("root.getId() = " + root.getId());
//        mainBorderPane.getCenter().;
        PerspectiveCamera cam = new PerspectiveCamera();

//        cam.setFieldOfView();
        subScene.setCamera(cam);
//        pointLight.setRotate(100);

//        page.setLeft(buttons);
        mainBorderPane.setCenter(subScenePane);

        setDragRotate(root);
        makeZoomable(root);
    }
    public void initialize() {
        timeline =
                new Timeline(new KeyFrame(Duration.millis(stepSpeed), f -> {
                    removeChildren(cube);
//                    System.out.println("this is working");
                    board.nextStep();
                    addValsToGroup(cube, board.getCells());
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public void setStyleDark() {
        mainBorderPane.getScene().getStylesheets().add("resources/outputStyle.css");
    }
    public void setBoard(String file) {
        board = new Board(new java.io.File(file));
    }
    public void setCube(int size) {
        this.size = size;
        cube = createCube(size);
//        System.out.println("this.size = " + this.size);
        // initial cube rotation
        cube.getTransforms().addAll(rotateX, rotateY);
        cube.setTranslateZ(-size);
    }
    private void setFalseFirst(boolean var){
        board.setFalseFirst(var);
    }

    public void setAliveNeighbors(int aliveNeighbors) {
        board.setAliveNeighbors(aliveNeighbors);
    }

    public void setDeadNeighbors(int deadNeighbors) {
        board.setDeadNeighbors(deadNeighbors);
    }

    public void setDragRotate(StackPane root){
        root.setOnMousePressed(me -> {
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        root.setOnMouseDragged(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            rotateX.setAngle(rotateX.getAngle() - (mousePosY - mouseOldY));
            rotateY.setAngle(rotateY.getAngle() + (mousePosX - mouseOldX));
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;

        });
    }

    public void addValsToGroup(Group cube, boolean[][][] cells){
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                for (int k = 0; k < cells[i][j].length; k++) {
                    if (cells[i][j][k]) {
//                        System.out.println(cells[i][j][k]);
//                        System.out.println("k = " + k);
//                        System.out.println("j = " + j);
//                        System.out.println("i = " + i);
                        PhongMaterial material = new PhongMaterial();
                        Box newBox = new Box(cubeSize, cubeSize, cubeSize);
                        newBox.setTranslateX(localize(i));
                        newBox.setTranslateY(localize(j));
                        newBox.setTranslateZ(localize(k));
                        newBox.setStyle("-fx-border-color: #808080");
                        int dx = width/2 - i;
                        int dy = width/2 - j;
                        int dz = width/2 - k;
                        final double MAX_DISTANCE = Math.sqrt(Math.pow(width/2, 2) + Math.pow(width/2, 2) + Math.pow(width/2, 2));
                        double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
                        material.setDiffuseColor(new Color(distance/MAX_DISTANCE,.3,1-distance/MAX_DISTANCE, .9));


                        newBox.setMaterial(material);
                        cube.getChildren().add(newBox);
                    }
//                    }
                }
            }
        }
    }
    public int localize(int value) {
        return (size/2)-5 - value*cubeSize;
    }

    public void removeChildren(Group cube) {
        for (Object object : cube.getChildren().toArray()) {
            if (object instanceof Box) {
                cube.getChildren().remove(object);
            }
        }
    }

    public void saveOriginGenC() {
        String content = String.format("%d %d %d\n%s", yVal, xVal, zVal, board.arrayToString(board.getStartingPos()));
        new fileIO().saveFile(content);
    }

    public void saveCurrentGenC() {
        if (modelRunning){
            runButtonC();
        }
        String content = String.format("%d %d %d\n%s", yVal, xVal, zVal, board.arrayToString(board.getCells()));
        new fileIO().saveFile(content);
    }

    public void openTemplateC() {
        fileIO ifio = new fileIO();
        ifio.openFile();
        yVal = ifio.getY();
        xVal = ifio.getX();
        zVal = ifio.getZ();
        board = new Board(ifio.getCellArray());
        init(yVal*10,board.getStartingPos());
    }
    public void switchSceneC(boolean[][][] arr) throws IOException {
        timeline.pause();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/input.fxml"));
        Parent inputRoot = loader.load();
//        Parent loader = FXMLLoader.load(getClass().getResource("/resources/output.fxml"));
        Scene scene = new Scene(inputRoot,mainBorderPane.getWidth(),mainBorderPane.getHeight());
        scene.getStylesheets().add("/resources/inputStyle.css");
        Stage input = (Stage) mainBorderPane.getScene().getWindow();
        input.setTitle("Input");
        input.setScene(scene);
        input.show();
        inputController inputC = loader.getController();
        inputC.mainBorderPane.requestFocus();
        if (yVal != 0){
            inputC.y.setText(String.valueOf(yVal));
            inputC.x.setText(String.valueOf(xVal));
            inputC.z.setText(String.valueOf(xVal));
            inputC.yVal = yVal;
            inputC.xVal = xVal;
            inputC.zVal = zVal;
            inputC.yC();
            inputC.xC();
            inputC.zC();
            inputC.handlers();
            inputC.setLayer(arr);
        }
    }
    public void switchCurrentC() throws IOException {
        switchSceneC(board.getCells());
    }

    public void switchOriginalC() throws IOException {
        switchSceneC(board.getStartingPos());
    }



    /**
     * Axis wall
     */
    public static class Axis extends Pane {

        Rectangle wall;

        public Axis(double size, Color lineColor) {

            // wall
            // first the wall, then the lines => overlapping of lines over walls
            // works
            wall = new Rectangle(size, size);
            getChildren().add(wall);
            wall.setOpacity(0);

            // grid
            double zTranslate = 0;
            double lineWidth = 0.5;
            Color gridColor = lineColor;

            for (int y = 0; y <= size; y += size) {

                Line line = new Line(0, 0, size, 0);
                line.setStroke(gridColor);
                line.setFill(gridColor);
                line.setTranslateY(y);
                line.setTranslateZ(zTranslate);
                line.setStrokeWidth(lineWidth);

                getChildren().addAll(line);

            }

            for (int x = 0; x <= size; x += size) {

                Line line = new Line(0, 0, 0, size);
                line.setStroke(gridColor);
                line.setFill(gridColor);
                line.setTranslateX(x);
                line.setTranslateZ(zTranslate);
                line.setStrokeWidth(lineWidth);

                getChildren().addAll(line);

            }
        }

        public void setFill(Paint paint) {
            wall.setFill(paint);
        }

    }

    public void makeZoomable(StackPane control) {

        final double MAX_SCALE = 3;
        final double MIN_SCALE = .05;

        control.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent event) {

                double delta = 1.2;

                double scale = control.getScaleX();

                if (event.getDeltaY() < 0) {
                    scale /= delta;
                } else {
                    scale *= delta;
                }

                scale = clamp(scale, MIN_SCALE, MAX_SCALE);

                control.setScaleX(scale);
                control.setScaleY(scale);

                event.consume();

            }

        });

    }

    /**
     * Create axis walls
     * @param size
     * @return returns cube
     */
    private Group createCube(int size) {

        Group cube = new Group();

        List<Axis> cubeFaces = new ArrayList<>();
        Axis r;

        // back face
        r = new Axis(size, lineColor);
        r.setTranslateX((-0.5 * size));
        r.setTranslateY(-0.5 * size);
        r.setTranslateZ((0.5 * size));
        cubeFaces.add(r);

        // bottom face
        r = new Axis(size, lineColor);
        r.setTranslateX(-0.5 * size);
        r.setTranslateY(0);
        r.setRotationAxis(Rotate.X_AXIS);
        r.setRotate(90);
        cubeFaces.add(r);

        // right face
        r = new Axis(size, lineColor);
        r.setTranslateX(-size);
        r.setTranslateY(-0.5 * size);
        r.setRotationAxis(Rotate.Y_AXIS);
        r.setRotate(90);
        cubeFaces.add( r);

        // left face
        r = new Axis(size, lineColor);
        r.setTranslateX(0);
        r.setTranslateY((-0.5 * size));
        r.setRotationAxis(Rotate.Y_AXIS);
        r.setRotate(90);
        cubeFaces.add(r);

        // top face
        r = new Axis(size, lineColor);
        r.setTranslateX(-0.5 * size);
        r.setTranslateY(-size);
        r.setRotationAxis(Rotate.X_AXIS);
        r.setRotate(90);
        cubeFaces.add(r);

        // front face
        r = new Axis(size, lineColor);
        r.setTranslateX(-0.5 * size);
        r.setTranslateY(-0.5 * size);
        r.setTranslateZ(-0.5 * size);
        cubeFaces.add( r);
        cube.getChildren().addAll(cubeFaces);
        return cube;
    }

    public static double clamp(double value, double min, double max) {

        if (Double.compare(value, min) < 0)
            return min;
        if (Double.compare(value, max) > 0)
            return max;
        return value;
    }
}