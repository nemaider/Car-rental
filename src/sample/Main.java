package sample;

import Client.ClientHandler;
import DBConnect.Connector;
import ValuesSetter.ValueSetter;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;

public class Main extends Application
{
    ///zmienne x i y określające położenie okna aplikacji na pulpicie
    private double x,y;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        Scene scena = new Scene(root, 510, 561);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scena);
        primaryStage.show();

        ComboBox marka = (ComboBox) scena.lookup("#Marka");
        ComboBox model = (ComboBox) scena.lookup("#Model");
        DatePicker odkiedy = (DatePicker) scena.lookup("#OdKiedy");
        DatePicker dokiedy = (DatePicker) scena.lookup("#DoKiedy");
        Button check = (Button) scena.lookup("#Check");
        Button close = (Button) scena.lookup("#Close");
        ScrollPane offerts = (ScrollPane) scena.lookup("#scroll");

        ValueSetter valueSetter = new ValueSetter();
        valueSetter.setMarka(marka);

        ClientHandler client = new ClientHandler(marka,model,odkiedy,dokiedy,check,offerts,close);
        Thread clientHandler = new Thread(client);
        clientHandler.start();
        clientHandler.join();

        close.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                try
                {
                    client.endThreads();
                    System.exit(1);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });

        root.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                x = primaryStage.getX() - event.getScreenX();
                y = primaryStage.getY() - event.getScreenY();
            }
        });

        root.setOnMouseDragged(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                primaryStage.setX(event.getScreenX() + x);
                primaryStage.setY(event.getScreenY() + y);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

