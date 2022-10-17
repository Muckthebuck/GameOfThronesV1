package thrones.game;
import ch.aplu.jcardgame.Hand;
import thrones.game.GoTCards.Suit;

public class DiamondOnHeartRule implements Rule {

    public void checkValidMove(Suit suit, Hand pile) throws BrokeRuleException {
        if ((suit == Suit.DIAMONDS) && (pile.getLast().getSuit() == Suit.HEARTS)) {
            throw new BrokeRuleException("You can not play a diamond on top of a heart");
        } else {}
    }

}