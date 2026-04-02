package Properties;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * The Reader class reads from the config.properties file to determine the winning condition, number of players and number of cards.
 * Based on the config.properties file, the Reader class can customize the number of tokens as well
 */

public class Reader {

    //java properties object
    private final Properties configProps = new Properties();
    

    /**
     * Initialize configProps based on the config.properties file
     * @throws Exception if config.properties file not found in classpath
     */
    public Reader() throws Exception {
        Path rootConfig = Path.of("config.properties");

        if (Files.exists(rootConfig)) {
            try (InputStream in = Files.newInputStream(rootConfig)) {
                configProps.load(in);
                System.out.println("Loaded config from project root: " + rootConfig.toAbsolutePath());
                return;
            }
        }

        try (InputStream in = getClass().getClassLoader().getResourceAsStream("Properties/config.properties")) {
            if (in == null) {
                throw new Exception("config.properties not found in project root or classpath");
            }
            configProps.load(in);
            System.out.println("Loaded config from classpath: Properties/config.properties");
        }
    }


    /**
     * Returns the number of prestige points for a player to end the game as set in the config.properties file
     * @return number of prestige points for a player to end the game
     */
    public int getPrestigePointToWin() {
        return Integer.parseInt(configProps.getProperty("prestigePointsToWin"));
    }


    /**
     * Returns the number of players as set in the config.properties file
     * @return number of players
     */
    public int getNumOfPlayers() {
        return Integer.parseInt(configProps.getProperty("numOfPlayers"));
    }

    //method to get the number of cards
    /**
     * Returns the number of cards as set in the config.properties file
     * @return number of cards
     */
    public int getNumOfCards(){
        return Integer.parseInt(configProps.getProperty("numOfCards"));
    }

    //method to get the custom token mode for the bank
    /**
     * Returns 1 or 0 based on setting in config.properties file
     * 1 indicates custom token mode where the number of tokens can be set in the config.properties file
     * @return 1 (custom token mode) or 0 (default)
     */
    public int getCustomTokenMode(){
        return Integer.parseInt(configProps.getProperty("enableCustomToken"));
    }

    /**
     * Returns the number of a specific color token as set in the config.properties file
     * Used when the custom token mode is enabled
     * @param str color of token
     * @return number of tokens of that color
     */
    public int getColourToken(String str){
        String value = configProps.getProperty(str);
        if(value == null){
            value = configProps.getProperty(str.toUpperCase());
        }
        if(value == null){
            value = configProps.getProperty(str.toLowerCase());
        }
        if(value == null) {
            throw new IllegalArgumentException("Missing token config for color: " + str);
        }
        return Integer.parseInt(value);
    }

    //file paths for the deck tiers
    /**
     * Returns the file path of the specific deck tier (level of development card)
     * @param tier level of development card
     * @return file path of the csv file containing the development cards of that level
     */
    public String getTierDeck(int tier){
        return configProps.getProperty("tier"+tier+"DeckDir");
    }
    
    /**
     * Returns the image path directory for the cards images for Java FX
     * @return image path directory
     */
    public String getNobleImages(){
        return configProps.getProperty("nobleImages");
    }

    public String getTierOneDir(){
        return configProps.getProperty("tierOneDir");
    }

    public String getTierTwoDir(){
        return configProps.getProperty("tierTwoDir");
    }

    public String getTierThreeDir(){
        return configProps.getProperty("tierThreeDir");
    }

    public String getTierOneCardBack(){
        return configProps.getProperty("TierOneCardBack");
    }

    public String getTierTwoCardBack(){
        return configProps.getProperty("TierTwoCardBack");
    }

    public String getTierThreeCardBack(){
        return configProps.getProperty("TierThreeCardBack");
    }
}
