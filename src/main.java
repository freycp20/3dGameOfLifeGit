import controllers.outputController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class main extends Application {

    Parent inputRoot;
    Parent outputRoot;
    Scene input;
    Scene output;
    Stage pStage;
    Paint labelFill;
    @Override
    public void start(Stage primaryStage) throws Exception{
        VBox vbox = new VBox();

        pStage = primaryStage;
        pStage.setTitle("welcome");
        Label inputLabel = new Label("input");
        Label outputLabel = new Label("output");
        Label splitSLabel = new Label("split-screen");
        Label consoleLabel = new Label("console");
//        labelFill = consoleLabel.getTextFill();
        setLabel(inputLabel);
        setLabel(outputLabel);
        setLabel(splitSLabel);
        setLabel(consoleLabel);
        FXMLLoader inputLoader = new FXMLLoader(getClass().getResource("/resources/input.fxml"));
        inputRoot = inputLoader.load();
        FXMLLoader outputLoader = new FXMLLoader(getClass().getResource("/resources/output.fxml"));
        outputRoot = outputLoader.load();
        inputLabel.setOnMouseClicked(e -> {
            input = new Scene(inputRoot, 1200, 600);
            pStage.close();
            pStage.setTitle("Input");
            input.getStylesheets().add("resources/inputStyle.css");
            pStage.setScene(input);
            pStage.centerOnScreen();
            pStage.show();
        });
        outputLabel.setOnMouseClicked(e -> { Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            pStage.setResizable(true);
            pStage.setX(bounds.getWidth()/2);
            output = new Scene(outputRoot, 1200, 600);
            pStage.setTitle("Output");
            outputController outputC = outputLoader.getController();
            outputC.setStyleDark();
            pStage.close();
            pStage.setScene(output);
            pStage.centerOnScreen();
            pStage.show();
        });

//        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(inputLabel,outputLabel,splitSLabel,consoleLabel);
        vbox.setId("pageOptions");
        Scene scene = new Scene(vbox, 300,200);
        scene.getStylesheets().add("resources/welcomePage.css");
        labelFill = inputLabel.getTextFill();
        pStage.setScene(scene);
        pStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
    private void setLabel(Label label){
        Insets insets = new Insets(2,2,2,2);
        label.setPadding(insets);
    }
}