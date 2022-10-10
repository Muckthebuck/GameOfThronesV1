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

    public Player getPlayer(PlayerType playerType) {
        if (playerType == PlayerType.HUMAN ) {
            // player is human and we use the human class
            return new Human();
        }

        if (playerType == playerType.RANDOM) {
            // player is an AI and is plays randomly
            return new RandomAi();
        }


        if (playerType == PlayerType.SIMPLE) {
            // player is an AI and is playing simply
            return new SimpleAi();
        }

        if (playerType == PlayerType.SMART) {
            // player is an AI and is playing smartly
            return new SmartAi();
        }
        return null;
    }
}