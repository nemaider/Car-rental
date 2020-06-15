package DBConnect;

import java.sql.*;

/**
 * klasa ktora laczy sie z baza danych
 */
public class Connector
{
    public Connection conn;

    /**
     * funkcja ktora relizuje polaczenie z baza danych
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
     * funkcja ktora pobiera informacje o markach z bazy
     * @return wynik zapytania zawierajacy kazda marke w bazie
     */
    public ResultSet getBrands() throws SQLException {
        Statement myQuery = conn.createStatement();
        ResultSet result = myQuery.executeQuery("select distinct marka from cars");
        return result;
    }

    /**
     * funkcja pobierajaca modele z bazy danych ktore dotycza danej marki
     * @param brand marka ktora zinteresowany jest klient
     * @return wynik zapytania zwierajacy kazdy model danej marki z bazy
     */
    public ResultSet getModel(String brand) throws SQLException {
        Statement myQuery = conn.createStatement();
        ResultSet result = myQuery.executeQuery("select distinct model from cars where marka = '" + brand + "'");
        return result;
    };

    /**
     * funkcja ktora sprawdza czy pojazd interesujacy klienta jest dostepny w danym przedziale czasowym
     * @param marka marka pojazdu ktora interesuje uzytkownika
     * @param model model pojzadu ktory interesuje uzytkownika
     * @param start data wypozyczenia pojazdu
     * @param end data konca wypozyczenia pojazdu
     * @return wynik zapytania ktora zawiera wszyskie pozycje spelniajace kryteria oraz dostepne w danym okresie
     */
    public ResultSet executeQuery(String marka, String model, String start, String end)
    {
        ResultSet results = null;
        try
        {

            Statement myQuery = conn.createStatement();

            ResultSet resultSet = myQuery.executeQuery("select count(*) from rental join cars on rental.id_car=cars.id_car where marka='" + marka + "' and model='" + model +
                    "' and ('" + start + "' between start and end or '" + end + "' between start and end)");

                while (resultSet.next())
                {
                    int columnValue = resultSet.getInt(1);

                    System.out.println(columnValue);
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
     * funkcja ktora wykonuje zapytanie rezerwujace dany samochod w danym okresie czasowym
     * @param parameters informacje o pojezdzie oraz okresie na ktory ma zostac zarezerwowany
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
