package Server;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler implements Runnable
{
    static List sockets = new ArrayList();
    TextArea messages;
    TextField field;

    ServerHandler(TextArea messages, TextField field)
    {
        this.field = field;
        this.messages = messages;
    }

    @Override
    public void run()
    {
        try
        {
            ServerSocket server = new ServerSocket(4999);

            Platform.runLater(()->{
                messages.appendText("Server started...\n");
            });

            while(true)
            {
                Socket socket = server.accept();
                sockets.add(socket);
                Platform.runLater(()->
                {
                    messages.appendText("Client connected\n");
                    messages.appendText("Creating new threads\n");
                });

                Thread waitForRequest = new Thread(new AnserTheRequest(messages,socket));
                Thread sendRemoteMessage = new Thread(new SendMessage(messages,field,sockets));
                waitForRequest.start();
                sendRemoteMessage.start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

class SendMessage implements Runnable
{
    TextArea messages;
    TextField field;
    List sockets;

    SendMessage(TextArea messages, TextField field, List sockets)
    {
        this.messages = messages;
        this.field = field;
        this.sockets = sockets;
    }

    public void sendMessage()
    {
        String messageToUser = field.getText();

        for(int i=0;i<sockets.size();i++)
        {
            try
            {
                Socket socket = (Socket) sockets.get(i);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                field.clear();
                out.writeUTF(messageToUser);
                messages.appendText("Server: " + messageToUser + "\n");
            }
            catch (IOException e)
            {
                messages.appendText("Brak podlaczonych uzytkownikow\n");
                field.clear();
            }
        }
    }

    @Override
    public void run()
    {
        field.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                if(event.getCode() == KeyCode.ENTER)
                {
                    sendMessage();
                }
            }
        });
    }
}

class AnserTheRequest implements Runnable
{
    TextArea messages;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    AnserTheRequest(TextArea messages, Socket socket)
    {
        this.messages = messages;
        this.socket = socket;
    }

    @Override
    public void run()
    {
        try
        {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            while(true)
            {
                String messageFromUser = in.readUTF();
                String messageToUser = "0";

                String parameters[] = messageFromUser.split("[^A-Za-z0-9]");

                if(parameters[0].equalsIgnoreCase("bimmer"))
                {
                    messageToUser = "1 bimmer x5";
                }
                if(parameters[0].equalsIgnoreCase("benz"))
                {
                    messageToUser = "3 benz cls 63amg chujwieco";
                }

                out.writeUTF(messageToUser);

                messages.appendText("Klient: " + messageFromUser + "\n");
                messages.appendText("Server: Odsylam klientowi dane\n");
            }
        }
        catch (IOException e)
        {
            System.out.println("Klient odlaczyl sie");
            if(socket != null)
            {
                try
                {
                    in.close();
                    out.close();
                }
                catch
                (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
}

