package thrones.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.CardGame;

import java.awt.*;
import java.util.Random;

@SuppressWarnings("serial")
public class GameOfThrones extends CardGame {

    public static Font smallFont = new Font("Arial", Font.PLAIN, 10);
    public static Font bigFont = new Font("Arial", Font.BOLD, 36);
    static public int seed;
    static Random random;
    private final String version = "1.0";
    private final Table table;

    public GameOfThrones() {
        super(700, 700, 30);
        setTitle("Game of Thrones (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        table = new Table(this);
    }

    public static void main(String[] args) {
        // System.out.println("Working Directory = " + System.getProperty("user.dir"));
        // final Properties properties = new Properties();
        // properties.setProperty("watchingTime", "5000");
        /*
        if (args == null || args.length == 0) {
            //  properties = PropertiesLoader.loadPropertiesFile("cribbage.properties");
        } else {
            //  properties = PropertiesLoader.loadPropertiesFile(args[0]);
        }

        String seedProp = properties.getProperty("seed");  //Seed property
        if (seedProp != null) { // Use property seed
			  seed = Integer.parseInt(seedProp);
        } else { // and no property
			  seed = new Random().nextInt(); // so randomise
        }
        */
        GameOfThrones.seed = 130006;
        System.out.println("Seed = " + seed);
        GameOfThrones.random = new Random(seed);
        new GameOfThrones();
    }

}
