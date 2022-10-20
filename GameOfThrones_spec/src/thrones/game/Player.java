package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardGame;
import ch.aplu.jcardgame.Hand;

import java.util.Optional;

public abstract class Player implements Moveable{
    private Hand hand;
    private Optional<Card> selected;
    private final int NON_SELECTION_VALUE = -1;
    private int selectedPileIndex = NON_SELECTION_VALUE;
    private final RuleChecker rules;
    private final int playerIdx;
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
    public void displaySelected(CardGame game){
        game.setStatusText("Selected: " + GoTCards.canonical(this.getSelected().get()) + ". Player" + this.getPlayerIdx() + " select a pile to play the card.");
    }
}
