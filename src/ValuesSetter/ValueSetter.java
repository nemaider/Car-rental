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
        Model.getItems().add("BMW");
        Model.getItems().add("Mercedes");
        Model.getItems().add("Volkswagen");
    }

    public static void setModel()
    {
        if(Model.getItems().isEmpty() && Marka.getValue()!=null)
        {
            if(Marka.getValue().equals("BMW"))
            {
                Model.getItems().add("M3");
                Model.getItems().add("E46");
                Model.getItems().add("X5");
            }

            if(Marka.getValue().equals("Mercedes"))
            {
                Model.getItems().add("GLC");
                Model.getItems().add("Vito");
                Model.getItems().add("CLA");
            }

            if(Marka.getValue().equals("Volkswagen"))
            {
                Model.getItems().add("Arteon");
                Model.getItems().add("Passat");
                Model.getItems().add("Golf");
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
