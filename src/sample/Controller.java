package sample;

import ValuesSetter.ValueSetter;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * klasa odpowiedzialna za podstawowe funkcje aplikacji
 */
public class Controller
{
    public ComboBox Model;
    public ComboBox Marka;
    public DatePicker OdKiedy;
    public DatePicker DoKiedy;
    public Button Close;

    /**
     *funkcja ktora inicjuje dane o modelach po wybraniu marki pojazdu
     */
    public void setModel() throws SQLException {
        ValueSetter.Model=Model;
        ValueSetter.Marka=Marka;
        ValueSetter.setModel();
    }

    /**
     * funkcja czysci informacje od modelach gdy zostanie zmieniona marka pojazdu
     */
    public void cleanModel()
    {
        Model.getItems().remove(0,Model.getItems().size());
        Model.setDisable(false);
        Model.setPromptText("Wybierz model");
    }

    /**
     * funckja ktora wywolywana jest gdy uzytkownik konczy prace
     */
    public void close()
    {
        Stage stage = (Stage) Close.getScene().getWindow();
        stage.close();
    }
}

