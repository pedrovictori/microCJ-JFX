module microCJ.JFX {
	requires javafx.controls;
	requires javafx.fxml;
	requires GraphFX;
	requires microCJ.core;
	opens gui to javafx.fxml;
	exports gui;
}