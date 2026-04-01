package Properties;

import java.io.InputStream;
import java.util.Properties;

public class Reader {

    //java properties object
    private final Properties configProps = new Properties();
    
    //constructor
    public Reader() throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream("Properties/config.properties");
        if (in == null) {
            throw new Exception("config.properties not found in classpath");
        }
        configProps.load(in);
    }


    //method to get pretsige points from file
    public int getPrestigePointToWin() {
        return Integer.parseInt(configProps.getProperty("prestigePointsToWin"));
    }

    //method to get number of players
    public int getNumOfPlayers() {
        return Integer.parseInt(configProps.getProperty("numOfPlayers"));
    }
    //method to get the number of cards
    public int getNumOfCards(){
        return Integer.parseInt(configProps.getProperty("numOfCards")); 
    }

    //method to get the custom tokemn mode for the bank
    public int getCustomTokenMode(){
        return Integer.parseInt(configProps.getProperty("enableCustomToken")); 
    }
    //method to get the custom colour tokens
    public int getColourToken(String str){
        return Integer.parseInt(configProps.getProperty(str)); 
    }

    //file paths for the deck tiers
    public String getTierDeck(int tier){
        return configProps.getProperty("tier"+tier+"DeckDir");  
    }
}
