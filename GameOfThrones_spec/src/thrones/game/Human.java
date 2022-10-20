package thrones.game;

import ch.aplu.jcardgame.CardGame;

import java.util.Optional;

public class Human extends Player {

    public Human(RuleChecker rules, int idx) {
        super(rules, idx);
    }

    public void makeMove(CardGame game, Pile tablePile, boolean isCharacter) {
        do {
            this.displayTurnStart(game, isCharacter);
            pickACorrectSuit(isCharacter);
            if (!this.getSelected().isPresent()) {
                this.setSelected(Optional.empty());
                return;
            }
            this.displaySelected(game);

            selectPile(tablePile, isCharacter);

        } while (!this.isLegalMove(tablePile));
    }


    public void pickACorrectSuit(boolean isCharacter) {
        if (this.getHand().isEmpty()) {
            this.setSelected(Optional.empty());
        } else {
            this.setSelected(null);
            this.getHand().setTouchEnabled(true);
            do {
                if (this.getSelected() == null) {
                    GameOfThrones.delay(100);
                    continue;
                }
                GoTCards.Suit suit = this.getSelected().isPresent() ? (GoTCards.Suit) this.getSelected().get().getSuit() : null;
                if (isCharacter && suit != null && suit.isCharacter() ||         // If we want character, can't pass and suit must be right
                        !isCharacter && (suit == null || !suit.isCharacter())) { // If we don't want character, can pass or suit must not be character
                    // if (suit != null && suit.isCharacter() == isCharacter) {
                    break;
                } else {
                    this.setSelected(null);
                    this.getHand().setTouchEnabled(true);
                }
                GameOfThrones.delay(100);
            } while (true);

        }
    }

    public void selectPile(Pile tablePile, boolean isCharacter) {
        if (isCharacter) {
            tablePile.selectTeamPile(this.getPlayerIdx());
        } else {
            tablePile.waitForPileSelection();
        }

    }

}
