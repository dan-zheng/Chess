package edu.xwei12.chess.gui;

import edu.xwei12.chess.*;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * GUI demo
 * @author Xinran Wei
 */
public class AppController extends Application implements GameObserver<RectangleBoard, RectanglePosition> {

    enum State {
        STANDBY, GUIDE, MOVING, HALTED
    }

    /** Chess model **/
    /* Game model */
    private StandardGame game;
    /* Board of the game (short reference) */
    private RectangleBoard board;

    /** View **/
    /* Table :: (Position, Image) */
    private HashMap<RectanglePosition, Image> pieceImageMap;
    /* Board image view */
    private ImageView boardView;
    /* Guide layer */
    private GuideLayer guideLayer;
    /* Primary stage */
    private Stage primaryStage;

    /** Interaction control **/
    /* Interaction state */
    State state;
    /* Inducer (piece) of the state */
    RectanglePosition inducerPosition;

    /**
     * Constructor: initializes a game with GUI
     */
    public AppController() {
        game = new StandardGame();
        game.setObserver(this);
        board = game.getBoard();
        state = State.STANDBY;
        inducerPosition = null;

        Set<RectanglePosition> pieces = board.getAllPieces();
        pieceImageMap = new HashMap<>(pieces.size());

        // Load all piece images
        pieces.forEach(p -> {
            Piece piece = board.getPiece(p);
            String playerSuffix = piece.getTag() == StandardGame.PLAYER_A ? "-a.png" : "-b.png";
            String path = "/pieces/" + piece.getKind() + playerSuffix;
            Image image = new Image(getClass().getResourceAsStream(path));
            pieceImageMap.put(p, image);
        });
    }

    /**
     * Start JavaFX application
     * @param primaryStage stage
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Group root = new Group();
        final int WINDOW_SIZE_Y = 500, WINDOW_SIZE_X = 500;

        // Initialize window
        primaryStage.setScene(new Scene(root, WINDOW_SIZE_X, WINDOW_SIZE_Y));
        primaryStage.setTitle("Chess");
        primaryStage.setResizable(false);

        // Board image view
        boardView = new ImageView(new Image(getClass().getResourceAsStream("/board/rectangle-board.png")));
        boardView.setFitWidth(WINDOW_SIZE_X);
        boardView.setFitHeight(WINDOW_SIZE_Y);
        boardView.setCache(true);
        boardView.setSmooth(true);
        boardView.setOpacity(0.1);
        boardView.setId("board");
        boardView.addEventFilter(MouseEvent.MOUSE_CLICKED, this::onBoardClicked);
        root.getChildren().add(boardView);

        // Add guide layer
        double unitWidth = boardView.getFitWidth() / board.getFiles();
        double unitHeight = boardView.getFitHeight() / board.getRanks();
        guideLayer = new GuideLayer(unitWidth, unitHeight);
        // TODO: decide whether or not to use Guide Layer!!!

        initializePieces();

        // Show stage
        primaryStage.show();
    }

    /**
     * Initialize piece positions
     */
    void initializePieces() {
        Group root = (Group)primaryStage.getScene().getRoot();

        // Add each piece to the scene
        pieceImageMap.forEach((pos, img) -> {
            Point2D coords = getGraphicalCoordinates(pos);
            ImageView view = new ImageView(img);
            view.setFitWidth(boardView.getFitWidth() / board.getFiles());
            view.setFitHeight(boardView.getFitHeight() / board.getRanks());
            view.setX(coords.getX());
            view.setY(coords.getY());

            // let onPieceClicked handle event
            view.addEventFilter(MouseEvent.MOUSE_CLICKED, this::onPieceClicked);

            // set ID for each image view
            view.setId(generatePieceID(pos));

            root.getChildren().add(view);
        });
    }

    /**
     * Restart game
     */
    void restart() {
        Group root = (Group)primaryStage.getScene().getRoot();
        // Remove all piece nodes
        root.getChildren().removeIf(x -> x.getId().startsWith("piece"));
        // Initialize pieces
        initializePieces();
    }

    /**
     * Mouse event handler that handles board clicks
     * @param event mouse event
     */
    private void onBoardClicked(MouseEvent event) {

        // Only accepts clicks on the board canvas
        if (state != State.GUIDE) return;

        Point2D clickPosition = new Point2D(event.getX(), event.getY());
        Point2D imagePosition = getCurrentCellCoordinates(clickPosition);
        RectanglePosition position = getBoardCoordinates(imagePosition);

        attemptMoveWithGuide(position);
    }

    /**
     * Mouse event handler that handles piece clicks
     * @param event mouse event
     */
    private void onPieceClicked(MouseEvent event) {
        ImageView target = (ImageView)event.getTarget();
        RectanglePosition position = getBoardCoordinates(new Point2D(target.getX(), target.getY()));
        switch (state) {
            // In guide mode, determine whether or not to move the piece
            case GUIDE:
                attemptMoveWithGuide(position);
                break;

            // In standby mode, show guide for the piece
            case STANDBY:
                if (game.getPlayerTurn().equals(board.getPiece(position).getTag()) ||
                        game.getPlayerTurn().equals(StandardGame.PLAYER_DEFAULT)) {
                    state = State.GUIDE;
                    inducerPosition = position;
                    showGuide(position);
                }
                break;

            default:
                break;
        }
    }

    private void attemptMoveWithGuide(RectanglePosition toPosition) {
        if (!inducerPosition.sameAs(toPosition)) {
            // If movable, move it
            boolean moved = game.stepWithMove(board.getPiece(inducerPosition).getTag(), inducerPosition, toPosition);
        }
        state = State.STANDBY;
        hideGuide();
    }


    /**
     * Show guide for piece
     * @param pos board position of piece
     *
     */
    public void showGuide(RectanglePosition pos) {
        // Show
        Set<Point2D> positions = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            positions.addAll( board.getPossibleMoves(pos, i).stream()
                    .map(this::getGraphicalCoordinates).collect(Collectors.toSet()));
        }
        guideLayer.show(positions);
    }

    /**
     * Hide guide
     */
    public void hideGuide() {
        guideLayer.hide();
    }

    /**
     * Game state delegate. To update the view after a move.
     * @param game game instance
     * @param move move
     */
    @Override
    public void onChessGameStateUpdate(Game<RectangleBoard, RectanglePosition> game,
                                       Game<RectangleBoard, RectanglePosition>.Move move) {

        // Set state to moving (for animation)
        state = State.MOVING;

        RectanglePosition source = move.source;
        RectanglePosition destination = move.destination;
        Point2D graphicalDestination = getGraphicalCoordinates(destination);

        // Debug output
        System.out.println(String.format("Player %s moved (%d, %d) to (%d, %d).",
                move.player == StandardGame.PLAYER_A ? "A" : "B",
                source.rank, source.file, destination.rank, destination.file));

        // Get piece image view by position as ID
        ImageView pieceImageView = getPieceView(source);

        // TODO: Animatedly move the piece
        // Remove killed piece view (if any)
        if (move.attacks) {
            Group root = (Group)primaryStage.getScene().getRoot();
            root.getChildren().remove(getPieceView(destination));
        }
        // Move piece view
        pieceImageView.setX(graphicalDestination.getX());
        pieceImageView.setY(graphicalDestination.getY());
        // Update ID of view to new position
        pieceImageView.setId(generatePieceID(destination));

        // Restore state
        state = State.STANDBY;

        // Check game end state
        if (game.getState() == Game.State.CHECKMATE) {
            state = State.HALTED;

            // Debug output
            System.out.println(String.format("Checkmate by player %s's %s at (%d, %d).",
                    move.player == StandardGame.PLAYER_A ? "A" : "B",
                    board.getPiece(destination).getKind(),
                    destination.rank, destination.file));

            // TODO: Popup
        }
    }

    /**
     * Translates rectangle board coordinates to graphical coordinates
     * @param pos board coordinates
     * @return graphical coordinates
     */
    private Point2D getGraphicalCoordinates(RectanglePosition pos)  {
        double unitWidth = boardView.getFitWidth() / board.getFiles();
        double unitHeight = boardView.getFitHeight() / board.getRanks();
        return new Point2D(unitWidth * pos.file, boardView.getFitHeight() - unitHeight * (pos.rank+1));
    }

    /**
     * Translates graphical coordinates to rectangle board coordinates
     * @param pos graphical coordinates
     * @return board coordinates
     */
    private RectanglePosition getBoardCoordinates(Point2D pos) {
        int unitWidth = (int) (boardView.getFitWidth() / board.getFiles());
        int unitHeight = (int) (boardView.getFitHeight() / board.getRanks());
        int rank = (int)(boardView.getFitHeight() - pos.getY()) / unitHeight - 1;
        int file = (int)pos.getX() / unitHeight;
        return new RectanglePosition(rank, file);
    }

    /**
     * Translates any graphical coordinates to the anchor coordinates of the current cell
     * @param pos any graphical coordinates on the board
     * @return anchor coordinates of the current cell
     */
    private Point2D getCurrentCellCoordinates(Point2D pos) {
        double unitWidth = (boardView.getFitWidth() / board.getFiles());
        double unitHeight = (boardView.getFitHeight() / board.getRanks());

        double x = unitWidth * ((int)pos.getX() / (int)unitWidth);
        double y = unitWidth * ((int)pos.getY() / (int)unitHeight);

        return new Point2D(x, y);
    }

    /**
     * Generate piece view ID from position
     * @param pos position
     * @return piece ID
     */
    private String generatePieceID(RectanglePosition pos) {
        String id = String.format("piece@%d,%d", pos.rank, pos.file);
        return id;
    }

    /**
     * Get piece view from
     * @param pos position
     * @return piece image view
     */
    private ImageView getPieceView(RectanglePosition pos) {
//        if (!board.pieceExists(pos)) return null;
        Group root = (Group)primaryStage.getScene().getRoot();
        Node node = root.lookup("#" + generatePieceID(pos));
        return (ImageView)node;
    }

    /**
     * Capture snapshot
     * @return current scene as an image
     */
    public Image snapshot() {
        WritableImage wi = new WritableImage((int)primaryStage.getWidth(), (int)primaryStage.getHeight());
        WritableImage snapshot = primaryStage.getScene().snapshot(null);
        return snapshot;
    }

    /**
     * Entry point of the application
     * @param args arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
