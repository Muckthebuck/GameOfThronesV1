package thrones.game;

public interface Rule {

    public void checkValidMove(Suit suit, Hand pile) throws BrokeRuleException;
}
