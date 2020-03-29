package ValuesSetter;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.awt.event.ActionEvent;
import java.time.LocalDate;

public class ValueSetter {

    public static ComboBox Marka;
    public static ComboBox Model;

    public static DatePicker OdKiedy;
    public static DatePicker DoKiedy;

    public void setMarka(ComboBox Model)
    {
        Model.getItems().add("bimmer");
        Model.getItems().add("benz");
        Model.getItems().add("bentley");
    }

    public static void setModel()
    {
        if(Model.getItems().isEmpty() && Marka.getValue()!=null)
        {
            if(Marka.getValue().equals("bimmer"))
            {
                Model.getItems().add("M3 GTR");
                Model.getItems().add("E46");
                Model.getItems().add("X5");
            }

            if(Marka.getValue().equals("benz"))
            {
                Model.getItems().add("C63 AMG");
                Model.getItems().add("GLA");
                Model.getItems().add("CLS");
            }

            if(Marka.getValue().equals("bentley"))
            {
                Model.getItems().add("Continental");
                Model.getItems().add("Bentayga");
                Model.getItems().add("Flying Spurs");
            }
        }
    }

    public static void checkAvailable()
    {
        LocalDate odKiedy = OdKiedy.getValue();
        LocalDate doKiedy = DoKiedy.getValue();

        if(odKiedy==null || doKiedy==null)
            System.out.println("Zle okreslone daty!");
        else if(!odKiedy.isBefore(doKiedy))
            System.out.println("Data poczatkowa nie moze byc mniejsza od koncowej!");
        else
            System.out.println("zarazerwuj samochod od " + odKiedy + " do " + doKiedy);
    }
}
