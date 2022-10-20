package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardGame;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomAi extends Ai {
    public RandomAi(RuleChecker rules, Random random, int idx) {
        super(rules,random, idx);
    }

    public void makeMove(CardGame game, Pile tablePile, boolean isCharacter){
        this.displayTurnStart(game, isCharacter);
        pickACorrectSuit(isCharacter);
        if(!this.getSelected().isPresent()){
            this.setSelected(Optional.empty());
            return;
        }
        this.displaySelected(game);

        selectPile(tablePile,isCharacter);
        if(!this.isLegalMove(tablePile)){
            this.setSelected(Optional.empty());
        }

    }


}
