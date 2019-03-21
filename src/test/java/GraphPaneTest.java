import graph.GeneGraph;
import graph_diagram.GeneDiagram;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

public class GraphPaneTest extends Application {

	@Override
	public void start(Stage primaryStage) {
		GeneGraph graph = GeneGraph.RandomlyActivatedGraph(1).turnNode("Oxygen_supply", true);
		GeneDiagram circleGraph = new GeneDiagram(graph, 400.);
		Pane pane = new StackPane(circleGraph);
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.show();

		Runnable helloRunnable = () -> {
			graph.update();
			circleGraph.updateActivationStatus();
		};

		ScheduledExecutorService executor = newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(helloRunnable, 0, 300, TimeUnit.MILLISECONDS);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
