package thrones.game;

import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;

public class PlayerFactory {
    public final int nbStartCards = 9;
    public enum PlayerType {
        HUMAN,
        RANDOM,
        SIMPLE,
        SMART
    }

    // initialising the player as human by default
    PlayerType playerType = PlayerType.HUMAN;


    public Player[] setUpPlayers(PlayerType[] playerTypes, RuleChecker rules, Deck deck){
        Player[] players = new Player[playerTypes.length];
        for (int i = 0; i < playerTypes.length; i++) {
            players[i] = getPlayer(playerTypes[i], rules, i);
            players[i].setHand(new Hand(deck));
        }
        Dealer.deal(players, playerTypes.length, nbStartCards, deck);
        return  players;
    }
    public Player getPlayer(PlayerType playerType, RuleChecker rules, int idx) {
        if (playerType == PlayerType.HUMAN ) {
            // player is human and we use the human class
            return new Human(rules, idx);
        }

        if (playerType == playerType.RANDOM) {
            // player is an AI and is plays randomly
            return new RandomAi(rules, GameOfThrones.random, idx);
        }


        if (playerType == PlayerType.SIMPLE) {
            // player is an AI and is playing simply
            return new SimpleAi(rules, GameOfThrones.random, idx);
        }

        if (playerType == PlayerType.SMART) {
            // player is an AI and is playing smartly
            return new SmartAi(rules, GameOfThrones.random, idx);
        }
        return null;
    }
}