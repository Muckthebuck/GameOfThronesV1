package thrones.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.Location;
import thrones.game.GoTCards.Rank;
import thrones.game.GoTCards.Suit;
import thrones.game.PlayerFactory.PlayerType;
import java.util.Properties;
import java.util.Optional;

@SuppressWarnings("serial")
public class Table {
    public final int nbPlayers = 4;
    public final int nbPlays = 6;
    public final int nbRounds = 3;

    private final int handWidth = 400;
    private final String[] playerTeams = {"[Players 0 & 2]", "[Players 1 & 3]"};
    private final RuleChecker rules = new RuleChecker();
    private final ScoreHandler scoreHandler;
    private final PlayerFactory playerFactory;
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };
    private final int watchingTime = 5000;

    private final int ATTACK_RANK_INDEX = 0;
    private final int DEFENCE_RANK_INDEX = 1;
    // boolean[] humanPlayers = { true, false, false, false};
    //boolean[] humanPlayers = {true, false, false, false};
    //PlayerType[] playerTypes = {PlayerType.HUMAN, PlayerType.RANDOM, PlayerType.RANDOM, PlayerType.RANDOM};

    Player[] players;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    //private Hand[] hands;
    private final Pile tablePile;
    private final CardGame Game;
    private int nextStartingPlayer = GameOfThrones.random.nextInt(nbPlayers);
    private int seed = 130006;
    PlayerType[] playerTypes;

    public static PlayerType checkPlayer( String player) {
        /* method to check what the player values are in properties and change PlayerType accordingly*/
        if (player.equals("human")) {
            return PlayerType.HUMAN;
        }
        if (player.equals("random")) {
            System.out.println("r");
            return PlayerType.RANDOM;
        }
        if (player.equals("simple")) {
            return PlayerType.SIMPLE;
        }
        if (player.equals("smart")) {
            return PlayerType.SMART;
        }
        //default to random
        return PlayerType.RANDOM;
    }
    public PlayerType[] initWithProperties (Properties properties) {

        String player = properties.getProperty("players.0");
        PlayerType player0 = checkPlayer(player);


        player = properties.getProperty("players.1");
        PlayerType player1 = checkPlayer(player);

        player = properties.getProperty("players.2");
        PlayerType player2 = checkPlayer(player);

        player = properties.getProperty("players.3");
        PlayerType player3 = checkPlayer(player);

        PlayerType[] playerTypes = {player0, player1, player2, player3};
        /*System.out.println(player0);
        System.out.println(player1);
        System.out.println(player2);
        System.out.println(player3);*/
        return playerTypes;
    }
    public Table(CardGame game, Properties properties) {
        playerTypes = initWithProperties(properties);
        this.Game = game;
        tablePile = new Pile(playerTeams, GameOfThrones.random, game);
        scoreHandler = new ScoreHandler(nbPlays, nbPlayers, game, tablePile, playerTeams);
        playerFactory = new PlayerFactory();
        setupGame();
        for (int i = 0; i < nbPlays; i++) {
            executeAPlay();
            scoreHandler.updateScores();
        }

        scoreHandler.displayResults();
        game.refresh();
    }

    // TABLE but make more helper functions/ class to handle display stuff
    private void setupGame() {
        players = playerFactory.setUpPlayers(playerTypes, rules, deck);

        for (final Player player : players) {
            final Hand currentHand = player.getHand();
            // Set up human player for interaction
            currentHand.addCardListener(new CardAdapter() {
                public void leftDoubleClicked(Card card) {
                    player.setSelected(Optional.of(card));
                    currentHand.setTouchEnabled(false);
                }

                public void rightClicked(Card card) {
                    player.setSelected(Optional.empty()); // Don't care which card we right-clicked for player to pass
                    currentHand.setTouchEnabled(false);
                }
            });
        }
        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            players[i].getHand().setView(Game, layouts[i]);
            players[i].getHand().draw();
        }
        // End graphics
    }

    // PLAYER?
    private int getPlayerIndex(int index) {
        return index % nbPlayers;
    }

    // TABLE
    private void executeAPlay() {
        tablePile.resetPile(deck);

        nextStartingPlayer = getPlayerIndex(nextStartingPlayer);
        if (players[nextStartingPlayer].getHand().getNumberOfCardsWithSuit(Suit.HEARTS) == 0)
            nextStartingPlayer = getPlayerIndex(nextStartingPlayer + 1);
        assert players[nextStartingPlayer].getHand().getNumberOfCardsWithSuit(Suit.HEARTS) != 0 : " Starting player has no hearts.";

        // 1: play the first 2 hearts
        Optional<Card> selected;
        for (int i = 0; i < 2; i++) {
            int playerIndex = getPlayerIndex(nextStartingPlayer + i);

            // get move from player
            players[playerIndex].makeMove(Game, tablePile, true);
            selected = players[playerIndex].getSelected();

            assert selected.isPresent() : " Pass returned on selection of character.";
            System.out.println("Player " + playerIndex + " plays " + GoTCards.canonical(selected.get()) + " on pile " + tablePile.getSelectedPile());
            tablePile.transferCardToPile(selected.get());
        }

        // 2: play the remaining nbPlayers * nbRounds - 2
        int remainingTurns = nbPlayers * nbRounds - 2;
        int nextPlayer = nextStartingPlayer + 2;

        while (remainingTurns > 0) {
            nextPlayer = getPlayerIndex(nextPlayer);

            players[nextPlayer].makeMove(Game, tablePile, false);
            selected = players[nextPlayer].getSelected();

            if (selected.isPresent()) {
                try {
                    rules.checkMove((Suit) selected.get().getSuit(), tablePile.getSelectedPile(), true);
                } catch (BrokeRuleException e) {
                    System.err.println("Caught BrokeRuleException: " + e.getMessage());
                }
                System.out.println("Player " + nextPlayer + " plays " + GoTCards.canonical(selected.get()) + " on pile " + tablePile.getSelectedPileIndex());
                tablePile.transferCardToPile(selected.get());
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


}
