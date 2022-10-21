package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Ai extends Player {
    private final Random random;

    public Ai(RuleChecker rules, Random random, int idx) {
        super(rules, idx);
        this.random = random;
    }

    public Random getRandom() {
        return random;
    }



    public void pickACorrectSuit(boolean isCharacter) {
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

    public void selectPile(Pile tablePile, boolean isCharacter) {
        if (isCharacter) {
            tablePile.selectTeamPile(this.getPlayerIdx());
        } else {
            tablePile.selectRandomPile();
        }
        this.setSelectedPileIndex(tablePile.getSelectedPileIndex());
    }
}
