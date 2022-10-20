package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import thrones.game.GoTCards.GOTSuit;


public class ScoreCalculator {
    private final int DOUBLE = 2;
    private final int ATTACK_RANK_INDEX;
    private final int DEFENCE_RANK_INDEX;

    public ScoreCalculator(int ATTACK_RANK_INDEX, int DEFENCE_RANK_INDEX) {
        this.ATTACK_RANK_INDEX = ATTACK_RANK_INDEX;
        this.DEFENCE_RANK_INDEX = DEFENCE_RANK_INDEX;
    }

    public int[] calculate(Hand currentPile){
        int[] rank = new int[2];
        int[] oldrank = {0,0};
        for (Card card:currentPile.getCardList()) {
            GoTCards.Suit suit = (GoTCards.Suit) card.getSuit();
            int[] r = {0,0};
            if(suit.isCharacter()){
                r = characterCardRank(card);
            }else if(suit.isMagic()){
                r = magicCardRank(card, oldrank);
            }else if(suit.isAttack()){
                r = attackDefenceCardRank(card, ATTACK_RANK_INDEX);
            }else if(suit.isDefence()){
                r = attackDefenceCardRank(card, DEFENCE_RANK_INDEX);
            }
            int[] r1 = rankMultiplier(oldrank, r);
            oldrank = r;
            rank = addRanks(rank, r1);
        }
        return  rank;
    }


    private int[] rankMultiplier(int[] oldRank, int[] newRank){
        int [] rank = {newRank[0], newRank[1]};
        if((Math.abs(oldRank[0])==Math.abs(newRank[0]))&&(Math.abs(oldRank[1])==Math.abs(newRank[1]))){
            rank[0] *= DOUBLE;
            rank[1] *= DOUBLE;
        }
        return rank;
    }

    private int[] characterCardRank(Card card) {
        int i = ((GoTCards.Rank) card.getRank()).getRankValue();

        return new int[]{i, i};
    }

    private int[] attackDefenceCardRank(Card card, int idx){
        int[] rank = {0,0};
        rank[idx] = ((GoTCards.Rank) card.getRank()).getRankValue();
        return rank;
    }

    private int[] magicCardRank(Card card, int[] oldCardRank){
        int[] rank = {0,0};
        int idx = (oldCardRank[0]>0)?0:1;
        rank[idx] = ((GoTCards.Rank) card.getRank()).getRankValue();
        return rank;
    }

    // return a = a+b, elementwise addition
    private int[] addRanks(int[] a, int[] b){
        int[] result = new int[a.length];
        for (int i = 0; i <a.length ; i++) {
            result[i] = a[i]+b[i];
            result[i] = (result[i]<0)?0:result[i];
        }
        return  result;
    }

}
