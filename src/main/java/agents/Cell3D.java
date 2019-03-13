package agents;

import core.Node;
import geom.Point3D;
import graph.GeneGraph;
import graph.GeneLink;
import graph_diagram.GeneDiagram;
import graphs.CircleGraph;
import javafx.scene.shape.Sphere;
import core.Cell;
import javafx.scene.transform.Translate;

public class Cell3D extends Sphere{
	private Cell cell;
	private GeneDiagram geneDiagram;

	public Cell3D(Cell cell) {
		super(cell.getRadius());
		this.cell = cell;
		moveTo(cell.getLocation());
		updateGraph();
	}

	public void moveTo(Point3D target) {
		Translate translation = new Translate(target.getX(), target.getY(), target.getZ());
		getTransforms().clear();
		getTransforms().addAll(translation);
		setLocation(target);
	}

	private void updateGraph() {
		geneDiagram = new GeneDiagram(getCell().getGeneGraph(), 300.);
	}

	public GeneDiagram getGeneDiagram() {
		updateGraph();
		return geneDiagram;
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
