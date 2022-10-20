package thrones.game;

import java.util.Random;

public class SimpleAi extends Ai{
    public SimpleAi(RuleChecker rules, Random random) {
        super(rules, random);
    }

    //TODO
    @Override
    public void makeMove(Pile tablePile, boolean isCharacter) {

    }

    @Override
    public boolean isLegalMove(Pile tablePile) {
        return false;
    }

    @Override
    public void pickACorrectSuit(boolean isCharacter) {

    }

    @Override
    public void selectPile(Pile tablePile) {

    }
}
