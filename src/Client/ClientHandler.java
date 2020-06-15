package Client;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;

import java.io.IOException;
import java.net.Socket;

/**
 * Klasa ktora uruchamia watek klienta ktory odbiera wiadomosci od serwera oraz ktory wysyla wiadomosci do serwera
 */
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

    /**
     * @param marka obiekt ktory przechowuje informacje na temat marki samochodu wybranej przez uzytkownika
     * @param model obiekt ktory przechowuje informacje na temat modelu samochodu wybranego przez uzytkownika
     * @param start obiekt ktory przechowuje informacje na temat poczatkowej daty wypozyczenia samochodu
     * @param end obiekt ktory przechowuje informacje na temat koncowej daty wypozyczenia samochodu
     * @param check przycisk ktory po wcisnieciu sprawdza dostepnosc pojazdu
     * @param offerts obiekt w ktorym wyswietlane beda oferty samochodow dostepnych do wyporzyczenia
     * @param close przycisk zamykajacy okno
     */
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

    /**
     * nadpisana funkcja klasy rozszerzonej o Runnable ktora wywolywana jest poprzez funkcje start()
     * funckja nowego watku
     */
    @Override
    public void run()
    {
        try
        {
            Socket socket = new Socket("127.0.0.1",5000);
            System.out.println("polaczono z serwerem");

            sendMessage = new SendMessage(marka,model,start,end,check,socket);
            getMessage = new Thread(new GetMessage(socket, offerts,sendMessage));
            sendMessage.queryHandler();
            getMessage.start();
            //sendMessage.join();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("Blad polaczenia");
        }
    }

    /**
     * funkcja konczaca watek oraz wyrzucajaca blad jesli sie to nie uda
     * @throws InterruptedException
     */
    public void endThreads() throws InterruptedException
    {
        getMessage.sleep(100);
        getMessage.interrupt();
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////