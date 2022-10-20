package thrones.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;
import thrones.game.GoTCards.Rank;
import thrones.game.GoTCards.Suit;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final RuleChecker rules = new RuleChecker();
    private  final ScoreHandler scoreHandler;

    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };




    private final int watchingTime = 5000;
    private Hand[] hands;
    private Pile tablePile;

    private int nextStartingPlayer = GameOfThrones.random.nextInt(nbPlayers);





    // boolean[] humanPlayers = { true, false, false, false};
    boolean[] humanPlayers = { true, false, false, false};

    private Optional<Card> selected;
    private final int NON_SELECTION_VALUE = -1;

    private final int UNDEFINED_INDEX = -1;
    private final int ATTACK_RANK_INDEX = 0;
    private final int DEFENCE_RANK_INDEX = 1;




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

    // AI
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

    // HUMAN
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

            // get move from player

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

                try {
                    rules.checkMove((Suit) selected.get().getSuit(), tablePile.getSelectedPile(), true);
                } catch (BrokeRuleException e) {
                    System.err.println("Caught BrokeRuleException: " + e.getMessage());
                }

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
        scoreHandler.setScores(ATTACK_RANK_INDEX, DEFENCE_RANK_INDEX);

        // 5: discarded all cards on the piles
        nextStartingPlayer += 1;
        GameOfThrones.delay(watchingTime);
    }

    public Table(CardGame game) {
        this.Game = game;
        tablePile = new Pile(playerTeams, GameOfThrones.random, game);
        scoreHandler = new ScoreHandler(nbPlays, nbPlayers, game, tablePile, playerTeams);

        setupGame();
        for (int i = 0; i < nbPlays; i++) {
            executeAPlay();
            scoreHandler.updateScores();
        }

        scoreHandler.displayResults();
        game.refresh();
    }


}
