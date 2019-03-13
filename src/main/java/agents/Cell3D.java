package agents;

import core.Node;
import geom.Point3D;
import graph.GeneLink;
import graphs.CircleGraph;
import javafx.scene.shape.Sphere;
import core.Cell;
import javafx.scene.transform.Translate;

public class Cell3D extends Sphere{
	private Cell cell;
	private CircleGraph<Node, GeneLink> graphDiagram;

	public Cell3D(double radius, Point3D location) {
		super(radius);
		cell = new Cell(location, radius);
		moveTo(location);
	}

	public Cell3D(Point3D location) {
		super(Cell.getDefaultSize());
		cell = new Cell(location);
		moveTo(location);
		updateGraph();
	}

	public void moveTo(Point3D target) {
		Translate translation = new Translate(target.getX(), target.getY(), target.getZ());
		getTransforms().clear();
		getTransforms().addAll(translation);
		setLocation(target);
	}

	private void updateGraph() {
		graphDiagram = new CircleGraph<>(getCell().getGeneGraph().getGraph(), 400.);
	}

	public CircleGraph<Node, GeneLink> getGraphDiagram() {
		updateGraph();
		return graphDiagram;
	}

	public Point3D getLocation() {
		return getCell().getLocation();
	}

	public void setLocation(Point3D location) {
		getCell().setLocation(location);
	}

	public Cell getCell() {
		return cell;
	}
}
