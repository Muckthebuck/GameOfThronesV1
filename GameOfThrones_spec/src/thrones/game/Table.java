package thrones.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;
import thrones.game.GoTCards.*;

@SuppressWarnings("serial")
public class Table {
    private final CardGame Game;
    public final int nbPlayers = 4;
    public final int nbStartCards = 9;
    public final int nbPlays = 6;
    public final int nbRounds = 3;
    private final int handWidth = 400;
    private final int pileWidth = 40;
    private Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final String[] playerTeams = { "[Players 0 & 2]", "[Players 1 & 3]"};
    private final CompositeRule rule = new CompositeRule();

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

    private Actor[] scoreActors = {null, null, null, null};
    private final int watchingTime = 5000;
    private Hand[] hands;
    private Pile tablePile;

    private int nextStartingPlayer = GameOfThrones.random.nextInt(nbPlayers);

    private int[] scores = new int[nbPlayers];



    // boolean[] humanPlayers = { true, false, false, false};
    boolean[] humanPlayers = { true, false, false, false};

    private Optional<Card> selected;
    private final int NON_SELECTION_VALUE = -1;

    private final int UNDEFINED_INDEX = -1;
    private final int ATTACK_RANK_INDEX = 0;
    private final int DEFENCE_RANK_INDEX = 1;


    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = 0;
            String text = "P" + i + "-0";
            scoreActors[i] = new TextActor(text, Color.WHITE, Game.bgColor, GameOfThrones.bigFont);
            Game.addActor(scoreActors[i], scoreLocations[i]);
        }

        String text = "Attack: 0 - Defence: 0";
        tablePile.initPileTextActors(text);
    }

    // PILE, caluclate score stuff
    private void updateScore(int player) {
        Game.removeActor(scoreActors[player]);
        String text = "P" + player + "-" + scores[player];
        scoreActors[player] = new TextActor(text, Color.WHITE, Game.bgColor, GameOfThrones.bigFont);
        Game.addActor(scoreActors[player], scoreLocations[player]);
    }

    // PILE, calculate score stuff
    private void updateScores() {
        for (int i = 0; i < nbPlayers; i++) {
            updateScore(i);
        }
        System.out.println(playerTeams[0] + " score = " + scores[0] + "; " + playerTeams[1] + " score = " + scores[1]);
    }

    // TABLE but make more helper functions/ class to handle display stuff
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
            hands[i].setView(Game, layouts[i]);
            hands[i].draw();
        }
        // End graphics
    }

    // RULES
    private void pickACorrectSuit(int playerIndex, boolean isCharacter) {
        Hand currentHand = hands[playerIndex];
        List<Card> shortListCards = new ArrayList<>();
        for (int i = 0; i < currentHand.getCardList().size(); i++) {
            Card card = currentHand.getCardList().get(i);
            Suit suit = (Suit) card.getSuit();
            if (suit.isCharacter() == isCharacter) {
                shortListCards.add(card);
            }
        }
        if (shortListCards.isEmpty() || !isCharacter && GameOfThrones.random.nextInt(3) == 0) {
            selected = Optional.empty();
        } else {
            selected = Optional.of(shortListCards.get(GameOfThrones.random.nextInt(shortListCards.size())));
        }
    }

    // RULES
    private void waitForCorrectSuit(int playerIndex, boolean isCharacter) {
        if (hands[playerIndex].isEmpty()) {
            selected = Optional.empty();
        } else {
            selected = null;
            hands[playerIndex].setTouchEnabled(true);
            do {
                if (selected == null) {
                    GameOfThrones.delay(100);
                    continue;
                }
                Suit suit = selected.isPresent() ? (Suit) selected.get().getSuit() : null;
                if (isCharacter && suit != null && suit.isCharacter() ||         // If we want character, can't pass and suit must be right
                        !isCharacter && (suit == null || !suit.isCharacter())) { // If we don't want character, can pass or suit must not be character
                    // if (suit != null && suit.isCharacter() == isCharacter) {
                    break;
                } else {
                    selected = null;
                    hands[playerIndex].setTouchEnabled(true);
                }
                GameOfThrones.delay(100);
            } while (true);
        }
    }

    // PLAYER?
    private int getPlayerIndex(int index) {
        return index % nbPlayers;
    }

    // TABLE
    private void executeAPlay() {
        tablePile.resetPile(deck);

        nextStartingPlayer = getPlayerIndex(nextStartingPlayer);
        if (hands[nextStartingPlayer].getNumberOfCardsWithSuit(Suit.HEARTS) == 0)
            nextStartingPlayer = getPlayerIndex(nextStartingPlayer + 1);
        assert hands[nextStartingPlayer].getNumberOfCardsWithSuit(Suit.HEARTS) != 0 : " Starting player has no hearts.";

        // 1: play the first 2 hearts
        for (int i = 0; i < 2; i++) {
            int playerIndex = getPlayerIndex(nextStartingPlayer + i);
            Game.setStatusText("Player " + playerIndex + " select a Heart card to play");
            if (humanPlayers[playerIndex]) {
                waitForCorrectSuit(playerIndex, true);
            } else {
                pickACorrectSuit(playerIndex, true);
            }

            int pileIndex = playerIndex % 2;
            assert selected.isPresent() : " Pass returned on selection of character.";
            System.out.println("Player " + playerIndex + " plays " + GoTCards.canonical(selected.get()) + " on pile " + pileIndex);
            selected.get().setVerso(false);
            selected.get().transfer(tablePile.getPiles()[pileIndex], true); // transfer to pile (includes graphic effect)
            tablePile.updatePileRanks();
        }

        // 2: play the remaining nbPlayers * nbRounds - 2
        int remainingTurns = nbPlayers * nbRounds - 2;
        int nextPlayer = nextStartingPlayer + 2;

        while(remainingTurns > 0) {
            nextPlayer = getPlayerIndex(nextPlayer);
            Game.setStatusText("Player" + nextPlayer + " select a non-Heart card to play.");
            if (humanPlayers[nextPlayer]) {
                waitForCorrectSuit(nextPlayer, false);
            } else {
                pickACorrectSuit(nextPlayer, false);
            }

            if (selected.isPresent()) {
                Game.setStatusText("Selected: " + GoTCards.canonical(selected.get()) + ". Player" + nextPlayer + " select a pile to play the card.");

                //// we set moves here
                if (humanPlayers[nextPlayer]) {
                    tablePile.waitForPileSelection();
                } else {
                    tablePile.selectRandomPile();
                }

                rule.checkValidMove((Suit) selected.get().getSuit(), tablePile.getSelectedPile());
                ////
                System.out.println("Player " + nextPlayer + " plays " + GoTCards.canonical(selected.get()) + " on pile " + tablePile.getSelectedPileIndex());
                selected.get().setVerso(false);
                selected.get().transfer(tablePile.getSelectedPile(), true); // transfer to pile (includes graphic effect)
                tablePile.updatePileRanks();
            } else {
                Game.setStatusText("Pass.");
            }
            nextPlayer++;
            remainingTurns--;
        }

        // 3: calculate winning & update scores for players
        tablePile.updatePileRanks();
        int[] pile0Ranks = tablePile.calculatePileRanks(0);
        int[] pile1Ranks = tablePile.calculatePileRanks(1);
        System.out.println("piles[0]: " + GoTCards.canonical(tablePile.getPiles()[0]));
        System.out.println("piles[0] is " + "Attack: " + pile0Ranks[ATTACK_RANK_INDEX] + " - Defence: " + pile0Ranks[DEFENCE_RANK_INDEX]);
        System.out.println("piles[1]: " + GoTCards.canonical(tablePile.getPiles()[1]));
        System.out.println("piles[1] is " + "Attack: " + pile1Ranks[ATTACK_RANK_INDEX] + " - Defence: " + pile1Ranks[DEFENCE_RANK_INDEX]);
        Rank pile0CharacterRank = (Rank) tablePile.getPiles()[0].getCardList().get(0).getRank();
        Rank pile1CharacterRank = (Rank) tablePile.getPiles()[1].getCardList().get(0).getRank();
        String character0Result;
        String character1Result;


        // this is where we set scores
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
        ///


        System.out.println(character0Result);
        System.out.println(character1Result);
        Game.setStatusText(character0Result + " " + character1Result);

        // 5: discarded all cards on the piles
        nextStartingPlayer += 1;
        GameOfThrones.delay(watchingTime);
    }

    public Table(CardGame game) {
        this.Game = game;
        tablePile = new Pile(playerTeams, GameOfThrones.random, game);
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
        game.setStatusText(text);
        game.refresh();
    }


}
