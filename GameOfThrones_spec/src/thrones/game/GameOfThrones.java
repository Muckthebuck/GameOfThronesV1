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

    private static final Font SMALLFONT = new Font("Arial", Font.PLAIN, 10);
    private static final Font BIGFONT = new Font("Arial", Font.BOLD, 36);
    private static int SEED;
    private static Random RANDOM;
    private static final int DEFAULT_NPLAYERS = 4;
    private static final String VERSION = "1.0";
    private static final String DEFAULT_PROPERTIES_PATH = "properties/got.properties";
    private static int WATCHING_TIME;
    private final Table table;
    private final ArrayList<PlayerType> playerTypes = new ArrayList<>();
    private String propertiesPath = DEFAULT_PROPERTIES_PATH;

    private GameOfThrones(String[] args) {
        super(700, 700, 30);
        if (args.length > 0) {
            propertiesPath = args[0];
        }
        initWithProperties(propertiesPath);
        System.out.println("Seed = " + GameOfThrones.getSeed());
        setTitle("Game of Thrones (V" + VERSION + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
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

        GameOfThrones.WATCHING_TIME = Integer.parseInt(properties.getProperty("watchingTime"));
        String seedProp = properties.getProperty("seed");  //Seed property
        if (seedProp != null) { // Use property seed
            SEED = Integer.parseInt(seedProp);
        } else { // and no property
            GameOfThrones.setSeed(new Random().nextInt()); // so randomise
        }
        GameOfThrones.setRandom();
        String temp;
        for(int i =0; (temp = properties.getProperty("players." + i))!=null; i++){
            getPlayerTypes().add(PlayerType.valueOf(temp));
        }

        if(getPlayerTypes().size()< DEFAULT_NPLAYERS){
            // add random player by default
            for(int i = getPlayerTypes().size(); i<DEFAULT_NPLAYERS;i++){
                getPlayerTypes().add(PlayerType.random);
            }
        }
    }

    public static Font getSMALLFONT() {
        return SMALLFONT;
    }


    public static Font getBIGFONT() {
        return BIGFONT;
    }

    public static int getSeed() {
        return SEED;
    }

    private static void setSeed(int seed){
        GameOfThrones.SEED = seed;
    }
    private static void setRandom(){
        GameOfThrones.RANDOM = new Random(GameOfThrones.getSeed());
    }

    public static Random getRandom() {
        return RANDOM;
    }

    public static int getWatchingTime() {
        return WATCHING_TIME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    public ArrayList<PlayerType> getPlayerTypes() {
        return playerTypes;
    }
}
