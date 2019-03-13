package graph_diagram;

import core.Fate;
import core.Gene;
import core.Input;
import core.Node;
import graph.GeneGraph;
import graph.GeneLink;
import graphs.Arrow;
import graphs.Coord;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;

import java.util.*;


public class GeneDiagram extends Parent {
	List<Node> nodes;
	List<GeneLink> links;
	private Double radius;
	private Map<Node, Coord> vPositions= new HashMap<>();

	public GeneDiagram(GeneGraph graph, Double radius) {
		this.nodes = new ArrayList<>(graph.getNodes());
		this.links = new ArrayList<>(graph.getEdges());
		this.radius = radius;
		loadGraph(nodes, radius);
	}

	private void loadGraph(List<Node> nodes, double radius) {
		//populate separate list for input, genes and fate
		List<Node> genes = new ArrayList<>();
		List<Node> inputs = new ArrayList<>();
		List<Node> fates = new ArrayList<>();
		for (Node node : nodes) {
			if (node instanceof Gene) genes.add(node);
			else if (node instanceof Fate) fates.add(node);
			else if (node instanceof Input) inputs.add(node);
		}
		Collections.reverse(inputs); //todo correct dirty fix for making linked nodes closer between them
		double thg = (2 * Math.PI) / genes.size(); //angle between genes, in radians
		double thi = Math.PI / inputs.size(); //angle between inputs, in radians
		double thf = Math.PI / fates.size(); //angle between fates, in radians
		double innerRadius =  radius * 0.80; //for genes

		for (int j = 0; j < genes.size(); j++) {
			addNode(Color.LIGHTBLUE, thg, j,  genes.get(j), innerRadius);
		}

		for (int j = 0; j < fates.size(); j++) {
			addNode(Color.DARKSALMON, thf, j, fates.get(j), radius);
		}

		for (int j = 0; j < inputs.size(); j++) {
			addNode(Color.DARKGOLDENROD, thi, -j - inputs.size()/3, inputs.get(j), radius);
		}

		for (GeneLink link : links) {
			Node s = link.getSource();
			Node t = link.getTarget();
			Arrow a = new Arrow(vPositions.get(s), vPositions.get(t));
			if(link.isPositive()) a.setFill(Color.DARKGREEN);
			else a.setFill(Color.DARKRED);
			getChildren().add(a);
		}

		//in a separate for so text is on top of all other nodes
		for (Node node : nodes) {
			Coord c = vPositions.get(node);
			final Text text = new Text(c.getX() + 7, c.getY() - 10, node.toString());
			text.setFill(Color.BLUE);
			getChildren().addAll(text);
		}
	}

	private void addNode(Paint color, double angle, int i, Node node, double radius) {
		i++;
		Double xCenter = 0.;
		Double yCenter = 0.;
		double x = radius * Math.cos(angle*i) + xCenter;
		double y = radius * Math.sin(angle*i) + yCenter;
		Coord coord = new Coord(x,y);
		vPositions.put(node, coord);
		Ellipse ellipse = new Ellipse(x, y,radius*0.0375,radius/40);
		ellipse.setFill(color);
		getChildren().addAll(ellipse);
	}
}

