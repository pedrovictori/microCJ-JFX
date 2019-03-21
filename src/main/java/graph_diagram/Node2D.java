package graph_diagram;

import core.Node;
import javafx.scene.shape.Shape;

public class Node2D {
	private Node node;
	private Shape ellipse;
	private Shape tag;
	private Coord coords;

	public Node2D(Node node, Shape ellipse, Shape tag, Coord coords) {
		this.node = node;
		this.ellipse = ellipse;
		this.tag = tag;
		this.coords = coords;
	}

	public Node getNode() {
		return node;
	}

	public Shape getEllipse() {
		return ellipse;
	}

	public Shape getTag() {
		return tag;
	}

	public Coord getCoords() {
		return coords;
	}
}
