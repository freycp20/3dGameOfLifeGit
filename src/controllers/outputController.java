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

import javafx.animation.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
    private ArrayList<Integer> aliveNList;
    private ArrayList<Integer> deadNList;
    private guiBoard board;
    private MediaPlayer player;
    private Timeline rotateTimeline;
    public Slider speedSlider;
    public VBox buttonVbox;
    public BorderPane mainBorderPane;
    public MenuBar mainMenuBar;
    public Button booleanBox;
    public TextField aliveNeighbor = null;
    public TextField deadNeighbor = null;
    public Button runButton;
    public Button resetButton;
    public Button nextGeneration;
    public StackPane subScenePane;
    private boolean trueBox = true;
    private Timeline timeline;
    private Group cube;
    protected int yVal=0;
    protected int xVal=0;
    protected int zVal=0;
    Color lineColor;
    public boolean boardMade;
    private int deadN;
    private int aliveN;
    public boolean areRules;
    public boolean rule1;
    ArrayList<Integer> aNeighbors;
    ArrayList<Integer> dNeighbors;


    public void booleanBoxC() {
        trueBox = !trueBox;
        board.setTrueFirst(trueBox);
        booleanBox.setText(String.valueOf(trueBox));
    }
    public void aliveNC() {
        if (!aliveNeighbor.getText().equals("")){
            aNeighbors = new ArrayList<>();

            Scanner scnr = new Scanner(aliveNeighbor.getText())
                    .useDelimiter("[^\\d]");

            while (scnr.hasNext()) {
                String next = scnr.next();
                if (!next.isEmpty()) {
                    aNeighbors.add(Integer.parseInt(next));
                }
            }
            board.setDeadNlist(aNeighbors);

        }
    }
    public void deadNC() {
        if (!deadNeighbor.getText().equals("")){
            dNeighbors = new ArrayList<>();

            Scanner scnr = new Scanner(deadNeighbor.getText())
                    .useDelimiter("[^\\d]");

            while (scnr.hasNext()) {
                String next = scnr.next();
                if (!next.isEmpty()) {
                    dNeighbors.add(Integer.parseInt(next));
                }
            }
            board.setDeadNlist(dNeighbors);

        }
    }

    public void runButtonC() {
        // this code is literally jank as fuck but it works don't delete it
        if (!modelRunning){
            playTimeLine();
            runButton.setText("Stop");
            modelRunning = true;
        } else {
            pauseTimeLine();
            runButton.setText("Start");
            modelRunning = false;
        }
    }
    public void rotateButtonC() {
        if (rotateTimeline.getStatus().equals(Animation.Status.RUNNING)){
            rotateTimeline.pause();
//            player.pause();
        }
        else {
            rotateTimeline.play();
//            player.play();
        }
    }
    public void resetButtonC() {
//        System.out.println("here");
//        rotateTimeline.play();
        if (modelRunning){
            runButtonC();
        }
        removeChildren(cube);
        board.reset();
        addValsToGroup(cube, board.getCells());
        pauseTimeLine();
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
        board = new guiBoard(alive);
//        System.out.println("board.getCells() = " + board.getCells());

        board.setDeadNeighbors(3);
        board.setAliveNeighbors(3);
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
    public void pauseTimeLine() {
        timeline.pause();
        modelRunning = false;
        runButton.setText("Start");
    }
    public void playTimeLine() {
        timeline.play();
        modelRunning = true;
        runButton.setText("Stop");

    }
    public void timeLineLogic() {
        boolean[][][] lastBoard = board.getCells();
        board.nextStep();
        if (Arrays.deepEquals(board.getCells(), lastBoard)) {
            pauseTimeLine();
        }
        else {
            removeChildren(cube);
            addValsToGroup(cube, board.getCells());
        }
    }
    public void initialize() {
        timeline =
                new Timeline(new KeyFrame(Duration.millis(stepSpeed), f -> {
                    timeLineLogic();
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        rotateTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        new KeyValue(rotateY.angleProperty(), rotateY.getAngle()),
                        new KeyValue(rotateX.angleProperty(), rotateX.getAngle())
                ),
                new KeyFrame(
                        Duration.seconds(90),
                        new KeyValue(rotateY.angleProperty(), rotateY.getAngle() + 3000),
                        new KeyValue(rotateX.angleProperty(), rotateX.getAngle() + 3000)
                )
        );
    }

    public void setStyleDark() {
        mainBorderPane.getScene().getStylesheets().add("resources/outputStyle.css");
    }
    public void setBoard(String file) {
        board = new guiBoard(new java.io.File(file));
    }
    public void setCube(int size) {
        this.size = size;
        cube = createCube(size);

        cube.getTransforms().addAll(rotateX, rotateY);
        cube.setTranslateZ(-size);
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
        if (boardMade){
            String aN = "";
            String dN = "";
            for (int i = 0; i < aNeighbors.size(); i++) {
                aN += aNeighbors.get(i) + " ";
            }
            for (int i = 0; i < dNeighbors.size(); i++) {
                dN += dNeighbors.get(i) + " ";
            }
            String content = String.format(
                    "%d %d %d %s %s %d %s %d %s\n%s", yVal, xVal, zVal, true, trueBox,aNeighbors.size(), aN,dNeighbors.size(),dN, board.arrayToString(board.getStartingPos()));
                    new fileIO().saveFile(content);
        }
    }

    public void saveCurrentGenC() {
        if (boardMade){
            if (modelRunning){
                runButtonC();
            }
            String aN = "";
            String dN = "";
            for (int i = 0; i < aNeighbors.size(); i++) {
                aN += aNeighbors.get(i) + " ";
            }
            for (int i = 0; i < dNeighbors.size(); i++) {
                dN += dNeighbors.get(i) + " ";
            }
            String content = String.format(
                    "%d %d %d %s %s %d %s %d %s\n%s", yVal, xVal, zVal, true, trueBox,aNeighbors.size(), aN,dNeighbors.size(),dN, board.arrayToString(board.getCells()));
            new fileIO().saveFile(content);
        }
    }

    public void openTemplateC() {
        fileIO ifio = new fileIO();
        ifio.openFile();
        if (!(cube == null)) {
            cube.getChildren().removeAll();
        }

        if (ifio.boardOpened()){
            yVal = ifio.getY();
            xVal = ifio.getX();
            zVal = ifio.getZ();
            board = new guiBoard(ifio.getCellArray());
            if (ifio.areRules()){
                board.setTrueFirst(ifio.getRule1());
                booleanBox.setText(String.valueOf(ifio.getRule1()));
                board.setAliveNlist(ifio.getRule2());
                aliveNeighbor.setText(String.valueOf(ifio.getRule2()));
                board.setDeadNlist(ifio.getRule3());
                deadNeighbor.setText(String.valueOf(ifio.getRule3()));
            }
            boardMade = true;
            init(yVal*10,board.getStartingPos());
            resetButtonC();
            booleanBoxC();
            booleanBoxC();
        }
    }
    public void switchSceneC(boolean[][][] arr) throws IOException {
        pauseTimeLine();
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
        if (boardMade){
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
            inputC.areRules = areRules;
            inputC.rule1 = rule1;
            inputC.aNeighbors = aNeighbors;
            inputC.dNeighbors = dNeighbors;
        }
    }
    public void switchCurrentC() throws IOException {
        if (boardMade){
            switchSceneC(board.getCells());
        } else {
            switchSceneC(new boolean[0][0][0]);
        }
    }

    public void switchOriginalC() throws IOException {
        if (boardMade){
            switchSceneC(board.getStartingPos());
        } else {
            switchSceneC(new boolean[0][0][0]);
        }
    }

    public void switchNewC() throws IOException {
        if (boardMade){
            saveWarning();
        } else {
            switchSceneC(new boolean[0][0][0]);
        }
    }
    public void saveWarning() {
        Stage stage = new Stage();
        VBox vb = new VBox();
        HBox hb = new HBox();
        stage.setTitle("save");
        hb.setAlignment(Pos.CENTER);
        vb.setAlignment(Pos.CENTER);
        Button contB = new Button("Continue");
        Button cancB = new Button("Cancel");
        Label l1 = new Label("Are you sure you want to continue?");
        Label l2 = new Label("Your work won't be saved.");

        l1.setAlignment(Pos.CENTER);
        l2.setAlignment(Pos.CENTER);
        contB.setOnMouseClicked(e -> {
            try {
                boardMade = false;
                switchSceneC(new boolean[0][0][0]);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            stage.close();
        });
        cancB.setOnMouseClicked(e -> stage.close());
        hb.getChildren().addAll(contB,cancB);
        vb.getChildren().addAll(l1,l2,hb);
        hb.setStyle("-fx-background-color: #3b3f41");
        vb.setStyle("-fx-background-color: #3b3f41");
        Scene scene = new Scene(vb,200,100);
        scene.getStylesheets().add("/resources/outputStyle.css");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public void nextGenC() {
        removeChildren(cube);
        board.nextStep();
        addValsToGroup(cube, board.getCells());

    }

    public void speedSliderC() {
        timeline.pause();
        this.stepSpeed = (int) speedSlider.getValue();
        timeline =
                new Timeline(new KeyFrame(Duration.millis(stepSpeed), f -> {
                    timeLineLogic();
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        if (modelRunning) {
            playTimeLine();
        } else {
            pauseTimeLine();
        }


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