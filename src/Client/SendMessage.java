package Client;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
                        /////////System.out.println("wiadomosc do servera: " + messageToServer);
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
