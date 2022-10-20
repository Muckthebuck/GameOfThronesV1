package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomAi extends Ai {
    public RandomAi(RuleChecker rules, Random random) {
        super(rules,random);
    }

    public void makeMove(Pile tablePile, boolean isCharacter){
        pickACorrectSuit(isCharacter);
        if(!this.getSelected().isPresent()){
            this.setSelected(Optional.empty());
            return;
        }
        selectPile(tablePile);
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

    public boolean isLegalMove(Pile tablePile){
        boolean valid = false;
        try {
            valid = this.getRules().checkMove((GoTCards.Suit) this.getSelected().get().getSuit(),
                tablePile.getSelectedPile(this.getSelectedPileIndex()), false);
        } catch (BrokeRuleException e) {
            System.err.println("Caught BrokeRuleException: " + e.getMessage());
        }
        return valid;

    }

    public void selectPile(Pile tablePile){
        tablePile.selectRandomPile();
        this.setSelectedPileIndex(tablePile.getSelectedPileIndex());
    }

}
