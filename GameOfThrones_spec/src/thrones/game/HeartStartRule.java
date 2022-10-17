package thrones.game;
import ch.aplu.jcardgame.Hand;
import thrones.game.GoTCards.Suit;

public class HeartStartRule implements Rule {

    public void checkValidMove(Suit suit, Hand pile) throws BrokeRuleException {
        if ((suit == Suit.HEARTS) && (pile.getLast() != null)) {
            throw new BrokeRuleException("You can only play a heart as the first card on a pile");
        } else {}
    }
}