package thrones.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.CardGame;

import java.awt.*;
import java.io.InputStream;
import java.util.Random;
import thrones.game.PlayerFactory.PlayerType;

import java.util.Properties;



@SuppressWarnings("serial")
public class GameOfThrones extends CardGame {

    public static Font smallFont = new Font("Arial", Font.PLAIN, 10);
    public static Font bigFont = new Font("Arial", Font.BOLD, 36);
    static public int seed;
    static Random random;
    private final String version = "1.0";
    private final Table table;
    private static final String DEFAULT_PROPERTIES_PATH = "properties/got.properties";

    public GameOfThrones(Properties properties) {
        super(700, 700, 30);
        setTitle("Game of Thrones (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        table = new Table(this, properties);
    }

    public static void main(String[] args) {
        String propertiesPath = DEFAULT_PROPERTIES_PATH;
        if (args.length > 0) {
            propertiesPath = args[0];
        }
        final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);

        //System.out.println("Working Directory = " + System.getProperty("user.dir"));
        properties.setProperty("watchingTime", "5000");
        String seedProp = properties.getProperty("seed");  //Seed property
        if (seedProp != null) { // Use property seed
            seed = Integer.parseInt(seedProp);
        } else { // and no property
            seed = new Random().nextInt(); // so randomise
        }

        //GameOfThrones.seed = 130006;
        System.out.println("Seed = " + seed);


        GameOfThrones.random = new Random(seed);

        new GameOfThrones(properties);
    }

}
