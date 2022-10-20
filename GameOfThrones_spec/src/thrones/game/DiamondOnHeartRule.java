package thrones.game;

import ch.aplu.jcardgame.Hand;
import thrones.game.GoTCards.Suit;

public class DiamondOnHeartRule implements Rule {

    public boolean checkValidMove(Suit suit, Hand pile) {
        return (suit != Suit.DIAMONDS) || (pile.getLast().getSuit() != Suit.HEARTS);
    }

}