package thrones.game;

import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;

public class PlayerFactory {
    public final int nbStartCards = 9;
    // initialising the player as human by default
    PlayerType playerType = PlayerType.human;

    public Player[] setUpPlayers(ArrayList<PlayerType> playerTypes, RuleChecker rules, Deck deck) {
        Player[] players = new Player[playerTypes.size()];
        for (int i = 0; i < playerTypes.size(); i++) {
            players[i] = getPlayer(playerTypes.get(i), rules, i);
            players[i].setHand(new Hand(deck));
        }
        Dealer.deal(players, playerTypes.size(), nbStartCards, deck);
        return players;
    }

    public Player getPlayer(PlayerType playerType, RuleChecker rules, int idx) {
        if (playerType == PlayerType.human) {
            // player is human and we use the human class
            return new Human(rules, idx);
        }

        if (playerType == PlayerType.random) {
            // player is an AI and is plays randomly
            return new RandomAi(rules, GameOfThrones.getRandom(), idx);
        }


        if (playerType == PlayerType.simple) {
            // player is an AI and is playing simply
            return new SimpleAi(rules, GameOfThrones.getRandom(), idx);
        }

        if (playerType == PlayerType.smart) {
            // player is an AI and is playing smartly
            return new SmartAi(rules, GameOfThrones.getRandom(), idx);
        }
        return null;
    }

    public enum PlayerType {
        human,
        random,
        simple,
        smart;

    }
}