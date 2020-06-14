package Client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetMessageTest {
    @Test
    public void ShouldReturn8CalculateTest() {
        //Given
        String cena="4",dni="2";
        //When
        int koszt = Integer.parseInt(cena) * Integer.parseInt(dni);
        //Then
        assertEquals(8,koszt);
    }

    @Test
    public void MultiplyByZeroTest(){
        //Given
        String cena="4",dni="0";
        //When
        int koszt = Integer.parseInt(cena) * Integer.parseInt(dni);
        //Then
        assertEquals(0,koszt);
    }
}