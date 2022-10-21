package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardGame;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class SmartAi extends Ai {
    ArrayList<GoTCards.Rank> safeRanks = new ArrayList<GoTCards.Rank>();
    boolean first = true;

    public SmartAi(RuleChecker rules, Random random, int idx) {
        super(rules, random, idx);
    }

    //TODO
    @Override
    public void makeMove(CardGame game, TablePile tablePile, boolean isCharacter) {
        updateSafeRanks(tablePile, isCharacter);
        Optional<Card> selectedCard = Optional.empty();
        int selectedPile = -1;
        int pile = tablePile.getTeamPileIdx(this.getPlayerIdx());
        int max=0;
        boolean selected = false;
        ArrayList<Card> characterCards = new ArrayList<Card>();
        //System.out.println(this.getPlayerIdx());

        for (var card: this.getHand().getCardList()) {

            if( isCharacter|| safeRanks.contains(((GoTCards.Rank) card.getRank())) && (!((GoTCards.Suit) card.getSuit()).isMagic())){
                // this card is safe to play on my own pile and this card isnt magic
                // check if this changes the battle outcome
                if(isCharacter &&(!((GoTCards.Suit) card.getSuit()).isCharacter())){
                    // need a character card
                    continue;
                }else if (isCharacter &&(((GoTCards.Suit) card.getSuit()).isCharacter())){
                    characterCards.add(card);
                    //System.out.println(GoTCards.canonical(card));
                }
                pile = tablePile.getTeamPileIdx(this.getPlayerIdx());

            }else if(((GoTCards.Suit) card.getSuit()).isMagic()){
                // this card is a magic card, check if this helps you win if you put it on enemy pile
                pile = tablePile.getTeamPileIdx(this.getPlayerIdx()+1);
            }
            this.setSelected(Optional.of(card));
            tablePile.selectPile(pile);
            if (!this.isLegalMove(tablePile)) {
                this.setSelected(Optional.empty());
                continue;
            }
            int countDifference = chekcChangesBattleOutcome(tablePile, card);
            if(countDifference>0 && max < countDifference){
                max = countDifference;
                selectedCard = Optional.of(card);
                selectedPile = pile;
                selected =true;
            }
        }
        //tablePile.getTeamPileIdx(this.getPlayerIdx())
        if(!selected){
            this.setSelected(Optional.empty());
        }
        if(!selected && isCharacter && !characterCards.isEmpty()){
            // if character card happens to be not selected and player has character cards
            // pick a random card
            tablePile.selectPile(tablePile.getTeamPileIdx(this.getPlayerIdx()));
            this.setSelected(Optional.of(characterCards.get(GameOfThrones.getRandom().nextInt(characterCards.size()))));
        }

        if(selected){
            this.setSelected(selectedCard);
            tablePile.selectPile(selectedPile);
        }
    }

    // checks if this move helps change the battle outcome in my teams favour
    private int chekcChangesBattleOutcome(TablePile tablePile, Card card){
        //System.out.println(GoTCards.canonical(card) + " ");
        int teamPileidx = tablePile.getTeamPileIdx(this.getPlayerIdx());
        int enemyPileidx = tablePile.getTeamPileIdx(this.getPlayerIdx()+1);
        int[] teamPileScore = tablePile.calculatePileRanks(teamPileidx);
        int[] enemyPileScore = tablePile.calculatePileRanks(enemyPileidx);

        int oldWinCount = countWins(tablePile, teamPileScore, enemyPileScore);


        //System.out.println(tablePile.getSelectedPile().getCardList().size());
        card.setVerso(false);
        tablePile.getSelectedPile().getCardList().add(card);
        int[] newTeamPileScore = tablePile.calculatePileRanks(teamPileidx);
        int[] newEnemyPileScore = tablePile.calculatePileRanks(enemyPileidx);

       // System.out.println(tablePile.getSelectedPile().getCardList().size());
        int newWinCount = countWins(tablePile, newTeamPileScore, newEnemyPileScore);
        //System.out.printf("Team pile scores: %d, %d. %d, %d ", teamPileScore[0], teamPileScore[1], newTeamPileScore[0], newTeamPileScore[1]);
        //System.out.printf("Enemy pile scores: %d, %d. %d, %d ", enemyPileScore[0], enemyPileScore[1], newEnemyPileScore[0], newEnemyPileScore[1]);
        tablePile.getSelectedPile().getCardList().remove(card);
        //System.out.println(newWinCount>oldWinCount);
         //System.out.println();
        return  newWinCount-oldWinCount;
    }

    private  int countWins(TablePile tablePile, int[] teamPileScore, int[] enemyPileScore){
        int winCount =0;
        if(teamPileScore[tablePile.getATTACK_RANK_INDEX()] > enemyPileScore[tablePile.getDEFENCE_RANK_INDEX()]){
            // winning attack
            winCount++;
        }
        if(teamPileScore[tablePile.getDEFENCE_RANK_INDEX()] > enemyPileScore[tablePile.getATTACK_RANK_INDEX()]){
            //winning
            winCount++;
        }
        return winCount;
    }

    private void updateSafeRanks(TablePile tablePile, boolean isCharacter){
        var usedCards= (isCharacter||first)?this.getHand().getCardList():tablePile.getUsedCards();
        if(isCharacter||first){
            first = false;
        }
        for(var card: usedCards){
            if(!((GoTCards.Suit) card.getSuit()).isMagic()){
                continue;
            }
           GoTCards.Rank rank  = ((GoTCards.Rank) card.getRank());
           if(!safeRanks.contains(rank)){
               safeRanks.add(rank);
           }
        }
    }

}
