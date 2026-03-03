//package Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReaderTest {
    public static void main(String[] args){
        try{
            Reader reader = new Reader(); // Create an instance of Reader
            System.out.println(reader.getPrestigePointToWin()); 
            
            // Call the method on the instance
        } catch ( Exception e){
                System.out.println("Cant find file man");
            }
        
    }
}
