package Cards.AbstractCard;
import Cards.Token.TokenBank;

public abstract class AbstractCard {
    private int points;
    private int blackCost;
    private int whiteCost;
    private int redCost;
    private int blueCost;
    private int greenCost;

    public AbstractCard(int points, int blackCost, int whiteCost, int redCost, int blueCost, int greenCost){

        this.points = points;
        this.blackCost = blackCost;
        this.whiteCost = whiteCost;
        this.redCost = redCost;
        this.blueCost = blueCost;
        this.greenCost = greenCost;

    }

    public int getPoints() {
        return points;
    }

    public int getCost(String tokenColor) {
        if (tokenColor.equals(TokenBank.BLACK)){
            return blackCost;
        }
        if (tokenColor.equals(TokenBank.WHITE)){
            return whiteCost;
        }
        if (tokenColor.equals(TokenBank.RED)){
            return redCost;
        }
        if (tokenColor.equals(TokenBank.BLUE)){
            return blueCost;
        }
        if (tokenColor.equals(TokenBank.GREEN)){
            return greenCost;
        }
        throw new IllegalArgumentException("Unknown token color: " + tokenColor);
    }

    @Override
    public String toString() {
        return " (" + points + "pt)" + " cost[Bk=" + blackCost + ", W=" + whiteCost + ", R=" + redCost + ", Bl=" + blueCost + ", G=" + greenCost + "]";
    }
}