package DBConnect;

import java.sql.*;

/**
 * klasa która łączy sie z bazą danych
 */
public class Connector
{
    public Connection conn;

    /**
     * funkcja która relizuje połączenie z bazą danych
     */
    public void connect()
    {
        conn = null;

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://54.38.50.59/www2857_events","www2857_events","8GJoBGoc3WUzkUA2rAn3");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * funkcja która pobiera informacje o markach z bazy
     * @return wynik zapytania zawierający każdą marke w bazie
     */
    public ResultSet getBrands() throws SQLException {
        Statement myQuery = conn.createStatement();
        ResultSet result = myQuery.executeQuery("select distinct marka from cars");
        return result;
    }

    /**
     * funkcja pobierająca modele z bazy danych które dotyczą danej marki
     * @param brand marka którą zinteresowany jest klient
     * @return wynik zapytania zwierający każdy model danej marki z bazy
     */
    public ResultSet getModel(String brand) throws SQLException {
        Statement myQuery = conn.createStatement();
        ResultSet result = myQuery.executeQuery("select distinct model from cars where marka = '" + brand + "'");
        return result;
    };

    /**
     * funkcja która sprawdza czy pojazd interesujący klienta jest dostępny w danym przedziale czasowym
     * @param marka marka pojazdu która interesuje użytkownika
     * @param model model pojzadu który interesuje użytkownika
     * @param start data wypożyczenia pojazdu
     * @param end data końca wypożyczenia pojazdu
     * @return wynik zapytania która zawiera wszyskie pozycje spełniające kryteria oraz dostępne w danym okresie
     */
    public ResultSet executeQuery(String marka, String model, String start, String end)
    {
        ResultSet results = null;
        try
        {

            Statement myQuery = conn.createStatement();

            ResultSet resultSet = myQuery.executeQuery("select count(*) from rental join cars on rental.id_car=cars.id_car where marka='" + marka + "' and model='" + model +
                    "' and ('" + start + "' between start and end and '" + end + "' between start and end)");

                while (resultSet.next())
                {
                    int columnValue = resultSet.getInt(1);

                    if (columnValue == 0) {
                        String query = "select distinct cars.id_car,cars.marka,cars.model,cars.logo,cars.cena from cars where marka='" + marka + "' and model='" + model + "'";
                        results = myQuery.executeQuery(query);
                    }
                    break;
                }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * funkcja która wykonuje zapytanie rezerwujące dany samochod w danym okresie czasowym
     * @param parameters informacje o pojeździe oraz okresie na który ma zostać zarezerwowany
     */
    public void makeInsert(String []parameters) throws SQLException
    {
        String carID = parameters[0];
        String rentStart = parameters[1];
        String rentEnd = parameters[2];
        System.out.println("wykonuje insert");

        Statement myQuery = conn.createStatement();
        String query = "INSERT INTO `rental`(`id_car`, `start`, `end`) VALUES ('" + carID + "','" + rentStart + "','" + rentEnd + "')";
        myQuery.executeUpdate(query);
    }
}
