package Client;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Funkcja odpowiadająca za odebranie wiadomości od serwera pracująca w osobnym wątku
 */
class GetMessage implements Runnable
{
    Socket socket;
    ScrollPane offertsScrollBox;
    SendMessage sendMessage;
    Label noOfferts;
    Pane offert[];
    int code;

    /**
     * @param socket socket przez który serwer komunikuje się z klientami
     * @param offerts obiekt w którym wyświetlane są oferty pojazdów
     * @param sendMessage obiekt klasy SendMessage który wykonuje polecenie zarezerwowania pojazdu
     */
    GetMessage(Socket socket,ScrollPane offerts, SendMessage sendMessage)
    {
        this.offertsScrollBox = offerts;
        this.socket = socket;
        this.sendMessage = sendMessage;
    }

    /**
     * nadpisana funkcja klasy rozszerzonej o Runnable która wywolywana jest poprzez funkcje start()
     * funckja nowego wątku
     */
    @Override
    public void run()
    {
        try
        {
            DataInputStream in = new DataInputStream(socket.getInputStream());

            while(true)
            {
                String messageFromServer = in.readUTF();

                Platform.runLater(()->
                {
                    String parameters[] = messageFromServer.split("\\s+");
                    code = Integer.parseInt(parameters[0]);
                    if(code == -2)
                        System.out.println("siema");
                    else if(code <= 0)
                        showNoOfferts(code);
                    else
                        showCars(code,parameters);
                });
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
//////////////////////////////oferty////////////////////////////////////////

    /**
     * funckja obliczająca cene za wypożyczenie
     * @param cena cena pojazdu za dobe
     * @param dni ilość dni na które pojazd ma być wypożyczony
     * @return cena za wypożyczenie danego samochodu na daną ilość dni
     */
    public Label calculate(String cena, String dni)
    {
        int koszt = Integer.parseInt(cena) * Integer.parseInt(dni);
        Label value = new Label("cena calkowita: " + koszt);
        return value;
    }

    /**
     * funkcja która wyświetla oferty, tworząca nowe obiekty dynamicznie
     * @param j ilość ofert do wyświetlenia
     * @param parameters dane pojazdów
     */
    public void showCars(int j, String parameters[])
    {
        //////////////////pane z wszystkimi ofertami
        Pane offertsMainPane = new Pane();
        offertsScrollBox.setContent(offertsMainPane);

        offertsScrollBox.setPrefWidth((j-1)*125);
        offertsMainPane.setPrefSize(offertsScrollBox.getHeight(),(j)*125);
        offert = new Pane[j];

        for(int i=0;i<j;i++)
        {
            offert[i] = new Pane();
            offert[i].setBackground(new Background(new BackgroundFill(Color.web("#44515F"), CornerRadii.EMPTY, Insets.EMPTY)));
            offert[i].setPrefSize(offertsScrollBox.getWidth()-100,100);
            offert[i].setLayoutY(i*105+25);
            offert[i].setLayoutX(50);
            ////////dodawanie oferty
            offertsMainPane.getChildren().add(offert[i]);

            Label name = new Label(parameters[6*i+1]);
            Label description = new Label("model: " + parameters[6*i+2]);
            Label vin = new Label("vin: " + parameters[6*i+3]);
            String path = parameters[4*i+4];
            Label cena_za_dobe = new Label("za dobe: " + parameters[6*i+5] + "zl.");

            name.setLayoutX(110);
            name.setLayoutY(20);

            description.setLayoutX(110);
            description.setLayoutY(40);

            vin.setLayoutX(110);
            vin.setLayoutY(60);

            Image image = new Image(path);
            ImageView logo = new ImageView(image);
            logo.setFitHeight(90);
            logo.setFitWidth(90);
            logo.setLayoutX(5);
            logo.setLayoutY(5);

            cena_za_dobe.setLayoutX(200);
            cena_za_dobe.setLayoutY(40);

            if(!parameters[6*i+6].equals("-1") && !parameters[6*i+6].equals("0"))
            {
                Label cena = calculate(parameters[6*i+6],parameters[6*i+5]);
                cena.setLayoutX(200);
                cena.setLayoutY(20);

                Button rentalButton = new Button("zarezerwuj");
                rentalButton.setLayoutX(offert[i].getPrefWidth()-100);
                rentalButton.setLayoutY(35);

                offert[i].getChildren().add(cena);
                offert[i].getChildren().add(rentalButton);

                int finalI = i;
                rentalButton.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent)
                    {
                        sendMessage.sendToInsert(parameters[finalI +3]);
                    }
                });
            }

            ///////dodawanie nazwy samochodu i opisu
            offert[i].getChildren().add(name);
            offert[i].getChildren().add(description);
            offert[i].getChildren().add(vin);
            offert[i].getChildren().add(logo);
            offert[i].getChildren().add(cena_za_dobe);
        }
    }

    /**
     * funkcja wykonywana kiedy nie ma żadnych ofert do wyświetlenia
     * może ona wyświetlić informacje iż samochód został zarezerwowany
     * @param j paramert decydujący o działaniu funkcji. Jeśli paramert jest równy -1 wtedy znaczy to iż samochód został zarezerwowany, jeśli 0 znaczy iż nie ma ofert dla użytkownika
     */
    public void showNoOfferts(int j)
    {
        Pane offertsMainPane = new Pane();
        offertsScrollBox.setContent(offertsMainPane);
        offertsScrollBox.setPrefWidth(125);
        offertsMainPane.setPrefSize(offertsScrollBox.getHeight(),(125));
        if(j==0)
        {
            noOfferts = new Label("Nie ma oferty dla ciebie");
            noOfferts.setLayoutX(offertsScrollBox.getWidth()/2-120);
        }

        if(j == -1)
        {
            noOfferts = new Label("Zarezerwowales samochod!");
            noOfferts.setLayoutX(offertsScrollBox.getWidth()/2-120);
        }

        noOfferts.setFont(Font.font(20));
        noOfferts.setLayoutY(40);
        offertsMainPane.getChildren().add(noOfferts);
    }
}
