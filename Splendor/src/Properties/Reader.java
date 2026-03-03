package Properties;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class Reader {
    // the default file paths
    String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    String filePath = rootPath + "config.properties";
    Properties configProps = new Properties();


    public int getPrestigePointToWin() throws IOException{
        configProps.load(new FileInputStream(filePath));
        int sum = 0;
        return sum;
    } 
    public int getnNumOfPlayers(){
        int sum = 0;
        return sum;
    }
}
