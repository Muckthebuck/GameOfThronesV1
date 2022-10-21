package thrones.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.CardGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import thrones.game.PlayerFactory.PlayerType;

import java.util.Properties;



@SuppressWarnings("serial")
public class GameOfThrones extends CardGame {

    private static Font smallFont = new Font("Arial", Font.PLAIN, 10);
    private static Font bigFont = new Font("Arial", Font.BOLD, 36);
    private static int seed;
    private static Random random;
    private final String version = "1.0";
    private final Table table;
    private final ArrayList<PlayerType> playerTypes = new ArrayList<>();
    private static final String DEFAULT_PROPERTIES_PATH = "properties/got.properties";
    private String propertiesPath = DEFAULT_PROPERTIES_PATH;
    private final int DEFAULT_NPLAYERS = 4;
    private GameOfThrones(String[] args) {
        super(700, 700, 30);
        if (args.length > 0) {
            propertiesPath = args[0];
        }
        initWithProperties(propertiesPath);
        GameOfThrones.seed = 130006;
        System.out.println("Seed = " + seed);
        GameOfThrones.random = new Random(seed);
        setTitle("Game of Thrones (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        table = new Table(this);
    }

    public static void main(String[] args) {
        new GameOfThrones(args);
    }

    private void initWithProperties(String propertiesPath) {
        final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        assert properties != null;
        properties.setProperty("watchingTime", "5000");
        String seedProp = properties.getProperty("seed");  //Seed property
        if (seedProp != null) { // Use property seed
            GameOfThrones.seed = Integer.parseInt(seedProp);
        } else { // and no property
            GameOfThrones.seed = new Random().nextInt(); // so randomise
        }
        GameOfThrones.random = new Random(seed);
        String temp;
        for(int i =0; (temp = properties.getProperty("players." + i))!=null; i++){
            getPlayerTypes().add(PlayerType.valueOf(temp));
        }
        if(getPlayerTypes().size()< DEFAULT_NPLAYERS){
            for(int i = getPlayerTypes().size(); i<DEFAULT_NPLAYERS;i++){
                getPlayerTypes().add(PlayerType.random);
            }
        }
    }

    public static Font getSmallFont() {
        return smallFont;
    }


    public static Font getBigFont() {
        return bigFont;
    }

    public static int getSeed() {
        return seed;
    }

    public static Random getRandom() {
        return random;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public ArrayList<PlayerType> getPlayerTypes() {
        return playerTypes;
    }
}
