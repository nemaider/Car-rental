package Client;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * klasa która odpowiada za wysyłanie wiadomości do serwera
 */
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

    /**
     *
     * @param marka obiekt który przechowuje informacje na temat marki samochodu wybranej przez użytkownika
     * @param model obiekt który przechowuje informacje na temat modelu samochodu wybranego przez użytkownika
     * @param start obiekt który przechowuje informacje na temat początkowej daty wypożyczenia samochodu
     * @param end obiekt który przechowuje informacje na temat końcowej daty wypożyczenia samochodu
     * @param check przycisk który po wciśnięciu sprawdza dostępność pojazdu
     * @param socket obiekt klasy socket pozwalający na komunikacje między klientami a serwerem
     */
    SendMessage(ComboBox marka, ComboBox model, DatePicker start, DatePicker end,Button check, Socket socket)
    {
        this.marka = marka;
        this.model = model;
        this.start = start;
        this.end = end;
        this.socket = socket;
        this.check = check;
    }

    /**
     * funkcja której używamy do zarezerwowania samochodu o danym id w danym przedziale czasowym
     * @param carID id samochodu który chcemy zarezerwować
     */
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

    /**
     * funkcja która dodaje evenhandlej do przycisku który odpowiada za wysłanie zapytania do serwera poprzez socket
     */
    public void queryHandler()
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
