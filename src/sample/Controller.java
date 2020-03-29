package sample;

import ValuesSetter.ValueSetter;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

public class Controller
{
    public ComboBox Model;
    public ComboBox Marka;
    public DatePicker OdKiedy;
    public DatePicker DoKiedy;
    public Button Close;

    //metody z ValueSetter.java
    public void setModel()
    {
        ValueSetter.Model=Model;
        ValueSetter.Marka=Marka;
        ValueSetter.setModel();
    }

    public void checkAvailable()
    {
        ValueSetter.OdKiedy=OdKiedy;
        ValueSetter.DoKiedy=DoKiedy;
        ValueSetter.checkAvailable();
    }

    public void cleanModel()
    {
        Model.getItems().remove(0,Model.getItems().size());
        Model.setDisable(false);
        Model.setPromptText("Wybierz model");
    }

    public void close()
    {
        Stage stage = (Stage) Close.getScene().getWindow();
        stage.close();
    }
}

