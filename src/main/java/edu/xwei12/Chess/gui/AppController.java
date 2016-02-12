package edu.xwei12.chess.gui;

import edu.xwei12.chess.*;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Set;


/**
 * GUI demo
 * @author Xinran Wei
 */
public class AppController extends Application implements GameObserver<RectangleBoard, RectanglePosition> {

    private StandardGame game;
    private RectangleBoard board;
    private ImageView boardView;
    private HashMap<RectanglePosition, Image> pieceImageMap;

    private Stage primaryStage;

    /**
     * Constructor: initializes a game with GUI
     */
    public AppController() {
        game = new StandardGame();
        game.setObserver(this);
        board = game.getBoard();

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
        root.getChildren().add(boardView);

        // Add each piece to the scene
        pieceImageMap.forEach((pos, img) -> {
            Point2D coords = getGraphicalCoordinates(pos);
            ImageView view = new ImageView(img);
            view.setFitWidth(boardView.getFitWidth() / board.getFiles());
            view.setFitHeight(boardView.getFitHeight() / board.getRanks());
            view.setX(coords.getX());
            view.setY(coords.getY());
            root.getChildren().add(view);
        });

        // Show stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Game state delegate. To update the view after a move.
     * @param game game instance
     * @param move move
     */
    @Override
    public void onChessGameStateUpdate(Game game, Game.Move move) {
        // TODO: Update view
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
     * Capture snapshot
     * @return current scene as an image
     */
    public Image snapshot() {
        WritableImage wi = new WritableImage((int)primaryStage.getWidth(), (int)primaryStage.getHeight());
        WritableImage snapshot = primaryStage.getScene().snapshot(null);
        return snapshot;
    }
}
