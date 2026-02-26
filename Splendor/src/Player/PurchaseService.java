/*
This purchaseService class is to simulate the action when a player buys a development card, we will do all the transaction in this class
*/
package Player;

import Cards.DevelopmentCard.DevelopmentCard;
import Cards.Token.TokenBank;

public class PurchaseService {
    // String list to store all the colors, and we compare the number of each color between the development card and players
    private static final String[] COLORS = {TokenBank.WHITE, TokenBank.BLUE, TokenBank.GREEN, TokenBank.RED, TokenBank.BLACK};

    // Helper function to check whether player can afford a development card 
    public static boolean canBuy(Player p, DevelopmentCard c){
        int hasgold = p.getTokens(TokenBank.GOLD);               // Take note the number of gold token a player has

        for(String color : COLORS){                              // go trough each color and compare the value
            int cost = c.getCost(color);
            int discount = p.getBonus(color);
            int amountNeedToPay = Math.max(0, cost - discount);   // Amount need to pay = original cost - Bonus, but what if i have more bonus than the original cost? then we take 0 instead.
                                                                
            if (p.getTokens(color) < amountNeedToPay) {           // if i dont have enough token
                if (p.getTokens(color) + hasgold > amountNeedToPay) { // check if the amount of gold token can pay it
                    return true;                                   // if can then can buy
                }else{ 
                    return false;                                  // if still not enough then cannot buy
                }
            }
        }
        return true;                                              // they alaway have enough token for every color then can buy
    }


//  Actual buy action
    public static void buy(Player p, DevelopmentCard c, TokenBank tb){
        if(!canBuy(p , c)){
            throw new IllegalArgumentException("Player cannot afford this card");
        }

        // similar logic as above method.
        for (String color : COLORS) {
            int cost = c.getCost(color);
            int discount = p.getBonus(color);
            int amountNeedToPay = Math.max(0, cost - discount);

            if (p.getTokens(color) > amountNeedToPay) {
                p.removeTokens(color, amountNeedToPay);
                tb.add(color, amountNeedToPay);
            }else{
                int payWithColor = p.getTokens(color);
                int payWithGold = amountNeedToPay - payWithColor;
                p.removeTokens(color, payWithColor);
                p.removeTokens(TokenBank.GOLD, payWithGold);
                tb.add(color, payWithColor);
                tb.add(TokenBank.GOLD, payWithGold);
            }
        }
        p.addDevelopmentCard(c);
    }

}