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
 * klasa ktora odpowiada za wysylanie wiadomosci do serwera
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
     * @param marka obiekt ktory przechowuje informacje na temat marki samochodu wybranej przez uzytkownika
     * @param model obiekt ktory przechowuje informacje na temat modelu samochodu wybranego przez uzytkownika
     * @param start obiekt ktory przechowuje informacje na temat poczatkowej daty wypozyczenia samochodu
     * @param end obiekt ktory przechowuje informacje na temat koncowej daty wypozyczenia samochodu
     * @param check przycisk ktory po wcisnieciu sprawdza dostepnosc pojazdu
     * @param socket obiekt klasy socket pozwalajacy na komunikacje miedzy klientami a serwerem
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
     * funkcja ktorej uzywamy do zarezerwowania samochodu o danym id w danym przedziale czasowym
     * @param carID id samochodu ktory chcemy zarezerwowac
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
     * funkcja ktora dodaje evenhandlej do przycisku ktory odpowiada za wyslanie zapytania do serwera poprzez socket
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
