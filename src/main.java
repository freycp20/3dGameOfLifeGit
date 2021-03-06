import controllers.consoleInput;
import controllers.outputController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class main extends Application {
    Parent inputRoot;
    Parent outputRoot;
    Scene input;
    Scene output;
    Stage pStage;

    /**
     * Welcome page with various options to traverse the program.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        // welcome page
        VBox vbox = new VBox();
        pStage = primaryStage;
        pStage.setTitle("welcome");
        Label inputLabel = new Label("input");
        Label outputLabel = new Label("output");
        Label consoleLabel = new Label("console");

        // get individual loaders for input/output, separate FXMLLoader & .load() to manipulate both objects
        FXMLLoader inputLoader = new FXMLLoader(getClass().getResource("/resources/input.fxml"));
        inputRoot = inputLoader.load();
        FXMLLoader outputLoader = new FXMLLoader(getClass().getResource("/resources/output.fxml"));
        outputRoot = outputLoader.load();

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        outputController outputC = outputLoader.getController();
        // set resizable and maintain stage center
        // add titles, set stylesheets, set size, close, load, and show stage
        inputLabel.setOnMouseClicked(e -> {
            pStage.setResizable(true);
            pStage.setX(bounds.getWidth()/2);
            input = new Scene(inputRoot, 1200, 600);
            pStage.close();
            pStage.setTitle("Input");
            input.getStylesheets().add("resources/inputStyle.css");
            pStage.setScene(input);
            pStage.centerOnScreen();
            pStage.show();
        });
        outputLabel.setOnMouseClicked(e -> {
            pStage.setResizable(true);
            pStage.setX(bounds.getWidth()/2);
            output = new Scene(outputRoot, 1200, 600);
            pStage.setTitle("Output");
            outputC.setStyleDark();
            pStage.close();
            pStage.setScene(output);
            pStage.centerOnScreen();
            pStage.show();
        });
        // close stage and load console
        consoleLabel.setOnMouseClicked(e -> {
            pStage.close();
            consoleInput i = new consoleInput();
            i.runConsole();
        });
        // center welcome pane, set ID, set size, set style, show stage
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(inputLabel,outputLabel,consoleLabel);
        vbox.setId("pageOptions");
        Scene scene = new Scene(vbox, 300,200);
        scene.getStylesheets().add("resources/welcomePage.css");
        pStage.setScene(scene);
        pStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}