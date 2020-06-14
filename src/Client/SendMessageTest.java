package Client;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class SendMessageTest {

    Button check;
    ComboBox marka;
    ComboBox model;
    DatePicker start;
    DatePicker end;
    Socket socket;

    @Test
    public void SendToInsertExceptionTest(){
        //Given
        SendMessage sendMessage = new SendMessage(marka, model, start, end, check, socket);
        //When & Then
        assertThrows(NullPointerException.class, () ->
                sendMessage.sendToInsert("11111"));

    }
}