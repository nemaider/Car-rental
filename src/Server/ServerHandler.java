package Server;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * klasa która obsługuje nowe watki serwera
 */
public class ServerHandler implements Runnable
{
    static List sockets = new ArrayList();
    TextArea messages;
    TextField field;

    /**
     *
     * @param messages obiekt w którym wypisywane są logi dla serwera
     * @param field obiekt w którym administrator może wpisać informacje dla użytkowników
     */
    ServerHandler(TextArea messages, TextField field)
    {
        this.field = field;
        this.messages = messages;
    }
    /**
     * nadpisana funkcja klasy rozszerzonej o Runnable która wywolywana jest poprzez funkcje start()
     * funkcja nowego wątku
     */
    @Override
    public void run()
    {
        try
        {
            ServerSocket server = new ServerSocket(5000);

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

                Thread waitForRequest = new Thread(new ReplyTheRequest(messages,socket));
                Thread sendRemoteMessage = new Thread(new SendOffert(messages,field,sockets));
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