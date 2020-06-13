package ValuesSetter;

import DBConnect.Connector;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * klasa odpowiedzialna za zasilenie aplikacji kilenta aktualnymi danymi
 */
public class ValueSetter {

    public static ComboBox Marka;
    public static ComboBox Model;

    public static DatePicker OdKiedy;
    public static DatePicker DoKiedy;

    /**
     * funkcja wczytuje marki pojazdów dostępnych w bazie
     * @param Marka obiekt w którym użytkownik ma wybór marki interesującego go pojazdu
     * @throws SQLException wyjątek wyrzucany w przypadku braku danych
     */
    public void setMarka(ComboBox Marka) throws SQLException {
        Connector conn = new Connector();
        conn.connect();
        ResultSet result = conn.getBrands();

        while(result.next())
        {
            Marka.getItems().add(result.getString("marka"));
        }
    }

    /**
     * funkcja wczytuje do aplikacji modele samochodów danej marki
     * @throws SQLException wyjątek wyrzucany w przypadku braku danych
     */
    public static void setModel() throws SQLException {
        if(Model.getItems().isEmpty() && Marka.getValue()!=null)
        {
            Connector conn = new Connector();
            conn.connect();
            ResultSet result = conn.getModel(Marka.getValue().toString());

            while(result.next())
            {
                Model.getItems().add(result.getString("model"));
            }
        }
    }
}
