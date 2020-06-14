package Server;

import javafx.scene.control.TextArea;
import org.junit.jupiter.api.Test;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class ReplyTheRequestTest {

    TextArea messages;
    Socket socket;

    @Test
    public void DateDifferenceWithNullParameterTest(){
        //Given
        ReplyTheRequest replyTheRequest = new ReplyTheRequest(messages,socket);
        //When&Then
        assertEquals(-1,replyTheRequest.dateDiffrece("null","2020-05-16"));
    }

    @Test
    public void ShouldReturn1DataDifferenceTest(){
        //Given
        ReplyTheRequest replyTheRequest = new ReplyTheRequest(messages,socket);
        //When
        long result = replyTheRequest.dateDiffrece("2020-05-16","2020-05-17");
        //Then
        assertEquals(1,result);
    }

    @Test
    public void ShouldReturn0DataDifferenceTest(){
        //Given
        ReplyTheRequest replyTheRequest = new ReplyTheRequest(messages,socket);
        //When
        long result = replyTheRequest.dateDiffrece("2020-05-19","2020-05-19");
        //Then
        assertEquals(0,result);
    }

}