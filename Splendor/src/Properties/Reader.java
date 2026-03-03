//package Properties;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class Reader {
    // the default file paths
    public String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    public String filePath = rootPath + "config.properties";
    public Properties configProps = new Properties();

    //working
    public int getPrestigePointToWin() throws IOException{
        configProps.load(new FileInputStream(filePath));
        int sum = Integer.parseInt(configProps.getProperty("prestigePointsToWin"));
        return sum;
    } 
    public int getNumOfPlayers() throws IOException{
        configProps.load(new FileInputStream(filePath));
        int sum = Integer.parseInt(configProps.getProperty("numOfPlayers"));
        return sum;
    }
}
