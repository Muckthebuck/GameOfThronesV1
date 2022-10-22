package thrones.game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class TablePile {
    private final CardGame game;
    private final int PILE_WIDTH = 40;
    private final int NON_SELECTION_VALUE = -1;
    private final int UNDEFINED_INDEX = -1;
    private final int ATTACK_RANK_INDEX = 0;
    private final int DEFENCE_RANK_INDEX = 1;
    private final Random random;
    private final String[] playerTeams;
    private final Location[] pileLocations = {
            new Location(350, 280),
            new Location(350, 430)
    };
    private final Location[] pileStatusLocations = {
            new Location(250, 200),
            new Location(250, 520)
    };
    private Hand[] piles;
    private int selectedPileIndex = NON_SELECTION_VALUE;
    private final Actor[] pileTextActors = {null, null};
    private final ScoreCalculator scoreCalculator = new ScoreCalculator(ATTACK_RANK_INDEX, DEFENCE_RANK_INDEX);
    private final ArrayList<Card> usedCards = new ArrayList<>();

    public TablePile(String[] playerTeams, Random random, CardGame game) {
        this.game = game;
        this.playerTeams = playerTeams;
        this.random = random;
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
            piles[i].setView(game, new RowLayout(pileLocations[i], 8 * PILE_WIDTH));
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

    int getTeamPileIdx(int playerIdx) {
        return playerIdx % 2;
    }


    void selectTeamPile(int playerIdx) {
        selectedPileIndex = playerIdx % 2;
    }

    void selectPile(int pileIdx){
        selectedPileIndex = pileIdx;
    }

    void selectRandomPile() {
        selectedPileIndex = random.nextInt(2);
    }

    void waitForPileSelection() {
        selectedPileIndex = NON_SELECTION_VALUE;
        for (Hand pile : piles) {
            pile.setTouchEnabled(true);
        }
        while (selectedPileIndex == NON_SELECTION_VALUE) {
            GameOfThrones.delay(100);
        }
        for (Hand pile : piles) {
            pile.setTouchEnabled(false);
        }
    }

    int[] calculatePileRanks(int pileIndex) {
        Hand currentPile = piles[pileIndex];
        return scoreCalculator.calculate(currentPile);
    }

    void updatePileRankState(int pileIndex, int attackRank, int defenceRank) {
        TextActor currentPile = (TextActor) pileTextActors[pileIndex];
        game.removeActor(currentPile);
        String text = playerTeams[pileIndex] + " Attack: " + attackRank + " - Defence: " + defenceRank;
        pileTextActors[pileIndex] = new TextActor(text, Color.WHITE, game.bgColor, GameOfThrones.getSMALLFONT());
        game.addActor(pileTextActors[pileIndex], pileStatusLocations[pileIndex]);
    }

    void updatePileRanks() {
        for (int j = 0; j < piles.length; j++) {
            int[] ranks = calculatePileRanks(j);
            updatePileRankState(j, ranks[ATTACK_RANK_INDEX], ranks[DEFENCE_RANK_INDEX]);
        }
    }

    void initPileTextActors(String text) {
        for (int i = 0; i < pileTextActors.length; i++) {
            pileTextActors[i] = new TextActor(text, Color.WHITE, game.bgColor, GameOfThrones.getSMALLFONT());
            game.addActor(pileTextActors[i], pileStatusLocations[i]);
        }
    }

    void transferCardToPile(Card card) {
        card.setVerso(false);
        card.transfer(this.getSelectedPile(), true); // transfer to pile (includes graphic effect)
        usedCards.add(card);
        this.updatePileRanks();
    }


    public Hand[] getPiles() {
        return piles;
    }

    public Hand getSelectedPile() {
        return getPiles()[getSelectedPileIndex()];
    }

    public Hand getSelectedPile(int pileIndex) {
        return getPiles()[pileIndex];
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

    public ArrayList<Card> getUsedCards() {
        return usedCards;
    }

    public int getATTACK_RANK_INDEX() {
        return ATTACK_RANK_INDEX;
    }

    public int getDEFENCE_RANK_INDEX() {
        return DEFENCE_RANK_INDEX;
    }
}
