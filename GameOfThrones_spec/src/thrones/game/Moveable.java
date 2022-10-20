package thrones.game;

public interface Moveable {
    void makeMove(Pile tablePile, boolean isCharacter);
    boolean isLegalMove( Pile tablePile);
    void pickACorrectSuit(boolean isCharacter);
    void selectPile(Pile tablePile);
}
