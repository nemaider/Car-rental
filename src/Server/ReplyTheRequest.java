package Server;

import DBConnect.Connector;
import javafx.scene.control.TextArea;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * klasa która odpowiada za odpowiedź na zapytanie od klientów, uruchomiona w nowym wątku
 */
class ReplyTheRequest implements Runnable
{
    TextArea messages;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    String messageToUser;
    int size;
    ResultSet results;
    String rentalStart;
    String rentalStop;

    /**
     * Konstruktor
     * @param messages obiekt w którym zapisane są logi
     * @param socket socket przez który serwer komunikuje się z klientami
     */
    ReplyTheRequest(TextArea messages, Socket socket)
    {
        this.messages = messages;
        this.socket = socket;
    }

    /**
     * funkcja obliczająca różnice między dwoma dniami
     * @param dateStart data poczatkowa wypożyczenia pojazdu
     * @param dateStop data końcowa wypożyczenia pojazdu
     * @return róznica dni miedzy data początkową a końcową
     */
    public long dateDiffrece(String dateStart, String dateStop)
    {
        if(dateStart.equals("null") || dateStop.equals("null"))
            return -1;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        Date d1,d2;
        long diffDays = 0;
        long diff;

        try
        {
            System.out.println(dateStart + " " + dateStop);
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);
            diff = d2.getTime() - d1.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);
            System.out.print(diffDays);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return diffDays;
    }

    /**
     *
     * @param connector obiekt klasy Connector pozwalającej połączyć się serwerowi z bazą danych
     * @param parameters dane otrzymane od klienta na temat samochodu(marka, model, data początkowa, data końcowa)
     * @return wynik zapytania wraz z liczba wyników na pierwszym miejscu
     * @throws SQLException wyjątek w przypadku błędu
     */
    public String getCars(Connector connector, String parameters[]) throws SQLException {
        messageToUser = "0";
        size = 0;
        results = connector.executeQuery(parameters[0],parameters[1],parameters[2],parameters[3]);
        rentalStart = parameters[2];
        rentalStop = parameters[3];

        if(results==null)
            size=0;
        else {
            size = results.last() ? results.getRow() : 0;
            results.beforeFirst();
        }

        if(size!=0)
            messageToUser = executeQuery();

        return messageToUser;
    }

    /**
     * funkcja która zamienia obiekt na ciąg znaków
     * @return wynik zapytania w postaci String
     * @throws SQLException wyrzucony wyjątek w przypadku błędu
     */
    public String executeQuery() throws SQLException {
        messageToUser = size + " ";
        while(results.next())
        {
            messageToUser += results.getString("Marka") + " " + results.getString("Model") + " " +
                    results.getString("id_car") + " " + results.getString("logo") + " " +
                    results.getString("cena") + " " + dateDiffrece(rentalStart,rentalStop) + " ";
        }
        return messageToUser;
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
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Connector connector = new Connector();
            connector.connect();

            while(true)
            {
                String messageFromUser = in.readUTF();
                String messageToUser = "0";
                String parameters[] = messageFromUser.split("[^A-Za-z0-9-]");
                //! w przypadku gdy użytkownik zdecyduje sie na samochód wysyłane są do serwera 3 argumenty
                //! id samochodu, data początkowa oraz data końcowa
                if(parameters.length == 3)
                {
                    connector.makeInsert(parameters);
                    messageToUser="-1";
                }
                //! w przypadku kiedy użytkownik szuka odpowiedniego samochodu dla siebie wysyłane do serwaera sa 4 parametry
                //! marka, model, data początkowa oraz data końcowa
                else if(parameters.length == 4)
                    messageToUser = getCars(connector, parameters);

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
