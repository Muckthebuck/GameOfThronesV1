package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.stream.Collectors;

public class GoTCards
{
    enum GOTSuit { CHARACTER, DEFENCE, ATTACK, MAGIC }
    public enum Suit {
        SPADES(GoTCards.GOTSuit.DEFENCE),
        HEARTS(GoTCards.GOTSuit.CHARACTER),
        DIAMONDS(GoTCards.GOTSuit.MAGIC),
        CLUBS(GoTCards.GOTSuit.ATTACK);
        Suit(GOTSuit gotsuit) {
            this.gotsuit = gotsuit;
        }
        private final GoTCards.GOTSuit gotsuit;

        public boolean isDefence(){ return gotsuit == GoTCards.GOTSuit.DEFENCE; }

        public boolean isAttack(){ return gotsuit == GoTCards.GOTSuit.ATTACK; }

        public boolean isCharacter(){ return gotsuit == GoTCards.GOTSuit.CHARACTER; }

        public boolean isMagic(){ return gotsuit == GoTCards.GOTSuit.MAGIC; }
    }

    public enum Rank {
        // Reverse order of rank importance (see rankGreater() below)
        // Order of cards is tied to card images
        ACE(1), KING(10), QUEEN(10), JACK(10), TEN(10), NINE(9), EIGHT(8), SEVEN(7), SIX(6), FIVE(5), FOUR(4), THREE(3), TWO(2);
        Rank(int rankValue) {
            this.rankValue = rankValue;
        }
        private final int rankValue;
        public int getRankValue() {
            return rankValue;
        }
    }

    /*
    Canonical String representations of Suit, Rank, Card, and Hand
    */
    public static String canonical(GoTCards.Suit s) { return s.toString().substring(0, 1); }

    public static String canonical(GoTCards.Rank r) {
        switch (r) {
            case ACE: case KING: case QUEEN: case JACK: case TEN:
                return r.toString().substring(0, 1);
            default:
                return String.valueOf(r.getRankValue());
        }
    }
    public static String canonical(Card c) { return canonical((GoTCards.Rank) c.getRank()) + canonical((GoTCards.Suit) c.getSuit()); }

    public static String canonical(Hand h) {
        return "[" + h.getCardList().stream().map(GoTCards::canonical).collect(Collectors.joining(",")) + "]";
    }
}
