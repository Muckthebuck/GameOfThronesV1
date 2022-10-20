package thrones.game;

import ch.aplu.jcardgame.Hand;

import java.util.Optional;

public class Human extends Player {

    public Human(RuleChecker rules) {
        super(rules);
    }

    public void makeMove(Pile tablePile,boolean isCharacter){
       do{
           pickACorrectSuit(isCharacter);
           if(!this.getSelected().isPresent()){
               this.setSelected(Optional.empty());
               return;
           }
           selectPile(tablePile);
       }while(!this.isLegalMove(tablePile));

    }


    public boolean isLegalMove( Pile tablePile){return false;};
    public void  pickACorrectSuit(boolean isCharacter){
        if (this.getHand().isEmpty()) {
            this.setSelected(Optional.empty());
        } else {
            this.setSelected(null);
            this.getHand().setTouchEnabled(true);
            do {
                if (this.getSelected() == null) {
                    GameOfThrones.delay(100);
                    continue;
                }
                GoTCards.Suit suit = this.getSelected().isPresent() ? (GoTCards.Suit) this.getSelected().get().getSuit() : null;
                if (isCharacter && suit != null && suit.isCharacter() ||         // If we want character, can't pass and suit must be right
                        !isCharacter && (suit == null || !suit.isCharacter())) { // If we don't want character, can pass or suit must not be character
                    // if (suit != null && suit.isCharacter() == isCharacter) {
                    break;
                } else {
                    this.setSelected(null);
                    this.getHand().setTouchEnabled(true);
                }
                GameOfThrones.delay(100);
            } while (true);
        }
    }

    public void selectPile(Pile tablePile){
        tablePile.waitForPileSelection();
    }

}
