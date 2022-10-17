package thrones.game;

import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;

public class CompositeRule implements Rule {

    private ArrayList<Rule> rules = new ArrayList<>();

    public CompositeRule() {
        rules.add(new DiamondOnHeartRule());
        rules.add(new HeartStartRule());
    }

    public void checkValidMove(GoTCards.Suit suit, Hand pile) throws BrokeRuleException {
        for (Rule rule: rules) {
            rule.checkValidMove(suit, pile);
        }
    }
}