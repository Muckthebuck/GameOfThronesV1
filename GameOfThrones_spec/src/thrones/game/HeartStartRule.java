package thrones.game;

import ch.aplu.jcardgame.Hand;
import thrones.game.GoTCards.Suit;

public class HeartStartRule implements Rule {

    public boolean checkValidMove(Suit suit, Hand pile) {
        return (suit != Suit.HEARTS) || (pile.getLast() == null);
    }
}