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

    public void  pickACorrectSuit(boolean isCharacter) {
        Hand currentHand = this.getHand();

        List<Card> shortListCards = new ArrayList<>();
        for (int i = 0; i < currentHand.getCardList().size(); i++) {
            Card card = currentHand.getCardList().get(i);
            GoTCards.Suit suit = (GoTCards.Suit) card.getSuit();
            if (suit.isCharacter() == isCharacter) {
                shortListCards.add(card);
            }
        }
        if (shortListCards.isEmpty() || !isCharacter && GameOfThrones.random.nextInt(3) == 0) {
            this.setSelected(Optional.empty());
        } else {
            this.setSelected(Optional.of(shortListCards.get(GameOfThrones.random.nextInt(shortListCards.size()))));
        }
    }



    public void selectPile(Pile tablePile, boolean isCharacter){
        if(isCharacter){
            tablePile.selectTeamPile(this.getPlayerIdx());
        }else{
            tablePile.selectRandomPile();
        }
        this.setSelectedPileIndex(tablePile.getSelectedPileIndex());
    }

}
