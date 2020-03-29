package Client;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    ComboBox marka;
    ComboBox model;
    DatePicker start;
    DatePicker end;
    Button check;
    ScrollPane offerts;
    Button close;
    Thread getMessage;
    Thread sendMessage;

    public ClientHandler(ComboBox marka, ComboBox model, DatePicker start, DatePicker end,Button check,ScrollPane offerts,Button close)
    {
        this.marka = marka;
        this.model = model;
        this.start = start;
        this.end = end;
        this.check = check;
        this.offerts = offerts;
        this.close = close;

    }

    @Override
    public void run()
    {
        try
        {
            Socket socket = new Socket("127.0.0.1",4999);
            System.out.println("polaczono z serwerem");

            getMessage = new Thread(new GetMessage(socket, offerts));
            sendMessage = new Thread(new SendMessage(marka,model,start,end,check,socket));
            sendMessage.start();
            getMessage.start();
            sendMessage.join();
        }
        catch(IOException | InterruptedException e)
        {
            e.printStackTrace();
            System.out.println("Blad polaczenia");
        }
    }

    public void endThreads() throws InterruptedException
    {
        sendMessage.sleep(1000);
        sendMessage.interrupt();
        getMessage.sleep(100);
        getMessage.interrupt();
    }
}

class GetMessage implements Runnable
{
    Socket socket;
    ScrollPane offertsScrollBox;

    GetMessage(Socket socket,ScrollPane offerts)
    {
        this.offertsScrollBox = offerts;
        this.socket = socket;

    }

    @Override
    public void run()
    {
        try
        {
            DataInputStream in = new DataInputStream(socket.getInputStream());

            while(true)
            {
                String messageFromServer = in.readUTF();

                System.out.println("server: " + messageFromServer);
                Platform.runLater(()->
                {
                    try
                    {
                        String parameters[] = messageFromServer.split("[^A-Za-z0-9]");

                        showOfferts(Integer.valueOf(parameters[0]),parameters);
                    }
                    catch
                    (IOException e)
                    {
                        e.printStackTrace();
                    }
                });
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
//////////////////////////////oferty////////////////////////////////////////

    public void showOfferts(int j,String parameters[]) throws IOException
    {
        //////////////////pane z wszystkimi ofertami
        offertsScrollBox.setPrefWidth((j-1)*125);
        Pane offertsMainPane = new Pane();
        offertsMainPane.setPrefSize(offertsScrollBox.getHeight(),(j-1)*125);
        offertsScrollBox.setContent(offertsMainPane);
        /////////////////oferty
        Pane offert[] = new Pane[j];

        if(j <= 0)
        {
            Label noOfferts = new Label("Nie ma oferty dla ciebie");
            noOfferts.setFont(Font.font(20));
            noOfferts.setLayoutX(offertsScrollBox.getWidth()/2-120);
            noOfferts.setLayoutY(40);
            offertsMainPane.getChildren().add(noOfferts);
        }

        for(int i=0;i<j;i++)
        {
            offert[i] = new Pane();
            offert[i].setBackground(new Background(new BackgroundFill(Color.web("#44515F"), CornerRadii.EMPTY, Insets.EMPTY)));
            offert[i].setPrefSize(offertsScrollBox.getWidth()-100,100);
            offert[i].setLayoutY(i*105+25);
            offert[i].setLayoutX(50);
            ////////dodawanie oferty
            offertsMainPane.getChildren().add(offert[i]);

            Label name = new Label(parameters[1]);
            name.setLayoutX(20);
            name.setLayoutY(20);

            Label description = new Label("model: " + parameters[i+2]);
            description.setLayoutX(20);
            description.setLayoutY(40);
            ///////dodawanie nazwy samochodu i opisu
            offert[i].getChildren().add(name);
            offert[i].getChildren().add(description);
        }
    }
}
/////////////////////////////////////////////////////////////////////////////
class SendMessage implements Runnable
{
    Button check;
    ComboBox marka;
    ComboBox model;
    DatePicker start;
    DatePicker end;
    Socket socket;

    SendMessage(ComboBox marka, ComboBox model, DatePicker start, DatePicker end,Button check, Socket socket)
    {
        this.marka = marka;
        this.model = model;
        this.start = start;
        this.end = end;
        this.socket = socket;
        this.check = check;
    }

    @Override
    public void run()
    {
        try
        {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            check.setOnMousePressed(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent mouseEvent)
                {
                    try
                    {
                        String messageToServer = "";
                        messageToServer = String.format("%s %s %s %s", marka.getValue(), model.getValue(), start.getValue(), end.getValue());

                        out.writeUTF(messageToServer);
                        System.out.println("wiadomosc do servera: " + messageToServer);
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}