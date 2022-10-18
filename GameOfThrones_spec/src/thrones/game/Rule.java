package thrones.game;
import ch.aplu.jcardgame.Hand;
import thrones.game.GoTCards.Suit;
public interface Rule {

    public boolean checkValidMove(Suit suit, Hand pile);
}
