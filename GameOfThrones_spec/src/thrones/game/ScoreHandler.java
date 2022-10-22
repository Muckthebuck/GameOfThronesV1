package thrones.game;

import ch.aplu.jcardgame.CardGame;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;

import java.awt.*;

public class ScoreHandler {
    public final int nbPlays;
    public final int nbPlayers;
    private final CardGame Game;
    private final String[] playerTeams;
    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(25, 25),
            new Location(575, 125)
    };
    private final Actor[] scoreActors = {null, null, null, null};
    private final int[] scores;
    private final TablePile tablePile;

    public ScoreHandler(int nbPlays, int nbPlayers, CardGame game, TablePile tablePile, String[] playerTeams) {
        this.nbPlays = nbPlays;
        this.nbPlayers = nbPlayers;
        this.Game = game;
        this.scores = new int[nbPlayers];
        this.tablePile = tablePile;
        this.playerTeams = playerTeams;
        initScore();
    }

    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = 0;
            String text = "P" + i + "-0";
            scoreActors[i] = new TextActor(text, Color.WHITE, Game.bgColor, GameOfThrones.getBIGFONT());
            Game.addActor(scoreActors[i], scoreLocations[i]);
        }

        String text = "Attack: 0 - Defence: 0";
        tablePile.initPileTextActors(text);
    }

    // PILE, caluclate score stuff
    public void updateScore(int player) {
        Game.removeActor(scoreActors[player]);
        String text = "P" + player + "-" + scores[player];
        scoreActors[player] = new TextActor(text, Color.WHITE, Game.bgColor, GameOfThrones.getBIGFONT());
        Game.addActor(scoreActors[player], scoreLocations[player]);
    }

    // PILE, calculate score stuff
    public void updateScores() {
        for (int i = 0; i < nbPlayers; i++) {
            updateScore(i);
        }
        System.out.println(playerTeams[0] + " score = " + scores[0] + "; " + playerTeams[1] + " score = " + scores[1]);
    }

    public void setScores(int ATTACK_RANK_INDEX, int DEFENCE_RANK_INDEX) {
        // 3: calculate winning & update scores for players
        tablePile.updatePileRanks();
        int[] pile0Ranks = tablePile.calculatePileRanks(0);
        int[] pile1Ranks = tablePile.calculatePileRanks(1);
        System.out.println("piles[0]: " + GoTCards.canonical(tablePile.getPiles()[0]));
        System.out.println("piles[0] is " + "Attack: " + pile0Ranks[ATTACK_RANK_INDEX] + " - Defence: " + pile0Ranks[DEFENCE_RANK_INDEX]);
        System.out.println("piles[1]: " + GoTCards.canonical(tablePile.getPiles()[1]));
        System.out.println("piles[1] is " + "Attack: " + pile1Ranks[ATTACK_RANK_INDEX] + " - Defence: " + pile1Ranks[DEFENCE_RANK_INDEX]);
        GoTCards.Rank pile0CharacterRank = (GoTCards.Rank) tablePile.getPiles()[0].getCardList().get(0).getRank();
        GoTCards.Rank pile1CharacterRank = (GoTCards.Rank) tablePile.getPiles()[1].getCardList().get(0).getRank();
        String character0Result;
        String character1Result;
        // this is where we set scores
        if (pile0Ranks[ATTACK_RANK_INDEX] > pile1Ranks[DEFENCE_RANK_INDEX]) {
            scores[0] += pile1CharacterRank.getRankValue();
            scores[2] += pile1CharacterRank.getRankValue();
            character0Result = "Character 0 attack on character 1 succeeded.";
        } else {
            scores[1] += pile1CharacterRank.getRankValue();
            scores[3] += pile1CharacterRank.getRankValue();
            character0Result = "Character 0 attack on character 1 failed.";
        }

        if (pile1Ranks[ATTACK_RANK_INDEX] > pile0Ranks[DEFENCE_RANK_INDEX]) {
            scores[1] += pile0CharacterRank.getRankValue();
            scores[3] += pile0CharacterRank.getRankValue();
            character1Result = "Character 1 attack on character 0 succeeded.";
        } else {
            scores[0] += pile0CharacterRank.getRankValue();
            scores[2] += pile0CharacterRank.getRankValue();
            character1Result = "Character 1 attack on character 0 failed.";
        }
        updateScores();
        System.out.println(character0Result);
        System.out.println(character1Result);
        Game.setStatusText(character0Result + " " + character1Result);
    }

    public void displayResults() {
        String text;
        if (scores[0] > scores[1]) {
            text = "Players 0 and 2 won.";
        } else if (scores[0] == scores[1]) {
            text = "All players drew.";
        } else {
            text = "Players 1 and 3 won.";
        }
        System.out.println("Result: " + text);
        Game.setStatusText(text);
    }
}
