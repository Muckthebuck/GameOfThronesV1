package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardGame;
import ch.aplu.jcardgame.Hand;

import java.util.Optional;

public class Player {
    private final int NON_SELECTION_VALUE = -1;
    private final RuleChecker rules;
    private final int playerIdx;
    private Hand hand;
    private Optional<Card> selected;
    private int selectedPileIndex = NON_SELECTION_VALUE;

    public Player(RuleChecker rules, int idx) {
        this.rules = rules;
        this.playerIdx = idx;
    }

    public RuleChecker getRules() {
        return rules;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public Optional<Card> getSelected() {
        return selected;
    }

    public void setSelected(Optional<Card> selected) {
        this.selected = selected;
    }

    public int getSelectedPileIndex() {
        return selectedPileIndex;
    }

    public void setSelectedPileIndex(int selectedPileIndex) {
        this.selectedPileIndex = selectedPileIndex;
    }

    public int getPlayerIdx() {
        return playerIdx;
    }

    public void displaySelected(CardGame game) {
        game.setStatusText("Selected: " + GoTCards.canonical(this.getSelected().get()) + ". Player" + this.getPlayerIdx() + " select a pile to play the card.");
    }

    public void displayTurnStart(CardGame game, boolean isCharacter) {
        if (isCharacter) {
            game.setStatusText("Player " + this.getPlayerIdx() + " select a Heart card to play");

        } else {
            game.setStatusText("Player" + this.getPlayerIdx() + " select a non-Heart card to play.");
        }
    }

    public boolean isLegalMove(TablePile tablePile) {
        if(!this.getSelected().isPresent()){
            return  true;
        }
        boolean valid = false;
        try {
            valid = this.getRules().checkMove((GoTCards.Suit) this.getSelected().get().getSuit(),
                    tablePile.getSelectedPile(), false);
        } catch (BrokeRuleException e) {
            System.err.println("Caught BrokeRuleExcetion: " + e.getMessage());
        }
        return valid;
    }
    public void makeMove(CardGame game, TablePile tablePile, boolean isCharacter){};


    public void pickACorrectSuit(boolean isCharacter){};

    public void selectPile(TablePile tablePile, boolean isCharacter){};
}
