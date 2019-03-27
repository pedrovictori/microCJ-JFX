module microCJ.JFX {
	requires javafx.controls;
	requires javafx.fxml;
	requires microCJ.core;
	opens gui to javafx.fxml;
	exports gui;
}