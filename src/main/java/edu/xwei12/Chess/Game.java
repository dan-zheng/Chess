package edu.xwei12.chess;

import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Game base class
 * @author Xinran Wei
 * @param <B> Board
 * @param <C> Coordinate system
 */
public abstract class Game<B extends Board<B, C>, C extends Coordinates<C>> {



    public class Move {
        public Integer player;
        public C source;
        public C destination;
        public boolean attacks;

        public Move(Integer player, C source, C destination) {
            this.player = player;
            this.source = source;
            this.destination = destination;
        }
    }

    public Move getLastMove() {
        return moveHistory.peek();
    }

    public enum State {
        NORMAL, CHECKMATE
    }

    public State getState() {
        return state;
    }

    public GameObserver<B, C> getObserver() {
        return observer;
    }

    public void setObserver(GameObserver<B, C> observer) {
        this.observer = observer;
    }

    public B getBoard() {
        return board;
    }

    public Set<Integer> getPlayers() {
        return players;
    }

    public Integer getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(Integer playerTurn) {
        this.playerTurn = playerTurn;
    }

    protected B board;

    private State state = State.NORMAL;
    private GameObserver<B, C> observer = null;
    private Set<Integer> players;
    private String kingPieceKind;
    private Stack<Move> moveHistory = null;
    private Integer playerTurn = 0;


    /**
     * Initialize with a board
     * @param board chess board
     * @param kingPieceKind the kind of piece to determine checkmate
     * @param players set of player tags
     */
    public Game(B board, String kingPieceKind, Set<Integer> players) {
        this.board = board;
        this.kingPieceKind = kingPieceKind;
        this.players = players;
        moveHistory = new Stack<>();

        initialize();
    }

    /**
     * Step the game forward with a move
     * @param player player tag
     * @param fromPosition source
     * @param toPosition destination
     * @return moved
     */
    public boolean stepWithMove(Integer player, C fromPosition, C toPosition) {
        return stepWithMove(new Move(player, fromPosition, toPosition));
    }

    /**
     * Step the game forward with a move
     * @param move move
     * @return moved
     */
    public boolean stepWithMove(Move move) {
        if (state == State.CHECKMATE) return false;
        Piece p = board.getPiece(move.source);
        if (p == null || !p.getTag().equals(move.player)) return false;

        // Set attacks flag
        move.attacks = board.pieceExists(move.destination);

        boolean moved = board.movePiece(move.source, move.destination);
        if (moved) updateState(move);
        return moved;
    }

    /**
     * Update player turn
     */
    public abstract void updateTurn();

    /**
     * Initialize game
     */
    protected abstract void initialize();

    /**
     * Undo by 1 move
     * @return success
     */
    public boolean undo() {
        if (moveHistory.isEmpty()) return false;

        // Move back
        Move move = moveHistory.pop();
        Integer player = move.player;
        C lastSrc = move.source;
        C lastDest = move.destination;
        board.movePiece(lastDest, lastSrc);
        return true;
    }

    /**
     * Restart game
     */
    public void restart() {
        moveHistory.removeAllElements();
        board.removeAllPieces();
        initialize();
    }

    /**
     * Update game state when a move is made
     * @param move move
     */
    protected void updateState(Move move) {
        state = State.NORMAL;

        moveHistory.push(move);

        // Update player turn
        updateTurn();

        // Set of Kings
        Set<C> kingPositions = board.getPiecesByKind(kingPieceKind).stream()
                .collect(Collectors.toSet());

        // Find the piece that can attack the King!
        Optional<C> defeater = board.getAllPieces().stream()
                .filter(x -> kingPositions.stream()
                        .anyMatch(y -> board.canMovePiece(x, y)))
                .filter(x -> (moveHistory.isEmpty()) || !getLastMove().player.equals(board.getPiece(x).getTag()))
                .findFirst();

        // Found defeater!
        if (kingPositions.size() < 2 || defeater.isPresent()) {
            state = State.CHECKMATE;
        }

        // Notify observer
        if (observer != null) {
            observer.onChessGameStateUpdate(this, move);
        }
    }

}
