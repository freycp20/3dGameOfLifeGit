package controllers;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class ZoomableScrollPane extends ScrollPane {
    // preset scale size
    private double scaleValue = 0.9;
    // inner pane of zoomable node (content)
    private final Node target;
    // outer pane of zoomable node
    private final Node zoomNode;

    /**
     * adds given node to zoomableScrollPane
     * sets up group and content, as well as other various variables necessary for zooming in an out
     * @param target given node
     */
    public ZoomableScrollPane(Node target) {
        super();
        this.target = target;
        // adds to super.scrollPane-GROUP
        this.zoomNode = new Group(target);
        setContent(outerNode(zoomNode));
        // allows for mouse-hold panning
        setPannable(true);
        // scroll bars are lame
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setFitToHeight(true); //center
        setFitToWidth(true); //center
        // visualizes zooming
        updateScale();
    }

    /**
     * sets outerNode to centered node, sets up listener for zooming by scrolling
     * @param node given node
     */
    private Node outerNode(Node node) {
        Node outerNode = centeredNode(node);
        outerNode.setOnScroll(e -> {
            // keeps scrolling from going up an down, and sets new x,y to 2d point on given e.zoom
            e.consume();
            onScroll(e.getTextDeltaY(), new Point2D(e.getX(), e.getY()));
        });
        return outerNode;
    }

    /**
     * centers the node
     * @param node given node
     */
    private Node centeredNode(Node node) {
        // sets node in vbox, sets style, and aligns to the center of the vbox
        VBox vBox = new VBox(node);
        vBox.setStyle("-fx-background-color: #2c2c2c");
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    /**
     * sets node scale value to preset scale val
     * this just determines the starting scale of the node, and the current scale
     */
    private void updateScale() {
        target.setScaleX(scaleValue);
        target.setScaleY(scaleValue);
    }

    /**
     * sets scale of the node relative to the outerNode, according the scrolling
     * @param wheelDelta double value of current position
     * @param mousePoint 2d point of mouse location given by x,y double values
     */
    private void onScroll(double wheelDelta, Point2D mousePoint) {
        // zoomIntensity just determines the speed of zooming relative to scrolling
        double zoomIntensity = 0.02;
        double zoomFactor = Math.exp(wheelDelta * zoomIntensity);
        Bounds innerBounds = zoomNode.getLayoutBounds();
        Bounds viewportBounds = getViewportBounds();

        // calculate pixel offsets from [0, 1] range
        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        scaleValue = scaleValue * zoomFactor;
        updateScale();
        this.layout(); // refresh ScrollPane scroll positions & target bounds

        // convert target coordinates to zoomTarget coordinates
        Point2D posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(mousePoint));

        // calculate adjustment of scroll position in pixels
        Point2D adjustment = target.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        // convert back to [0, 1] range
        // (too large/small values are automatically corrected by ScrollPane)
        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        this.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
        this.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
    }
}