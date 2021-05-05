package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class outputController {
    /**
     * Size of cube
     */
    private int size;
    private int width;
    private int cubeSize;
    private int stepSpeed = 250;

    // variables for mouse interaction
    private double mousePosX, mousePosY;
    private double mouseOldX, mouseOldY;

    // rotation tranformations
    private final Rotate rotateX = new Rotate(20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(-45, Rotate.Y_AXIS);

    // variables regarding board rules
    private boolean modelRunning = false;
    private boolean trueBox = true;
    public boolean trueFirst;
    private LinkedHashSet<Integer> aNeighbors;
    private LinkedHashSet<Integer> dNeighbors;

    // storing vals from input in case of switch back
    protected int yVal=0;
    protected int xVal=0;
    protected int zVal=0;

    // color of lines
    protected Color lineColor;

    // variables regarding fileIO
    public boolean areRules;
    private boolean clickOpen = true;
    private fileIO ifio;
    public boolean boardMade;

    // board and cube members
    private guiBoard board;
    private Group cube;

    // timeline variables
    private Timeline timeline;
    private Timeline rotateTimeline;

    @FXML
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

    /**
     * Initialize is called automatically when the FXMLLoader loads the gui, and instantiates the controller.
     * Since the menu scene is displayed first, not all of the variables in the output gui can be initialized.
     * This method is to initialize the things that can be initialize before the gui is opened,
     * and additionally there is an init method to initialize all of the other class members
     * when this output GUI is opened and visualized.
     */
    public void initialize() {
        ifio = new fileIO();
        timeline =
                new Timeline(new KeyFrame(Duration.millis(stepSpeed), f -> timeLineLogic()));
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

    /**
     * This method is called when the gui is visualized and given values.
     *  initializes all class members
     * @param size the size of the cube
     * @param alive the board of cells
     * @param aNeighbors a list of the alive neighbor rules
     * @param dNeighbors a list of the dead neighbor rules
     * @param xVal the x dimension size
     * @param yVal the y dimension size
     * @param zVal the z dimension size
     * @param trueFirst the trueFirst rule
     * @param areRules hold if there are rules
     */
    public void init(int size, boolean[][][] alive,
                     LinkedHashSet<Integer> aNeighbors,
                     LinkedHashSet<Integer> dNeighbors,
                     int xVal, int yVal, int zVal,
                     boolean trueFirst, boolean areRules) {
        this.areRules = areRules;
        this.trueFirst = trueFirst;
        this.aNeighbors = aNeighbors;
        this.dNeighbors = dNeighbors;
        lineColor = Color.web("#adacac");
        this.size = size;
        cube = createCube(size);
        width = (size/10);
        cubeSize = (size/(size/10));
        // initial cube rotation
        cube.getTransforms().addAll(rotateX, rotateY);
        cube.setTranslateZ(-size);


        if (!boardMade){
            board = new guiBoard(alive, aNeighbors, dNeighbors, xVal, yVal, zVal, trueFirst, areRules);
            boardMade = true;
        }
        // Set Lighting
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
        subScene.setStyle("-fx-background-color: #000000;");
        subScenePane.getChildren().addAll(subScene);
        subScenePane.setId("subScenePane");
        root.setId("cubeGridPane");
        PerspectiveCamera cam = new PerspectiveCamera();
        subScene.setCamera(cam);
        mainBorderPane.setCenter(subScenePane);
        setDragRotate(root);
        makeZoomable(root);
    }


    /**
     * Displays information about the program in a new stage
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

    /**
     * Sets the board rules to the values input in the alive neighbors TextField
     *  if the board is already made and the text box is not empty
     */
    @FXML
    public void aliveNC() {
        if (boardMade) {
            if (!aliveNeighbor.getText().equals("")) {
                aNeighbors = setRules(aliveNeighbor.getText());
                board.setAliveNlist(aNeighbors);
            }
        }
    }

    /**
     * Controls the trueFirst box and updates the value of Boards
     *  trueFirst value
     */
    @FXML
    public void booleanBoxC() {
        if (boardMade) {
            trueBox = !trueBox;
            board.setTrueFirst(trueBox);
            booleanBox.setText(String.valueOf(trueBox));
        }
    }

    /**
     * Sets the board rules to the values input in the dead neighbors TextField
     *  if the board is already made and the text box is not empty
     */
    @FXML
    public void deadNC() {
        if (boardMade) {
            if (!deadNeighbor.getText().equals("")) {
                dNeighbors = setRules(deadNeighbor.getText());
                board.setDeadNlist(dNeighbors);
            }
        }
    }

    /**
     * checks if the board is already made and calls the nextStep method
     *  from board if exists. Updates the cube to reflect the new board
     */
    @FXML
    public void nextGenC() {
        if (boardMade) {
            removeChildren(cube);
            board.nextStep();
            addValsToGroup(cube, board.getCells());
        }
    }

    /**
     * opens file using fileIO instance.
     * Values are read from board and the init method is called
     *  to set the class members.
     * displays opened board
     */
    @FXML
    public void openTemplateC() {

        if (clickOpen){
            board = ifio.openFile();
        }
        if (!(cube == null)) {
            cube.getChildren().clear();
        }
        if (ifio.getBoardOpened()){
            yVal = board.getyVal();
            xVal = board.getxVal();
            zVal = board.getzVal();
            if (board.areRules()){
                init(yVal*10, board.getStartingPos(), board.getAliveNlist(),
                        board.getDeadNlist(), board.getxVal(),board.getyVal(),
                        board.getzVal(), board.getTrueFirst(), true);
                trueBox = !trueFirst;
                aliveNeighbor.setText(stripChars(aNeighbors.toString(), "[]"));
                deadNeighbor.setText(stripChars(dNeighbors.toString(), "[]"));
                booleanBoxC();
                aliveNC();
                deadNC();
            } else {
                LinkedHashSet<Integer> a = new LinkedHashSet<>(List.of(5, 6, 7));
                LinkedHashSet<Integer> d = new LinkedHashSet<>(List.of(6));
                init(yVal*10, board.getStartingPos(), a, d,
                        board.getxVal(),board.getyVal(),
                        board.getzVal(), true, false);

            }
            boardMade = true;
            resetButtonC();
            booleanBoxC();
            booleanBoxC();
        }
        clickOpen = true;
    }

    /**
     * Resets the current board to the original starting board.
     * Stops rotation if rotating.
     */
    @FXML
    public void resetButtonC() {
        if (boardMade) {
            if (modelRunning) {
                runButtonC();
            }
            // Pause timeline if running
            if (rotateTimeline.getStatus().equals(Animation.Status.RUNNING)) {
                rotateTimeline.pause();
            }
            // update board
            removeChildren(cube);
            board.reset();
            addValsToGroup(cube, board.getCells());
            pauseTimeLine();
        }
    }

    /**
     * Rotates the cube indefinitely on the x and y axes.
     * Plays/pauses timeline depending on status
     */
    @FXML
    public void rotateButtonC() {
        if (boardMade) {
            if (rotateTimeline.getStatus().equals(Animation.Status.RUNNING)) {
                rotateTimeline.pause();
            } else {
                rotateTimeline.play();
            }
        }
    }

    /**
     * This button starts the timeline, which runs the model. The boards next step method is
     *  called repeatedly until this button is pressed again.
     */
    @FXML
    public void runButtonC() {
        if (boardMade) {
            if (!modelRunning) {
                playTimeLine();
                runButton.setText("Stop");
                modelRunning = true;
            } else {
                pauseTimeLine();
                runButton.setText("Start");
                modelRunning = false;
            }
        }
    }

    /**
     * Saves the current generation of the board to a file.
     */
    @FXML
    public void saveCurrentGenC() {
        if (boardMade){
            saveTemplate(board.arrayToString(board.getCells()));
        }
    }

    /**
     * Saves the original generation of the board to a file.
     */
    @FXML
    public void saveOriginGenC() {
        if (boardMade){
            saveTemplate(board.arrayToString(board.getStartingPos()));
        }
    }

    /**
     * Slider to control the time in between generations, changing the speed of the model.
     */
    @FXML
    public void speedSliderC() {
        timeline.pause();
        this.stepSpeed = (int) speedSlider.getValue();
        timeline =
                new Timeline(new KeyFrame(Duration.millis(stepSpeed), f -> timeLineLogic()));
        timeline.setCycleCount(Animation.INDEFINITE);
        if (modelRunning) {
            playTimeLine();
        } else {
            pauseTimeLine();
        }


    }

    /**
     * Switches the scene to input
     * @param arr the generation of board that is to be switched to input
     * @throws IOException if error opening a file
     */
    @FXML
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
            inputC.trueFirst = trueFirst;
            inputC.aNeighbors = aNeighbors;
            inputC.dNeighbors = dNeighbors;
        }
    }

    /**
     * Switches scene with current board generation
     * @throws IOException if error opening file.
     */
    @FXML
    public void switchCurrentC() throws IOException {
        if (boardMade){
            switchSceneC(board.getCells());
        } else {
            switchSceneC(new boolean[0][0][0]);
        }
    }

    /**
     * Switches scene with original board generation
     * @throws IOException if error opening file.
     */
    @FXML
    public void switchOriginalC() throws IOException {
        if (boardMade){
            switchSceneC(board.getStartingPos());
        } else {
            switchSceneC(new boolean[0][0][0]);
        }
    }

    /**
     * Switches scene with empty board
     * @throws IOException if error opening file.
     */
    @FXML
    public void switchNewC() throws IOException {
        if (boardMade){
            saveWarning();
        } else {
            switchSceneC(new boolean[0][0][0]);
        }
    }


    /**
     * Pre-made Templates; Opens templates from the templates package that is a part of the project.
     */
    public void checkeredLoafC() {
        preTemplateOpen(new File("src/templates/checkeredLoaf.txt"));
    }
    public void gliderC() {
        preTemplateOpen(new File("src/templates/glider.txt"));
    }
    public void longC() {
        preTemplateOpen(new File("src/templates/long.txt"));
    }
    public void mutableCubeC() {
        preTemplateOpen(new File("src/templates/mutableCube.txt"));
    }
    public void miniCubeC() {
        preTemplateOpen(new File("src/templates/miniCube.txt"));
    }
    public void zoomyBoiC() {
        preTemplateOpen(new File("src/templates/zoomyBoi.txt"));
    }


    /**
     * TimeLine Functions. Handles the logic along with playing and
     * pausing the timeline, and updating the respective buttons
     */
    public void playTimeLine() {
        timeline.play();
        modelRunning = true;
        runButton.setText("Stop");

    }
    public void pauseTimeLine() {
        timeline.pause();
        modelRunning = false;
        runButton.setText("Start");
    }
    public void timeLineLogic() {
        boolean[][][] lastBoard = board.getCells();
        board.nextStep();
        // Pauses if nothing is happening
        if (Arrays.deepEquals(board.getCells(), lastBoard)) {
            pauseTimeLine();
        }
        else {
            removeChildren(cube);
            addValsToGroup(cube, board.getCells());
        }
    }



     // Helper methods for output. These methods control the visualization and the actual
     // rendering of the model.


    /**
     * This method adds the cell array to the 3d cube.
     * @param cube group that boxes will be added to
     * @param cells values to be added to group
     */
    public void addValsToGroup(Group cube, boolean[][][] cells){
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                for (int k = 0; k < cells[i][j].length; k++) {
                    if (cells[i][j][k]) {
                        // Create material for boxes to handle color
                        PhongMaterial material = new PhongMaterial();
                        Box newBox = new Box(cubeSize, cubeSize, cubeSize);
                        newBox.setTranslateX(localize(i));
                        newBox.setTranslateY(localize(j));
                        newBox.setTranslateZ(localize(k));
                        newBox.setStyle("-fx-border-color: #808080");

                        // Distance from center
                        int dx = width/2 - i;
                        int dy = width/2 - j;
                        int dz = width/2 - k;
                        final double MAX_DISTANCE = Math.sqrt(Math.pow(width/2.0, 2) + Math.pow(width/2.0, 2) + Math.pow(width/2.0, 2));
                        double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));

                        // Sets the color as a function of the distance from center. Blue ranges from 1 to 0 as
                        //  distance increases from center, and red ranges from 0 to 1. This results in blue at the
                        //  center of the cube and red at the corners.
                        material.setDiffuseColor(new Color(distance/MAX_DISTANCE,.3,1-distance/MAX_DISTANCE, .9));
                        newBox.setMaterial(material);
                        cube.getChildren().add(newBox);
                    }
                }
            }
        }
    }

    /**
     * localizes a coordinate in terms of the center of the model.
     * @param value the coordinate to be translated
     * @return the translated value
     */
    public int localize(int value) {
        return (size/2)-5 - value*cubeSize;
    }

    /**
     * Sets up the class to open a template. Creates a new instance of fileIO
     *  and opens the file given.
     * @param file the file to be opened
     */
    private void preTemplateOpen(File file){
        ifio = new fileIO();
        ifio.setBoardOpened(true);
        board = ifio.readFile(file);
        clickOpen = false;
        openTemplateC();
    }

    /**
     * Removes all of the boxes from the cube.
     * @param cube the group to remove the boxes from
     */
    public void removeChildren(Group cube) {
        for (Object object : cube.getChildren().toArray()) {
            if (object instanceof Box) {
                cube.getChildren().remove(object);
            }
        }
    }

    /**
     * Saves the template with the given board values
     * @param boo the board values to save
     */
    private void saveTemplate(String boo){
        if (modelRunning){
            runButtonC();
        }
        StringBuilder aN = new StringBuilder();
        StringBuilder dN = new StringBuilder();
        for (Integer alvn : board.getAliveNlist()) {
            aN.append(alvn).append(" ");
        }
        for (Integer ddvn : board.getDeadNlist()){
            dN.append(ddvn).append(" ");
        }
        String content = String.format(
                "%d %d %d %s %s %d %s %d %s\n%s", yVal, xVal, zVal, areRules, trueBox,aNeighbors.size(), aN,dNeighbors.size(), dN, boo);
        new fileIO().saveFile(content);
    }

    /**
     * This is a message that pops up when the user is about to switch back to the input
     *  with a board that is not empty.
     */
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

    /**
     * Sets the style of the gui to dark mode
     */
    public void setStyleDark() {
        mainBorderPane.getScene().getStylesheets().add("resources/outputStyle.css");
    }

    /**
     * allows for the cube to be rotated
     * @param root the stackpane to apply the rotational ability to
     */
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

    /**
     * This method returns a hashset with the rules. Hashset was used because the rules are to be unique,
     *  and using a hashset is a fast and easy way to ensure uniqueness.
     * This method is very robust, as it will delete anything from the string that is not a digit,
     *  using a regex, and return just the numbers in the string in the form of a linkedhashset.
     * @param text the unformatted uncleaned string containing in some way numbers that are the rules.
     * @return the LinkedHashSet containing the rules
     */
    private LinkedHashSet<Integer> setRules(String text){
        LinkedHashSet<Integer> rules = new LinkedHashSet<>();
        // Delimiter of anything but a digit.
        Scanner scnr = new Scanner(text)
                .useDelimiter("[^\\d]");

        while (scnr.hasNext()) {
            String next = scnr.next();
            if (!next.isEmpty()) {
                rules.add(Integer.parseInt(next));
            }
        }
        return rules;
    }

    /**
     * Strips a string of values that it is given
     * @param input the string to be stripped
     * @param strip the characters to remove from the string
     * @return the string without characters that were to be removed.
     */
    public static String stripChars(String input, String strip) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (strip.indexOf(c) == -1) {
                result.append(c);
            }
        }
        return result.toString();
    }


     // Handles the visualization of the cube itself.
     // Creates axes and displays lines for borders.

    /**
     * This subclass creates the axes for the cube.
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
            for (int y = 0; y <= size; y += size) {
                Line line = new Line(0, 0, size, 0);
                line.setStroke(lineColor);
                line.setFill(lineColor);
                line.setTranslateY(y);
                line.setTranslateZ(zTranslate);
                line.setStrokeWidth(lineWidth);
                getChildren().addAll(line);
            }

            for (int x = 0; x <= size; x += size) {
                Line line = new Line(0, 0, 0, size);
                line.setStroke(lineColor);
                line.setFill(lineColor);
                line.setTranslateX(x);
                line.setTranslateZ(zTranslate);
                line.setStrokeWidth(lineWidth);
                getChildren().addAll(line);
            }
        }
    }

    /**
     * Create axis walls
     * @param size size of the cube
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

    /**
     * clamps the value between a min and max
     * @param value the value to be checked.
     * @param min the min value to check it against
     * @param max the max value to check it against
     * @return the clamped value
     */
    public static double clamp(double value, double min, double max) {
        if (Double.compare(value, min) < 0)
            return min;
        if (Double.compare(value, max) > 0)
            return max;
        return value;
    }

    /**
     * This method makes a stackpane able to be zoomable. This allows the user to
     *  zoom in and out and see the model from different distances
     * @param control the stackpane to apply modifications to.
     */
    public void makeZoomable(StackPane control) {
        final double MAX_SCALE = 3;
        final double MIN_SCALE = .05;
        control.addEventFilter(ScrollEvent.ANY, event -> {

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

        });
    }
}