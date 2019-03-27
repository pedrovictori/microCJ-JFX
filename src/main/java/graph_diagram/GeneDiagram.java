package graph_diagram;

import graph.FateNode;
import graph.Gene;
import graph.Input;
import graph.Node;
import graph.GeneGraph;
import graph.GeneLink;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;

import java.util.*;


public class GeneDiagram extends Parent {
	private static final Paint inactive = Color.FIREBRICK;
	private static final Paint active = Color.LIMEGREEN;
	Map<Node, Node2D> elements = new HashMap<>();
	private List<GeneLink> links;
	private Double radius;

	public GeneDiagram(GeneGraph graph, Double radius) {
		this.links = new ArrayList<>(graph.getEdges());
		this.radius = radius;
		loadGraph(graph.getNodes(), radius);
	}

	public void updateActivationStatus() {
		for (Node2D element : elements.values()) {
			element.getEllipse().setFill(element.getNode().isActive() ? active : inactive); //color accordingly to activation status
		}
	}

	private void loadGraph(Collection<Node> nodes, double radius) {
		//populate separate list for input, genes and fate
		List<Node> genes = new ArrayList<>();
		List<Node> inputs = new ArrayList<>();
		List<Node> fates = new ArrayList<>();
		for (Node node : nodes) {
			if (node instanceof Gene) genes.add(node);
			else if (node instanceof FateNode) fates.add(node);
			else if (node instanceof Input) inputs.add(node);
		}
		Collections.reverse(inputs); //todo correct dirty fix for making linked nodes closer between them
		double thg = (2 * Math.PI) / genes.size(); //angle between genes, in radians
		double thi = Math.PI / inputs.size(); //angle between inputs, in radians
		double thf = Math.PI / fates.size(); //angle between fates, in radians
		double innerRadius =  radius * 0.80; //for genes

		for (int j = 0; j < genes.size(); j++) {
			addNode(thg, j,  genes.get(j), innerRadius);
		}

		for (int j = 0; j < fates.size(); j++) {
			addNode(thf, j, fates.get(j), radius);
		}

		for (int j = 0; j < inputs.size(); j++) {
			addNode(thi, -j - inputs.size()/3, inputs.get(j), radius);
		}

		for (GeneLink link : links) {
			Node s = link.getSource();
			Node t = link.getTarget();
			Arrow a = new Arrow(elements.get(s).getCoords(), elements.get(t).getCoords());
			if(link.isPositive()) a.setFill(Color.DARKGREEN);
			else a.setFill(Color.DARKRED);
			getChildren().add(a);
		}

		//in a separate for so text is on top of all other nodes
		for (Node2D element : elements.values()) {
			getChildren().addAll(element.getTag());
		}
	}

	private void addNode(double angle, int i, Node node, double radius) {
		int h = i + 1;
		Double xCenter = 0.;
		Double yCenter = 0.;
		double x = radius * Math.cos(angle*h) + xCenter;
		double y = radius * Math.sin(angle*h) + yCenter;
		Coord coord = new Coord(x,y);
		Ellipse ellipse = new Ellipse(x, y,radius*0.0375,radius/40);
		Paint color = node.isActive() ? active : inactive;
		ellipse.setFill(color);
		getChildren().addAll(ellipse);

		//text will be added later to children nodes
		final Text text = new Text(coord.getX() + 7, coord.getY() - 10, node.toString());
		text.setFill(Color.BLUE);

		elements.put(node, new Node2D(node, ellipse, text, coord));
	}
}

