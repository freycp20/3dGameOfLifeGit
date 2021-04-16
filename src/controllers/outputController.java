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
import javafx.scene.input.MouseEvent;
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

    boolean[][][] alive;
    String file = "FORCLAEB.txt";
    // size of graph
    private int size;
    private int width;
    private int cubeSize;
    private int stepSpeed = 250;
    Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

    private boolean falseFirst = true;
    private int aliveNeighbors = 1;
    private int deadNeighbors = 1;
    // variables for mouse interaction
    private double mousePosX, mousePosY;
    private double mouseOldX, mouseOldY;
    private final Rotate rotateX = new Rotate(20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(-45, Rotate.Y_AXIS);
    private boolean modelRunning = false;
    private Board board;

    public VBox mainVbox;
    public BorderPane mainBorderPane;
    public MenuBar mainMenuBar;
    public Button booleanBox;
    public TextField aliveNeighbor = null;
    public TextField deadNeighbor = null;
    public Button runButton;
    public Button resetButton;
    private boolean falseBoxVal = false;
    private Timeline timeline;
    private Group cube;
    protected int yVal;
    protected int xVal;
    protected int zVal;

    public void booleanBoxC() {
        falseBoxVal = !falseBoxVal;
        setFalseFirst(falseBoxVal);
        booleanBox.setText(String.valueOf(falseBoxVal));
    }
    public void aliveNC() {
        setAliveNeighbors(Integer.parseInt(aliveNeighbor.getText()));
        System.out.println("aliveNeighbors = " + aliveNeighbors);
    }
    public void deadNC() {
        setDeadNeighbors(Integer.parseInt(deadNeighbor.getText()));
        System.out.println("deadNeighbor = " + deadNeighbors);
    }
    public void runButtonC() {
        if (!modelRunning){
            System.out.println("Working!");
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
        System.out.println("here");
        removeChildren(cube);
        board.reset();
        addValsToGroup(cube, board.getCells());
        modelRunning = false;
        timeline.pause();
    }

    public void switchSceneC() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/input.fxml"));
        Parent inputRoot = loader.load();
//        Parent loader = FXMLLoader.load(getClass().getResource("/resources/output.fxml"));
        Scene scene = new Scene(inputRoot,mainBorderPane.getWidth(),mainBorderPane.getHeight());
        Stage output = (Stage) mainBorderPane.getScene().getWindow();
        output.setScene(scene);
        inputController inputC = loader.getController();
        inputC.yVal = yVal;
        output.show();

    }
    public void init(int size, boolean[][][] alive) {
        this.size = size;
        cube = createCube(size);
        width = (size/10);
        cubeSize = (size/(size/10));
        // initial cube rotation
        cube.getTransforms().addAll(rotateX, rotateY);
        cube.setTranslateZ(-size);
        board = new Board(alive);
        System.out.println("board.getCells() = " + board.getCells());


        BorderPane page = new BorderPane();
        StackPane root = new StackPane();
        root.getChildren().add(cube);

        addValsToGroup(cube, board.getCells());

        // scene
        SubScene subScene = new SubScene(root, 800, 900, true, SceneAntialiasing.BALANCED);
        PerspectiveCamera cam = new PerspectiveCamera();

//        cam.setFieldOfView();
        subScene.setCamera(cam);
        Label random = new Label();

//        page.setLeft(buttons);
        page.setCenter(subScene);
        mainVbox.getChildren().add(page);

//        page.getChildren().addAll(removeKids, random, subScene);

//        Scene scene = new Scene(page, bounds.getWidth()/2, bounds.getHeight(), true, SceneAntialiasing.BALANCED);
//        scene.setOnKeyPressed(e -> {
//            if (e.getCode() == KeyCode.COMMAND) {
//                if (!modelRunning) {
//                    timeline.play();
//                    removeKids.setText("Stop Generations");
//                    modelRunning = true;
//                } else{
//                    modelRunning = false;
//                    timeline.pause();
//                    removeKids.setText("Start Generations");
//                }
//            }
//        });
//        screenshot(scene, "woah.jpg");

//        primaryStage.setResizable(true);
//        primaryStage.setScene(scene);
//        primaryStage.setX(bounds.getWidth()/2);
//        primaryStage.show();
        setDragRotate(root);
        makeZoomable(root);
    }
    public void initialize() {
        timeline =
                new Timeline(new KeyFrame(Duration.millis(stepSpeed), f -> {
                    removeChildren(cube);
                    System.out.println("this is working");
                    board.nextStep();
                    addValsToGroup(cube, board.getCells());
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
//        // create axis walls
//        size = 100;
//        cube = createCube(size);
//        width = (size/10);
//        cubeSize = (size/(size/10));
//        // initial cube rotation
//        cube.getTransforms().addAll(rotateX, rotateY);
//        cube.setTranslateZ(-size);
//        board = new Board(new java.io.File("test.txt"));
//        timeline =
//                new Timeline(new KeyFrame(Duration.millis(stepSpeed), f -> {
//                    removeChildren(cube);
//                    System.out.println();
//                    board.nextStep();
//                    addValsToGroup(cube, board.getCells());
//                }));
//        timeline.setCycleCount(Animation.INDEFINITE);
//
//        BorderPane page = new BorderPane();
//        StackPane root = new StackPane();
//        root.getChildren().add(cube);
//
//        addValsToGroup(cube, board.getCells());
//
//        // scene
//        SubScene subScene = new SubScene(root, 800, 550, true, SceneAntialiasing.BALANCED);
//        PerspectiveCamera cam = new PerspectiveCamera();
//
////        cam.setFieldOfView();
//        subScene.setCamera(cam);
//        Label random = new Label();
//
////        page.setLeft(buttons);
//        page.setCenter(subScene);
//        mainVbox.getChildren().add(page);
//
////        page.getChildren().addAll(removeKids, random, subScene);
//
////        Scene scene = new Scene(page, bounds.getWidth()/2, bounds.getHeight(), true, SceneAntialiasing.BALANCED);
////        scene.setOnKeyPressed(e -> {
////            if (e.getCode() == KeyCode.COMMAND) {
////                if (!modelRunning) {
////                    timeline.play();
////                    removeKids.setText("Stop Generations");
////                    modelRunning = true;
////                } else{
////                    modelRunning = false;
////                    timeline.pause();
////                    removeKids.setText("Start Generations");
////                }
////            }
////        });
////        screenshot(scene, "woah.jpg");
//
////        primaryStage.setResizable(true);
////        primaryStage.setScene(scene);
////        primaryStage.setX(bounds.getWidth()/2);
////        primaryStage.show();
//        setDragRotate(root);
//        makeZoomable(root);
    }
    public void setBoard(String file) {
        board = new Board(new java.io.File(file));
    }
    public void setCube(int size) {
        this.size = size;
        cube = createCube(size);
        System.out.println("this.size = " + this.size);
        // initial cube rotation
        cube.getTransforms().addAll(rotateX, rotateY);
        cube.setTranslateZ(-size);
    }
    private void setFalseFirst(boolean var){
        falseFirst = var;
    }

    public void setAliveNeighbors(int aliveNeighbors) {
        this.aliveNeighbors = aliveNeighbors;
    }

    public void setDeadNeighbors(int deadNeighbors) {
        this.deadNeighbors = deadNeighbors;
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
                        System.out.println(cells[i][j][k]);
                        System.out.println("k = " + k);
                        System.out.println("j = " + j);
                        System.out.println("i = " + i);
                        PhongMaterial material = new PhongMaterial();
                        Box newBox = new Box(cubeSize, cubeSize, cubeSize);
                        newBox.setTranslateX(localize(i));
                        newBox.setTranslateY(localize(j));
                        newBox.setTranslateZ(localize(k));

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


    /**
     * Axis wall
     */
    public static class Axis extends Pane {

        Rectangle wall;

        public Axis(double size) {

            // wall
            // first the wall, then the lines => overlapping of lines over walls
            // works
            wall = new Rectangle(size, size);
            getChildren().add(wall);
            wall.setOpacity(0);

            // grid
            double zTranslate = 0;
            double lineWidth = 0.5;
            Color gridColor = Color.BLUE;

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

        final double MAX_SCALE = .8;
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
        r = new Axis(size);
        r.setTranslateX((-0.5 * size));
        r.setTranslateY(-0.5 * size);
        r.setTranslateZ((0.5 * size));
        cubeFaces.add(r);

        // bottom face
        r = new Axis(size);
        r.setTranslateX(-0.5 * size);
        r.setTranslateY(0);
        r.setRotationAxis(Rotate.X_AXIS);
        r.setRotate(90);
        cubeFaces.add(r);

        // right face
        r = new Axis(size);
        r.setTranslateX(-size);
        r.setTranslateY(-0.5 * size);
        r.setRotationAxis(Rotate.Y_AXIS);
        r.setRotate(90);
        cubeFaces.add( r);

        // left face
        r = new Axis(size);
        r.setTranslateX(0);
        r.setTranslateY((-0.5 * size));
        r.setRotationAxis(Rotate.Y_AXIS);
        r.setRotate(90);
        cubeFaces.add(r);

        // top face
        r = new Axis(size);
        r.setTranslateX(-0.5 * size);
        r.setTranslateY(-size);
        r.setRotationAxis(Rotate.X_AXIS);
        r.setRotate(90);
        cubeFaces.add(r);

        // front face
        r = new Axis(size);
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