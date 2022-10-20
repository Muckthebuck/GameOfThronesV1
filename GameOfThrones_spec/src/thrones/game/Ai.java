package thrones.game;

import java.util.Random;

public abstract class Ai extends Player {
    private final Random random;
    public Ai(RuleChecker rules,Random random) {
        super(rules);
        this.random = random;
    }

    public Random getRandom() {
        return random;
    }
}
