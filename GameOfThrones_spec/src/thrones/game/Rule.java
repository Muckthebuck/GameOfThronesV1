package thrones.game;

import ch.aplu.jcardgame.Hand;
import thrones.game.GoTCards.Suit;

public interface Rule {

    boolean checkValidMove(Suit suit, Hand pile);
}
