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

class ReplyTheRequest implements Runnable
{
    TextArea messages;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    ReplyTheRequest(TextArea messages, Socket socket)
    {
        this.messages = messages;
        this.socket = socket;
    }

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

    public String getCars(Connector connector, String parameters[]) throws SQLException {
        String messageToUser = "0";
        int size = 0;
        ResultSet results = connector.executeQuery(parameters[0],parameters[1],parameters[2],parameters[3]);

        if(results==null)
            size=0;
        else {
            size = results.last() ? results.getRow() : 0;
            results.beforeFirst();
        }

        if(size!=0)
        {
            messageToUser = size + " ";
            while(results.next())
            {
                messageToUser += results.getString("Marka") + " " + results.getString("Model") + " " +
                        results.getString("id_car") + " " + results.getString("logo") + " " +
                        results.getString("cena") + " " + dateDiffrece(parameters[2],parameters[3]) + " ";
            }
        }
        return messageToUser;
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
                String parameters[] = messageFromUser.split("[^A-Za-z0-9-]");

                if(parameters.length == 3)
                {
                    connector.makeInsert(parameters);
                    messageToUser="-1";
                }
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
