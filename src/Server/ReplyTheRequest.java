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
 * klasa ktora odpowiada za odpowiedź na zapytanie od klientow, uruchomiona w nowym watku
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
     * @param messages obiekt w ktorym zapisane sa logi
     * @param socket socket przez ktory serwer komunikuje sie z klientami
     */
    ReplyTheRequest(TextArea messages, Socket socket)
    {
        this.messages = messages;
        this.socket = socket;
    }

    /**
     * funkcja obliczajaca roznice miedzy dwoma dniami
     * @param dateStart data poczatkowa wypozyczenia pojazdu
     * @param dateStop data koncowa wypozyczenia pojazdu
     * @return roznica dni miedzy data poczatkowa a koncowa
     */
    public long dateDiffrece(String dateStart, String dateStop)
    {
        if(dateStart.equals("null") || dateStop.equals("null"))
            return -1;
        long roznica = 0;

        try
        {
            LocalDate d1 = LocalDate.parse(dateStart);
            LocalDate d2 = LocalDate.parse(dateStop);
            roznica = ChronoUnit.DAYS.between(d1,d2);
            System.out.println(roznica);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return roznica;
    }

    /**
     *
     * @param connector obiekt klasy Connector pozwalajacej polaczyc sie serwerowi z baza danych
     * @param parameters dane otrzymane od klienta na temat samochodu(marka, model, data poczatkowa, data koncowa)
     * @return wynik zapytania wraz z liczba wynikow na pierwszym miejscu
     * @throws SQLException wyjatek w przypadku bledu
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
     * funkcja ktora zamienia obiekt na ciag znakow
     * @return wynik zapytania w postaci String
     * @throws SQLException wyrzucony wyjatek w przypadku bledu
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

    public void stop()
    {

    }

    /**
     * nadpisana funkcja klasy rozszerzonej o Runnable ktora wywolywana jest poprzez funkcje start()
     * funkcja nowego watku
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
                //! w przypadku gdy uzytkownik zdecyduje sie na samochod wysylane sa do serwera 3 argumenty
                //! id samochodu, data poczatkowa oraz data koncowa
                if(parameters.length == 3)
                {
                    connector.makeInsert(parameters);
                    messageToUser="-1";
                }
                //! w przypadku kiedy uzytkownik szuka odpowiedniego samochodu dla siebie wysylane do serwaera sa 4 parametry
                //! marka, model, data poczatkowa oraz data koncowa
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
                    System.out.println("nie udalo sie zamknac polaczenia");
                }
            }
        }
    }
}
