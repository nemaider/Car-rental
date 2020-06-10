package Client;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
            Socket socket = new Socket("127.0.0.1",5000);
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

/////////////////////////////////////////////////////////////////////////////////////////////////////