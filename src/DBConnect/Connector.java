package DBConnect;

import java.sql.*;

public class Connector
{
    public Connection conn;

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
                    System.out.println(columnValue);
                    if (columnValue == 0) {
                        String query = "select distinct cars.id_car,cars.marka,cars.model from rental right join cars on rental.id_car=cars.id_car where marka='" + marka + "' and model='" + model + "'";
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
