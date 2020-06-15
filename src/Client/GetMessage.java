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
import javafx.scene.text.FontWeight;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Funkcja odpowiadajaca za odebranie wiadomosci od serwera pracujaca w osobnym watku
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
     * @param socket socket przez ktory serwer komunikuje sie z klientami
     * @param offerts obiekt w ktorym wyswietlane sa oferty pojazdow
     * @param sendMessage obiekt klasy SendMessage ktory wykonuje polecenie zarezerwowania pojazdu
     */
    GetMessage(Socket socket,ScrollPane offerts, SendMessage sendMessage)
    {
        this.offertsScrollBox = offerts;
        this.socket = socket;
        this.sendMessage = sendMessage;
    }

    /**
     * nadpisana funkcja klasy rozszerzonej o Runnable ktora wywolywana jest poprzez funkcje start()
     * funckja nowego watku
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
                    {
                        System.out.println(messageFromServer.replace("-2", ""));
                    }
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
     * funckja obliczajaca cene za wypozyczenie
     * @param cena cena pojazdu za dobe
     * @param dni ilosc dni na ktore pojazd ma byc wypozyczony
     * @return cena za wypozyczenie danego samochodu na dana ilosc dni
     */
    public Label calculate(String cena, String dni)
    {
        int koszt = Integer.parseInt(cena) * Integer.parseInt(dni);
        Label value = new Label("cena calkowita: " + koszt);
        return value;
    }

    /**
     * funkcja ktora wyswietla oferty, tworzaca nowe obiekty dynamicznie
     * @param offertsNumber ilosc ofert do wyswietlenia
     * @param parameters dane pojazdow
     */
    public void showCars(int offertsNumber, String parameters[])
    {
        ///pane na ktorym beda wyswietlane oferty
        Pane offertsMainPane = new Pane();
        offertsMainPane.setPrefSize(offertsScrollBox.getHeight(),(offertsNumber)*125);

        offertsScrollBox.setContent(offertsMainPane);
        offertsScrollBox.setPrefWidth((offertsNumber-1)*125);

        offert = new Pane[offertsNumber];

        for(int i=0;i<offertsNumber;i++)
        {
            offert[i] = new Pane();
            offert[i].setBackground(new Background(new BackgroundFill(Color.web("#ffffff"), new CornerRadii(20), Insets.EMPTY)));
            offert[i].setPrefSize(offertsScrollBox.getWidth()-100,100);
            offert[i].setLayoutY(i*105+25);
            offert[i].setLayoutX(50);

            //dodawanie oferty
            offertsMainPane.getChildren().add(offert[i]);

            Label brand = new Label(parameters[6*i+1]);
            Label model = new Label( parameters[6*i+2]);
            Label vin = new Label("vin: " + parameters[6*i+3]);
            String path = parameters[6*i+4];
            Label totalCost = new Label("za dobe: " + parameters[6*i+5] + "zl.");

            brand.setLayoutX(110);
            brand.setLayoutY(20);
            brand.setFont(Font.font("Verdana", FontWeight.BOLD, 15));

            model.setLayoutX(110);
            model.setLayoutY(40);
            model.setFont(Font.font("Verdana", FontWeight.BOLD, 13));

            vin.setLayoutX(110);
            vin.setLayoutY(60);
            vin.setFont(Font.font("Verdana", FontWeight.NORMAL, 13));

            Image image = new Image(path);
            ImageView logo = new ImageView(image);
            logo.setFitHeight(90);
            logo.setFitWidth(90);
            logo.setLayoutX(5);
            logo.setLayoutY(5);

            totalCost.setLayoutX(200);
            totalCost.setLayoutY(40);
            totalCost.setFont(Font.font("Verdana", FontWeight.NORMAL, 13));

            if(!parameters[6*i+6].equals("-1") && !parameters[6*i+6].equals("0"))
            {
                Label cost = calculate(parameters[6*i+6],parameters[6*i+5]);
                cost.setLayoutX(200);
                cost.setLayoutY(20);
                cost.setFont(Font.font("Verdana", FontWeight.NORMAL, 13));

                Button rentalButton = new Button("zarezerwuj");
                rentalButton.setLayoutX(offert[i].getPrefWidth()-100);
                rentalButton.setLayoutY(55);

                offert[i].getChildren().add(cost);
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

            //dodawanie nazwy samochodu i opisu
            offert[i].getChildren().add(brand);
            offert[i].getChildren().add(model);
            offert[i].getChildren().add(vin);
            offert[i].getChildren().add(logo);
            offert[i].getChildren().add(totalCost);
        }
    }

    /**
     * funkcja wykonywana kiedy nie ma zadnych ofert do wyswietlenia
     * moze ona wyswietlic informacje iz samochod zostal zarezerwowany
     * @param j paramert decydujacy o dzialaniu funkcji. Jesli paramert jest rowny -1 wtedy znaczy to iz samochod zostal zarezerwowany, jesli 0 znaczy iz nie ma ofert dla uzytkownika
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
