package edu.xwei12.chess.gui;

import edu.xwei12.chess.*;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.util.HashMap;
import java.util.Set;


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
    /* Primary stage */
    private Stage primaryStage;
    /* Player name property */
    private StringProperty playerProperty;
    /* Player A score property */
    private IntegerProperty scoreAProperty;
    /* Player B score property */
    private IntegerProperty scoreBProperty;

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

        addCustomPieces();

        Set<RectanglePosition> pieces = board.getAllPieces();
        pieceImageMap = new HashMap<>(pieces.size());

        // Load all piece images
        pieces.forEach(p -> {
            Piece piece = board.getPiece(p);
            pieceImageMap.put(p, getImageForPiece(piece));
        });
    }

    /**
     * Start JavaFX application
     * @param primaryStage stage
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Pane root = new Pane();
        final int WINDOW_WIDTH = 700, WINDOW_HEIGHT = 500;
        final int BOARD_WIDTH = 500, BOARD_HEIGHT = 500;

        // Initialize window
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.setTitle("Chess");
        primaryStage.setResizable(false);

        // Initialize properties
        playerProperty = new SimpleStringProperty("Any");
        scoreAProperty = new SimpleIntegerProperty();
        scoreBProperty = new SimpleIntegerProperty();
        // Update scores
        ScoreManager scoreManager = game.getScoreManager();
        scoreAProperty.setValue(scoreManager.getScore(StandardGame.PLAYER_A));
        scoreBProperty.setValue(scoreManager.getScore(StandardGame.PLAYER_B));

        // Set background color
        root.setStyle("fx-background-color: #f2f2f2");

        /* Add labels */
        // Turn label
        Label turnLabel = new Label("Turn:") {{
            setFont(Font.font(20));
            setLayoutX(540);
            setLayoutY(100);
        }};
        root.getChildren().add(turnLabel);
        // Player label
        Label playerLabel = new Label() {{
            setLayoutX(540);
            setLayoutY(120);
            textProperty().bind(playerProperty);
            setFont(Font.font(40));
        }};
        root.getChildren().add(playerLabel);
        // Score A label
        Label scoreALabel = new Label() {{
            setFont(Font.font(35));
            setLayoutX(540);
            setLayoutY(200);
            textProperty().bindBidirectional(scoreAProperty, new NumberStringConverter());
        }};
        root.getChildren().add(scoreALabel);
        // Score B label
        Label scoreBLabel = new Label() {{
            setFont(Font.font(35));
            setLayoutX(540);
            setLayoutY(260);
            textProperty().bindBidirectional(scoreBProperty, new NumberStringConverter());
        }};
        root.getChildren().add(scoreBLabel);

        /* Add buttons */
        // Restart button
        Button restartButton = new Button("Restart Game") {{
            setLayoutX(550);
            setLayoutY(400);
            addEventHandler(MouseEvent.MOUSE_CLICKED, x -> restart());
        }};
        root.getChildren().add(restartButton);
        // Undo button
        Button undoButton = new Button("Undo") {{
            setLayoutX(550);
            setLayoutY(360);
            addEventHandler(MouseEvent.MOUSE_CLICKED, x -> undo());
        }};
        root.getChildren().add(undoButton);

        // Board image view
        boardView = new ImageView(new Image(getClass().getResourceAsStream("/board/rectangle-board.png"))) {{
            setFitWidth(BOARD_WIDTH);
            setFitHeight(BOARD_HEIGHT);
            setCache(true);
            setSmooth(true);
            setOpacity(0.1);
            setId("board");
            addEventFilter(MouseEvent.MOUSE_CLICKED, x -> onBoardClicked(x));
        }};
        root.getChildren().add(boardView);

        initializePieces();

        // Show stage
        primaryStage.show();
    }

    /**
     * Add custom pieces to the board
     */
    void addCustomPieces() {
        board.addPiece(ExtendedPiece.GRASSHOPPER.newPieceWithTag(StandardGame.PLAYER_A), new RectanglePosition(2, 2));
        board.addPiece(ExtendedPiece.GRASSHOPPER.newPieceWithTag(StandardGame.PLAYER_B), new RectanglePosition(5, 2));
        board.addPiece(ExtendedPiece.BEROLINA.newPieceWithTag(StandardGame.PLAYER_A), new RectanglePosition(2, 5));
        board.addPiece(ExtendedPiece.BEROLINA.newPieceWithTag(StandardGame.PLAYER_B), new RectanglePosition(5, 5));
    }

    /**
     * Initialize piece positions
     */
    void initializePieces() {
        // Add each piece to the scene
        pieceImageMap.forEach(this::addPiece);
    }

    /**
     * Add piece
     */
    void addPiece(RectanglePosition pos, Image img) {
        Pane root = (Pane)primaryStage.getScene().getRoot();
        Point2D coords = getGraphicalCoordinates(pos);
        ImageView view = new ImageView(img) {{
            setFitWidth(boardView.getFitWidth() / board.getFiles());
            setFitHeight(boardView.getFitHeight() / board.getRanks());
            setX(coords.getX());
            setY(coords.getY());
            // let onPieceClicked handle event
            addEventFilter(MouseEvent.MOUSE_CLICKED, x -> onPieceClicked(x));
            // set ID for each image view
            setId(generatePieceID(pos));
        }};
        root.getChildren().add(view);
    }

    /**
     * Load a image for piece
     * @param piece piece instance
     * @return image
     */
    private Image getImageForPiece(Piece piece) {
        String playerSuffix = piece.getTag() == StandardGame.PLAYER_A ? "-a.png" : "-b.png";
        String path = "/pieces/" + piece.getKind() + playerSuffix;
        return new Image(getClass().getResourceAsStream(path));
    }

    /**
     * Restart game
     */
    void restart() {
        Pane root = (Pane)primaryStage.getScene().getRoot();
        game.restart();
        // Remove all piece nodes
        root.getChildren().removeIf(x -> x.getId() != null && x.getId().startsWith("piece"));
        // Re-add custom pieces
        addCustomPieces();
        // Initialize pieces
        initializePieces();
        // Reset player
        playerProperty.setValue("Any");
        // Reset state
        state = State.STANDBY;
    }

    /**
     * Undo
     */
    void undo() {
        StandardGame.Move move = game.getLastMove();
        if (move == null) return;
        game.undo();

        // Restore position
        Pane root = (Pane) primaryStage.getScene().getRoot();
        ImageView pieceView = getPieceView(move.destination);
        Point2D origPos = getGraphicalCoordinates(move.source);
        pieceView.setX(origPos.getX());
        pieceView.setY(origPos.getY());

        // Reset position identifier
        pieceView.setId(generatePieceID(move.source));

        // Reset player turn
        playerProperty.setValue(game.getPlayerTurn() == StandardGame.PLAYER_A ? "Black" : "White");

        // Update scores
        ScoreManager scoreManager = game.getScoreManager();
        scoreAProperty.setValue(scoreManager.getScore(StandardGame.PLAYER_A));
        scoreBProperty.setValue(scoreManager.getScore(StandardGame.PLAYER_B));

        // Re-add piece if attacked
        if (move.attacks)
            addPiece(move.destination, getImageForPiece(move.victim));

        // Recover state
        state = State.STANDBY;
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

        attemptMoveUnderGuide(position);
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
                attemptMoveUnderGuide(position);
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

    /**
     * Attemp a move under guide state
     * @param toPosition destination
     */
    private void attemptMoveUnderGuide(RectanglePosition toPosition) {
        hideGuide();
        if (!inducerPosition.sameAs(toPosition)) {
            // If movable, move it
            boolean moved = game.stepWithMove(board.getPiece(inducerPosition).getTag(), inducerPosition, toPosition);
        }
        state = State.STANDBY;
    }


    /**
     * Show guide for piece
     * @param pos board position of piece
     *
     */
    public void showGuide(RectanglePosition pos) {
        ImageView pieceView = getPieceView(pos);
        pieceView.setOpacity(0.5);
    }

    /**
     * Hide guide
     */
    public void hideGuide() {
        if (inducerPosition == null) return;
        ImageView pieceView = getPieceView(inducerPosition);
        pieceView.setOpacity(1.0);
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

        // Remove killed piece view (if any)
        if (move.attacks) {
            Pane root = (Pane)primaryStage.getScene().getRoot();
            root.getChildren().remove(getPieceView(destination));
        }
        // Move piece view
        pieceImageView.setX(graphicalDestination.getX());
        pieceImageView.setY(graphicalDestination.getY());
        // Update ID of view to new position
        pieceImageView.setId(generatePieceID(destination));

        // Update player label
        playerProperty.setValue(game.getPlayerTurn() == StandardGame.PLAYER_A ? "Black" : "White");

        // Restore state
        state = State.STANDBY;

        // Check game end state
        if (game.getState() == Game.State.CHECKMATE) {
            state = State.HALTED;

            // Set player label
            playerProperty.setValue("Checkmate");

            // Update scores
            ScoreManager scoreManager = game.getScoreManager();
            scoreAProperty.setValue(scoreManager.getScore(StandardGame.PLAYER_A));
            scoreBProperty.setValue(scoreManager.getScore(StandardGame.PLAYER_B));

            // Get winner
            RectanglePosition defeaterPosition = game.getDefeaterPosition();
            Piece defeater = board.getPiece(defeaterPosition);

            // Debug output
            System.out.println(String.format("Checkmate by player %s's %s at (%d, %d).",
                    defeater.getTag() == StandardGame.PLAYER_A ? "A" : "B",
                    defeater.getKind(),
                    defeaterPosition.rank, defeaterPosition.file));
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
        Pane root = (Pane)primaryStage.getScene().getRoot();
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
