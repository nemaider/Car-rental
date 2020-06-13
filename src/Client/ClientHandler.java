package Client;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;

import java.io.IOException;
import java.net.Socket;

/**
 * Klasa która uruchamia wątek klienta który odbiera wiadomosci od serwera oraz który wysyła wiadomości do serwera
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
     * @param marka obiekt który przechowuje informacje na temat marki samochodu wybranej przez użytkownika
     * @param model obiekt który przechowuje informacje na temat modelu samochodu wybranego przez użytkownika
     * @param start obiekt który przechowuje informacje na temat początkowej daty wypożyczenia samochodu
     * @param end obiekt który przechowuje informacje na temat końcowej daty wypożyczenia samochodu
     * @param check przycisk który po wciśnięciu sprawdza dostępność pojazdu
     * @param offerts obiekt w którym wyświetlane bedą oferty samochodów dostępnych do wyporzyczenia
     * @param close przycisk zamykający okno
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
     * nadpisana funkcja klasy rozszerzonej o Runnable która wywolywana jest poprzez funkcje start()
     * funckja nowego wątku
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
     * funkcja kończąca wątek oraz wyrzucająca błąd jeśli sie to nie uda
     * @throws InterruptedException
     */
    public void endThreads() throws InterruptedException
    {
        getMessage.sleep(100);
        getMessage.interrupt();
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////