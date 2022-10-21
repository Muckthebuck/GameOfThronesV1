package thrones.game;

import ch.aplu.jcardgame.CardGame;

import java.util.Optional;
import java.util.Random;

public class SimpleAi extends Ai {
    public SimpleAi(RuleChecker rules, Random random, int idx) {
        super(rules, random, idx);
    }

    //TODO
    @Override
    public void makeMove(CardGame game, TablePile tablePile, boolean isCharacter) {
        this.displayTurnStart(game, isCharacter);
        pickACorrectSuit(isCharacter);
        if (!this.getSelected().isPresent()) {
            this.setSelected(Optional.empty());
            return;
        }
        this.displaySelected(game);
        selectPile(tablePile, isCharacter);

        removeHinderMoves(tablePile);

        if (!this.isLegalMove(tablePile)) {
            this.setSelected(Optional.empty());
        }

    }

    private void removeHinderMoves(TablePile tablePile) {
        GoTCards.Suit suit = (GoTCards.Suit) this.getSelected().get().getSuit();
        if (tablePile.getSelectedPileIndex() != tablePile.getTeamPileIdx(this.getPlayerIdx())) {
            // in enemy team Pile, dont want to increase their attack or defence

            if (suit == GoTCards.Suit.CLUBS || suit == GoTCards.Suit.SPADES) {
                this.setSelected(Optional.empty());
                return;
            }
        } else {
            // in own team pile, dont want to decrease my own stats
            if (suit == GoTCards.Suit.DIAMONDS) {
                this.setSelected(Optional.empty());
                return;
            }
        }
    }

}
