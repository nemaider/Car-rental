package Client;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
    SendMessage sendMessage;

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

            sendMessage = new SendMessage(marka,model,start,end,check,socket);
            getMessage = new Thread(new GetMessage(socket, offerts,sendMessage));
            sendMessage.sendToQuery();
            getMessage.start();
            //sendMessage.join();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("Blad polaczenia");
        }
    }

    public void endThreads() throws InterruptedException
    {
//        sendMessage.sleep(1000);
//        sendMessage.interrupt();
        getMessage.sleep(100);
        getMessage.interrupt();
    }
}

class GetMessage implements Runnable
{
    Socket socket;
    ScrollPane offertsScrollBox;
    SendMessage sendMessage;
    Label noOfferts;
    Pane offert[];
    int result;

    GetMessage(Socket socket,ScrollPane offerts, SendMessage sendMessage)
    {
        this.offertsScrollBox = offerts;
        this.socket = socket;
        this.sendMessage = sendMessage;
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

                Platform.runLater(()->
                {
                    try
                    {
                        String parameters[] = messageFromServer.split("[^A-Za-z0-9-]");
                        result = Integer.parseInt(parameters[0]);
                        showCars(result,parameters);
                    }
                    catch
                    (IOException  e)
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

    public void showCars(int j, String parameters[]) throws IOException
    {
        //////////////////pane z wszystkimi ofertami
        Pane offertsMainPane = new Pane();
        offertsScrollBox.setContent(offertsMainPane);

        if(j <= 0)
        {
            offertsScrollBox.setPrefWidth(125);
            offertsMainPane.setPrefSize(offertsScrollBox.getHeight(),(125));
            if(j==0)
            {
                noOfferts = new Label("Nie ma oferty dla ciebie");
                noOfferts.setLayoutX(offertsScrollBox.getWidth()/2-120);
            }
            else
            {
                noOfferts = new Label("Zarezerwowano");
                noOfferts.setLayoutX(offertsScrollBox.getWidth()/2-90);
            }

            noOfferts.setFont(Font.font(20));
            noOfferts.setLayoutY(40);
            offertsMainPane.getChildren().add(noOfferts);
        }
        else
        {
            offertsScrollBox.setPrefWidth((j-1)*125);
            offertsMainPane.setPrefSize(offertsScrollBox.getHeight(),(j)*125);
            offert = new Pane[j];
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

            Label vin = new Label("vin: " + parameters[3*i+3]);
            vin.setLayoutX(90);
            vin.setLayoutY(20);

            Label name = new Label(parameters[3*i+1]);
            name.setLayoutX(20);
            name.setLayoutY(20);

            Label description = new Label("model: " + parameters[3*i+2]);
            description.setLayoutX(20);
            description.setLayoutY(40);

            Button rentalButton = new Button("zarezerwuj");
            rentalButton.setLayoutX(offert[i].getPrefWidth()-150);
            rentalButton.setLayoutY(35);

            ///////dodawanie nazwy samochodu i opisu
            offert[i].getChildren().add(name);
            offert[i].getChildren().add(description);
            offert[i].getChildren().add(rentalButton);
            offert[i].getChildren().add(vin);

            int finalI = i;
            rentalButton.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent)
                {
                    sendMessage.sendToInsert(parameters[finalI +3]);
                }
            });
        }
    }
}
/////////////////////////////////////////////////////////////////////////////////////////////////////

class SendMessage
{
    Button check;
    ComboBox marka;
    ComboBox model;
    DatePicker start;
    DatePicker end;
    Socket socket;
    DataOutputStream out;
    String messageToInsert;

    SendMessage(ComboBox marka, ComboBox model, DatePicker start, DatePicker end,Button check, Socket socket)
    {
        this.marka = marka;
        this.model = model;
        this.start = start;
        this.end = end;
        this.socket = socket;
        this.check = check;
    }


    public void sendToInsert(String carID)
    {
        try
        {
            out = new DataOutputStream(socket.getOutputStream());
            messageToInsert = "";
            messageToInsert = String.format("%s %s %s", carID, start.getValue(), end.getValue());
            out.writeUTF(messageToInsert);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendToQuery()
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