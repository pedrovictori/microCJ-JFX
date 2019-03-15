import core.Node;
import graph.GeneLink;
import graphs.CircleGraph;
import graph.GeneGraph;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GraphPaneTest extends Application {

        @Override
        public void start(Stage primaryStage) {
            GeneGraph graph = new GeneGraph(1);
            CircleGraph circleGraph = new CircleGraph<>(graph.getGraph(), 400.);
            Pane pane = new StackPane(circleGraph);
            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
