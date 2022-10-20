package thrones.game;

public class PlayerFactory {

    public enum PlayerType {
        HUMAN,
        RANDOM,
        SIMPLE,
        SMART
    }

    // initialising the player as human by default
    PlayerType playerType = PlayerType.HUMAN;


    public Player[] setUpPlayers(PlayerType[] playerTypes, RuleChecker rules){
        Player[] players = new Player[playerTypes.length];
        for (int i = 0; i < playerTypes.length; i++) {
            players[i] = getPlayer(playerTypes[i],  rules);
        }
        return  players;
    }
    public Player getPlayer(PlayerType playerType, RuleChecker rules) {
        if (playerType == PlayerType.HUMAN ) {
            // player is human and we use the human class
            return new Human(rules);
        }

        if (playerType == playerType.RANDOM) {
            // player is an AI and is plays randomly
            return new RandomAi(rules, GameOfThrones.random);
        }


        if (playerType == PlayerType.SIMPLE) {
            // player is an AI and is playing simply
            return new SimpleAi(rules, GameOfThrones.random);
        }

        if (playerType == PlayerType.SMART) {
            // player is an AI and is playing smartly
            return new SmartAi(rules, GameOfThrones.random);
        }
        return null;
    }
}