package Server;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ServerApplication extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {

        TextArea messages = new TextArea();
        TextField field = new TextField();

        BorderPane fieldPane = new BorderPane();
        fieldPane.setPadding(new Insets(5, 5, 5, 5));

        fieldPane.setCenter(field);
        messages.setEditable(false);

        BorderPane mainPane = new BorderPane();
        mainPane.setTop(new ScrollPane(messages));
        mainPane.setBottom(fieldPane);

        Scene scene = new Scene(mainPane, 450, 230);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Server");
        primaryStage.show();

        Thread ServerHandler = new Thread(new ServerHandler(messages,field));
        ServerHandler.start();
    }

    public static void main(String args[])
    {
        launch(args);
    }
}
