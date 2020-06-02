package Server;

import DBConnect.Connector;
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
import java.sql.ResultSet;
import java.sql.SQLException;
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
                Thread sendRemoteMessage = new Thread(new SendOfert(messages,field,sockets));
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

class SendOfert implements Runnable
{
    TextArea messages;
    TextField field;
    List sockets;

    SendOfert(TextArea messages, TextField field, List sockets)
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
            Connector connector = new Connector();
            connector.connect();

            while(true)
            {
                String messageFromUser = in.readUTF();
                String messageToUser = "0";
                int size;

                String parameters[] = messageFromUser.split("[^A-Za-z0-9-]");

                /*if(parameters.length == 3)
                {
                    connector.makeInsert(parameters);
                    messageToUser="-1";
                }*/

                if(parameters.length == 4 /*&& parameters[0]!=null && parameters[3]!=null && !parameters[2].equals("null")*/)
                {
                    ResultSet results = connector.executeQuery(parameters[0],parameters[1],parameters[2],parameters[3]);

                    if(results==null)
                        size=0;
                    else {
                        size = results.last() ? results.getRow() : 0;
                        results.beforeFirst();
                        System.out.println("rozmiar: " + size);
                    }

                    if(size!=0)
                    {
                        messageToUser = size + " ";
                        while(results.next())
                        {
                            messageToUser += results.getString("Marka") + " " + results.getString("Model") + " " + results.getString("id_car") + " ";
                        }
                    }
                }

                out.writeUTF(messageToUser);

                messages.appendText("Klient: " + messageFromUser + "\n");
                messages.appendText("Server: Odsylam klientowi dane\n");
            }
        }
        catch (IOException | SQLException e)
        {
            System.out.println("Klient odlaczyl sie");
            e.printStackTrace();
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

