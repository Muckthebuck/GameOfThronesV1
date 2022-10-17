package thrones.game;
import ch.aplu.jcardgame.Hand;
import thrones.game.GoTCards.Suit;
public interface Rule {

    public void checkValidMove(Suit suit, Hand pile) throws BrokeRuleException;
}
