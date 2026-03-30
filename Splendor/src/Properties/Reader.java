package Properties;

import java.io.InputStream;
import java.util.Properties;

public class Reader {

    private final Properties configProps = new Properties();

    public Reader() throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties");
        if (in == null) {
            throw new Exception("config.properties not found in classpath");
        }
        configProps.load(in);
    }

    public int getPrestigePointToWin() {
        return Integer.parseInt(configProps.getProperty("prestigePointsToWin"));
    }

    public int getNumOfPlayers() {
        return Integer.parseInt(configProps.getProperty("numOfPlayers"));
    }
}
