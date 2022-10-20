package thrones.game;

import ch.aplu.jcardgame.CardGame;

import java.util.Random;

public class SimpleAi extends Ai{
    public SimpleAi(RuleChecker rules, Random random, int idx) {
        super(rules, random, idx);
    }

    //TODO
    @Override
    public void makeMove(CardGame game, Pile tablePile, boolean isCharacter) {

    }

    @Override
    public void pickACorrectSuit(boolean isCharacter) {

    }

    @Override
    public void selectPile(Pile tablePile, boolean isCharacter) {

    }
}
