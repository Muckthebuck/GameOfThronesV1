package thrones.game;

import ch.aplu.jcardgame.*;

import ch.aplu.jgamegrid.*;

import java.awt.*;
import java.util.Random;

public class Pile{
    private CardGame table;
    private Hand[] piles;
    private final int pileWidth = 40;
    private final int NON_SELECTION_VALUE = -1;
    private int selectedPileIndex = NON_SELECTION_VALUE;
    private final int UNDEFINED_INDEX = -1;
    private final int ATTACK_RANK_INDEX = 0;
    private final int DEFENCE_RANK_INDEX = 1;
    private Actor[] pileTextActors = { null, null };
    private static Random random;
    private final String[] playerTeams;
    Font smallFont = new Font("Arial", Font.PLAIN, 10);

    private final Location[] pileLocations = {
            new Location(350, 280),
            new Location(350, 430)
    };
    private final Location[] pileStatusLocations = {
            new Location(250, 200),
            new Location(250, 520)
    };


    public Pile(String[] playerTeams, Random random, CardGame table) {
        this.table = table;
        this.playerTeams = playerTeams;
        Pile.random = random;
    }

    public void resetPile(Deck deck) {
        if (piles != null) {
            for (Hand pile : piles) {
                pile.removeAll(true);
            }
        }
        piles = new Hand[2];
        for (int i = 0; i < 2; i++) {
            piles[i] = new Hand(deck);
            piles[i].setView(table, new RowLayout(pileLocations[i], 8 * pileWidth));
            piles[i].draw();
            final Hand currentPile = piles[i];
            final int pileIndex = i;
            piles[i].addCardListener(new CardAdapter() {
                public void leftClicked(Card card) {
                    selectedPileIndex = pileIndex;
                    currentPile.setTouchEnabled(false);
                }
            });
        }

        updatePileRanks();
    }

    void selectRandomPile() {
        selectedPileIndex = random.nextInt(2);
    }
    void waitForPileSelection() {
        selectedPileIndex = NON_SELECTION_VALUE;
        for (Hand pile : piles) {
            pile.setTouchEnabled(true);
        }
        while(selectedPileIndex == NON_SELECTION_VALUE) {
            Table.delay(100);
        }
        for (Hand pile : piles) {
            pile.setTouchEnabled(false);
        }
    }

    int[] calculatePileRanks(int pileIndex) {
        Hand currentPile = piles[pileIndex];
        int i = currentPile.isEmpty() ? 0 : ((GoTCards.Rank) currentPile.get(0).getRank()).getRankValue();
        return new int[] { i, i };
    }

    void updatePileRankState(int pileIndex, int attackRank, int defenceRank) {
        TextActor currentPile = (TextActor) pileTextActors[pileIndex];
        table.removeActor(currentPile);
        String text = playerTeams[pileIndex] + " Attack: " + attackRank + " - Defence: " + defenceRank;
        pileTextActors[pileIndex] = new TextActor(text, Color.WHITE, table.bgColor, smallFont);
        table.addActor(pileTextActors[pileIndex], pileStatusLocations[pileIndex]);
    }

    void updatePileRanks() {
        for (int j = 0; j < piles.length; j++) {
            int[] ranks = calculatePileRanks(j);
            updatePileRankState(j, ranks[ATTACK_RANK_INDEX], ranks[DEFENCE_RANK_INDEX]);
        }
    }

    void initPileTextActors(String text){
        for (int i = 0; i < pileTextActors.length; i++) {
            pileTextActors[i] = new TextActor(text, Color.WHITE, table.bgColor, smallFont);
            table.addActor(pileTextActors[i], pileStatusLocations[i]);
        }
    }


    public Hand[] getPiles() {
        return piles;
    }

    public Hand getSelectedPile(){
        return getPiles()[getSelectedPileIndex()];
    }

    public Location[] getPileLocations() {
        return pileLocations;
    }

    public Location[] getPileStatusLocations() {
        return pileStatusLocations;
    }

    public int getSelectedPileIndex() {
        return selectedPileIndex;
    }
}
