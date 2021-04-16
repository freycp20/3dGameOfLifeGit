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
import javafx.stage.Screen;
import javafx.stage.Stage;

public class main extends Application {
    Parent inputRoot;
    Parent outputRoot;
    Scene input;
    Scene output;
    Stage pStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        pStage = primaryStage;
        pStage.setTitle("welcome");
        Label inputLabel = new Label("input");
        Label outputLabel = new Label("output");
        Label splitSLabel = new Label("split-screen");
        Label consoleLabel = new Label("console");
        setLabel(inputLabel);
        setLabel(outputLabel);
        setLabel(splitSLabel);
        setLabel(consoleLabel);
        inputRoot = FXMLLoader.load(getClass().getResource("resources/input.fxml"));
        outputRoot = FXMLLoader.load(getClass().getResource("resources/output.fxml"));
        inputLabel.setOnMouseClicked(e -> {
            input = new Scene(inputRoot, 1200, 600);
            pStage.close();
            pStage.setScene(input);
            pStage.centerOnScreen();
            pStage.show();
        });
        outputLabel.setOnMouseClicked(e -> { Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setResizable(true);
            primaryStage.setX(bounds.getWidth()/2);
            output = new Scene(outputRoot, 1200, 600);
            pStage.close();
            pStage.setScene(output);
            pStage.centerOnScreen();
            pStage.show();
        });

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(inputLabel,outputLabel,splitSLabel,consoleLabel);
        Scene scene = new Scene(vbox, 300,200);
        pStage.setScene(scene);
        pStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
    private void setLabel(Label label){
        Insets insets = new Insets(2,2,2,2);
        label.setPadding(insets);
        label.setOnMouseEntered(e -> {
            label.setTextFill(Color.BLUE);
            label.setUnderline(true);
        });
        label.setOnMouseExited(e -> {
            label.setTextFill(Color.BLACK);
            label.setUnderline(false);
        });
    }
}