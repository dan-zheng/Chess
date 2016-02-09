package edu.xwei12.Chess;

/**
 * Demo app
 *
 */
public class App implements GameObserver<RectangleBoard, RectanglePosition> {

    public static void main(String[] args) {
        System.out.println("Hello World!");

        App app = new App();
    }

    StandardGame game;

    App() {
        game = new StandardGame();
        game.setObserver(this);

        game.getBoard().print();

        Integer player1 = 1;
        Integer player2 = -1;

        game.stepWithMove(player1, 1, 4, 3, 4);
        game.stepWithMove(player1, 3, 4, 4, 4);
        game.stepWithMove(player1, 4, 4, 5, 4);
        game.stepWithMove(player1, 5, 4, 6, 4);
    }

    @Override
    public void onChessGameStateUpdate(Game<RectangleBoard, RectanglePosition> game, Game<RectangleBoard, RectanglePosition>.Move move) {
        game.getBoard().print();

        String playerName = move.player == 1 ? "Player 1" : "Player 2";
        System.out.println(playerName + " moved.");

        if (game.getState() == Game.State.CHECKMATE)
            System.out.println("Checkmate! " + playerName + " lost.");
    }
}