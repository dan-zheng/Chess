package edu.xwei12.chess.gui;

import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;

import java.util.HashSet;
import java.util.Set;

/**
 * Guide highlighting all possible target cells
 * @author Xinran Wei
 */
public class GuideLayer extends StackPane {

    public double getUnitWidth() {
        return unitWidth;
    }

    public void setUnitWidth(double unitWidth) {
        this.unitWidth = unitWidth;
    }

    public double getUnitHeight() {
        return unitHeight;
    }

    public void setUnitHeight(double unitHeight) {
        this.unitHeight = unitHeight;
    }

    public void setPositions(Set<Point2D> positions) {
        this.positions = positions;
    }

    /** Unit dimensions **/
    private double unitWidth, unitHeight;

    /** Position set **/
    private Set<Point2D> positions = new HashSet<>();

    /**
     * Initializer
     */
    public GuideLayer(double unitWidth, double unitHeight) {
        super();
    }

    /**
     * Show highlighting at positions
     * @param positions
     */
    public void show(Set<Point2D> positions) {

    }

    /**
     * Show highlighting at last shown positions
     */
    public void show() {

    }

    /**
     * Hide highlighting
     */
    public void hide() {

    }
}
