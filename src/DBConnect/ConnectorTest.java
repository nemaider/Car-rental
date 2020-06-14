package DBConnect;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConnectorTest {
    @Test
    public void GetModelExceptionTest(){
        //Given
        Connector connector = new Connector();
        //When
        Exception exception = assertThrows(NullPointerException.class, () ->
                connector.getModel("asd"));
        //Then
        assertEquals(null, exception.getMessage());
    }

    @Test
    public void ExecuteQueryExceptionTest(){
        //Given
        Connector connector = new Connector();
        //When
        Exception exception = assertThrows(NullPointerException.class, () ->
                connector.executeQuery("BMW","M5","20/02/20","20/03/20"));
        //Then
        assertEquals(null, exception.getMessage());
    }

}