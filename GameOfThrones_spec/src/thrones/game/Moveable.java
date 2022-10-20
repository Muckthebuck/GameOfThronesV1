package thrones.game;

import ch.aplu.jcardgame.CardGame;

public interface Moveable {
    void makeMove(CardGame game, Pile tablePile, boolean isCharacter);
    boolean isLegalMove( Pile tablePile);
    void pickACorrectSuit(boolean isCharacter);
    void selectPile(Pile tablePile);
}
