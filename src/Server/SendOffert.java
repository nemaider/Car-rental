package Server;

import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * klasa odpowiedzialna za wysyłanie informacji do użytkowników z poziomu serwera
 *  z racji iz klasa posiada liste uzytkownikow mozemy wybrac pojedynczego uzytkownika
 */
class SendOffert implements Runnable
{
    TextArea messages;
    TextField field;
    List sockets;

    SendOffert(TextArea messages, TextField field, List sockets)
    {
        this.messages = messages;
        this.field = field;
        this.sockets = sockets;
    }

    /**
     * funkcja pozwalająca wysłać komunikat do użytkownika
     */
    public void sendMessage()
    {
        String messageToUser = "-2 " + field.getText();

        for(int i=0;i<sockets.size();i++)
        {
            try
            {
                Socket socket = (Socket) sockets.get(i);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                field.clear();
                out.writeUTF(messageToUser);
                messages.appendText("Server: " + field.getText() + "\n");
            }
            catch (IOException e)
            {
                messages.appendText("Brak podlaczonych uzytkownikow\n");
                field.clear();
            }
        }
    }
    /**
     * nadpisana funkcja klasy rozszerzonej o Runnable która wywolywana jest poprzez funkcje start()
     * funkcja nowego wątku
     */
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
