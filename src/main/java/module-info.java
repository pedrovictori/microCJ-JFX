module microC2.JFX {
	requires javafx.controls;
	requires javafx.fxml;
	requires GraphFX;
	requires microC2.core;
	opens gui to javafx.fxml;
	exports gui;
}