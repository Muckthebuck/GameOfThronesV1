package thrones.game;


import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class Table {

    static public int seed;
    static Random random;

    // return random Card from Hand

    private final String version = "1.0";
    public final int nbPlayers = 4;
    public final int nbStartCards = 9;
    public final int nbPlays = 6;
    public final int nbRounds = 3;
    private final int handWidth = 400;
    private final int pileWidth = 40;
    private Deck deck = new Deck(GameOfThrones.Suit.values(), GameOfThrones.Rank.values(), "cover");
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };

    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(25, 25),
            new Location(575, 125)
    };
    private final Location[] pileLocations = {
            new Location(350, 280),
            new Location(350, 430)
    };
    private final Location[] pileStatusLocations = {
            new Location(250, 200),
            new Location(250, 520)
    };

    private Actor[] pileTextActors = { null, null };
    private Actor[] scoreActors = {null, null, null, null};
    private final int watchingTime = 5000;
    private Hand[] hands;
    private Hand[] piles;
    private final String[] playerTeams = { "[Players 0 & 2]", "[Players 1 & 3]"};
    private int nextStartingPlayer = random.nextInt(nbPlayers);

    private int[] scores = new int[nbPlayers];

    Font bigFont = new Font("Arial", Font.BOLD, 36);
    Font smallFont = new Font("Arial", Font.PLAIN, 10);

    // boolean[] humanPlayers = { true, false, false, false};
    boolean[] humanPlayers = { true, false, false, false};


    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = 0;
            String text = "P" + i + "-0";
            scoreActors[i] = new TextActor(text, Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], scoreLocations[i]);
        }

        String text = "Attack: 0 - Defence: 0";
        for (int i = 0; i < pileTextActors.length; i++) {
            pileTextActors[i] = new TextActor(text, Color.WHITE, bgColor, smallFont);
            addActor(pileTextActors[i], pileStatusLocations[i]);
        }
    }

    private void updateScore(int player) {
        removeActor(scoreActors[player]);
        String text = "P" + player + "-" + scores[player];
        scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], scoreLocations[player]);
    }

    private void updateScores() {
        for (int i = 0; i < nbPlayers; i++) {
            updateScore(i);
        }
        System.out.println(playerTeams[0] + " score = " + scores[0] + "; " + playerTeams[1] + " score = " + scores[1]);
    }

    private Optional<Card> selected;
    private final int NON_SELECTION_VALUE = -1;
    private int selectedPileIndex = NON_SELECTION_VALUE;
    private final int UNDEFINED_INDEX = -1;
    private final int ATTACK_RANK_INDEX = 0;
    private final int DEFENCE_RANK_INDEX = 1;
    private void setupGame() {
        hands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            hands[i] = new Hand(deck);
        }
        Dealer.deal(hands, nbPlayers, nbStartCards, deck);

        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, true);
            System.out.println("hands[" + i + "]: " + GoTCards.canonical(hands[i]));
        }

        for (final Hand currentHand : hands) {
            // Set up human player for interaction
            currentHand.addCardListener(new CardAdapter() {
                public void leftDoubleClicked(Card card) {
                    selected = Optional.of(card);
                    currentHand.setTouchEnabled(false);
                }
                public void rightClicked(Card card) {
                    selected = Optional.empty(); // Don't care which card we right-clicked for player to pass
                    currentHand.setTouchEnabled(false);
                }
            });
        }
        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            hands[i].setView(this, layouts[i]);
            hands[i].draw();
        }
        // End graphics
    }

    

    private void pickACorrectSuit(int playerIndex, boolean isCharacter) {
        Hand currentHand = hands[playerIndex];
        List<Card> shortListCards = new ArrayList<>();
        for (int i = 0; i < currentHand.getCardList().size(); i++) {
            Card card = currentHand.getCardList().get(i);
            GameOfThrones.Suit suit = (GameOfThrones.Suit) card.getSuit();
            if (suit.isCharacter() == isCharacter) {
                shortListCards.add(card);
            }
        }
        if (shortListCards.isEmpty() || !isCharacter && random.nextInt(3) == 0) {
            selected = Optional.empty();
        } else {
            selected = Optional.of(shortListCards.get(random.nextInt(shortListCards.size())));
        }
    }



    private void waitForCorrectSuit(int playerIndex, boolean isCharacter) {
        if (hands[playerIndex].isEmpty()) {
            selected = Optional.empty();
        } else {
            selected = null;
            hands[playerIndex].setTouchEnabled(true);
            do {
                if (selected == null) {
                    delay(100);
                    continue;
                }
                GameOfThrones.Suit suit = selected.isPresent() ? (GameOfThrones.Suit) selected.get().getSuit() : null;
                if (isCharacter && suit != null && suit.isCharacter() ||         // If we want character, can't pass and suit must be right
                        !isCharacter && (suit == null || !suit.isCharacter())) { // If we don't want character, can pass or suit must not be character
                    // if (suit != null && suit.isCharacter() == isCharacter) {
                    break;
                } else {
                    selected = null;
                    hands[playerIndex].setTouchEnabled(true);
                }
                delay(100);
            } while (true);
        }
    }



    private int getPlayerIndex(int index) {
        return index % nbPlayers;
    }

    private void executeAPlay() {
        resetPile();

        nextStartingPlayer = getPlayerIndex(nextStartingPlayer);
        if (hands[nextStartingPlayer].getNumberOfCardsWithSuit(GameOfThrones.Suit.HEARTS) == 0)
            nextStartingPlayer = getPlayerIndex(nextStartingPlayer + 1);
        assert hands[nextStartingPlayer].getNumberOfCardsWithSuit(GameOfThrones.Suit.HEARTS) != 0 : " Starting player has no hearts.";

        // 1: play the first 2 hearts
        for (int i = 0; i < 2; i++) {
            int playerIndex = getPlayerIndex(nextStartingPlayer + i);
            setStatusText("Player " + playerIndex + " select a Heart card to play");
            if (humanPlayers[playerIndex]) {
                waitForCorrectSuit(playerIndex, true);
            } else {
                pickACorrectSuit(playerIndex, true);
            }

            int pileIndex = playerIndex % 2;
            assert selected.isPresent() : " Pass returned on selection of character.";
            System.out.println("Player " + playerIndex + " plays " + GoTCards.canonical(selected.get()) + " on pile " + pileIndex);
            selected.get().setVerso(false);
            selected.get().transfer(piles[pileIndex], true); // transfer to pile (includes graphic effect)
            updatePileRanks();
        }

        // 2: play the remaining nbPlayers * nbRounds - 2
        int remainingTurns = nbPlayers * nbRounds - 2;
        int nextPlayer = nextStartingPlayer + 2;

        while(remainingTurns > 0) {
            nextPlayer = getPlayerIndex(nextPlayer);
            setStatusText("Player" + nextPlayer + " select a non-Heart card to play.");
            if (humanPlayers[nextPlayer]) {
                waitForCorrectSuit(nextPlayer, false);
            } else {
                pickACorrectSuit(nextPlayer, false);
            }

            if (selected.isPresent()) {
                setStatusText("Selected: " + GoTCards.canonical(selected.get()) + ". Player" + nextPlayer + " select a pile to play the card.");
                if (humanPlayers[nextPlayer]) {
                    waitForPileSelection();
                } else {
                    selectRandomPile();
                }
                System.out.println("Player " + nextPlayer + " plays " + GoTCards.canonical(selected.get()) + " on pile " + selectedPileIndex);
                selected.get().setVerso(false);
                selected.get().transfer(piles[selectedPileIndex], true); // transfer to pile (includes graphic effect)
                updatePileRanks();
            } else {
                setStatusText("Pass.");
            }
            nextPlayer++;
            remainingTurns--;
        }

        // 3: calculate winning & update scores for players
        updatePileRanks();
        int[] pile0Ranks = calculatePileRanks(0);
        int[] pile1Ranks = calculatePileRanks(1);
        System.out.println("piles[0]: " + GoTCards.canonical(piles[0]));
        System.out.println("piles[0] is " + "Attack: " + pile0Ranks[ATTACK_RANK_INDEX] + " - Defence: " + pile0Ranks[DEFENCE_RANK_INDEX]);
        System.out.println("piles[1]: " +  GoTCards.canonical(piles[1]));
        System.out.println("piles[1] is " + "Attack: " + pile1Ranks[ATTACK_RANK_INDEX] + " - Defence: " + pile1Ranks[DEFENCE_RANK_INDEX]);
        GameOfThrones.Rank pile0CharacterRank = (GameOfThrones.Rank) piles[0].getCardList().get(0).getRank();
        GameOfThrones.Rank pile1CharacterRank = (GameOfThrones.Rank) piles[1].getCardList().get(0).getRank();
        String character0Result;
        String character1Result;

        if (pile0Ranks[ATTACK_RANK_INDEX] > pile1Ranks[DEFENCE_RANK_INDEX]) {
            scores[0] += pile1CharacterRank.getRankValue();
            scores[2] += pile1CharacterRank.getRankValue();
            character0Result = "Character 0 attack on character 1 succeeded.";
        } else {
            scores[1] += pile1CharacterRank.getRankValue();
            scores[3] += pile1CharacterRank.getRankValue();
            character0Result = "Character 0 attack on character 1 failed.";
        }

        if (pile1Ranks[ATTACK_RANK_INDEX] > pile0Ranks[DEFENCE_RANK_INDEX]) {
            scores[1] += pile0CharacterRank.getRankValue();
            scores[3] += pile0CharacterRank.getRankValue();
            character1Result = "Character 1 attack on character 0 succeeded.";
        } else {
            scores[0] += pile0CharacterRank.getRankValue();
            scores[2] += pile0CharacterRank.getRankValue();
            character1Result = "Character 1 attack character 0 failed.";
        }
        updateScores();
        System.out.println(character0Result);
        System.out.println(character1Result);
        setStatusText(character0Result + " " + character1Result);

        // 5: discarded all cards on the piles
        nextStartingPlayer += 1;
        delay(watchingTime);
    }

    public Table() {
        super(700, 700, 30);

        setTitle("Game of Thrones (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScore();

        setupGame();
        for (int i = 0; i < nbPlays; i++) {
            executeAPlay();
            updateScores();
        }

        String text;
        if (scores[0] > scores[1]) {
            text = "Players 0 and 2 won.";
        } else if (scores[0] == scores[1]) {
            text = "All players drew.";
        } else {
            text = "Players 1 and 3 won.";
        }
        System.out.println("Result: " + text);
        setStatusText(text);

        refresh();
    }

}
