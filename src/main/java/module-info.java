module microC2.JFX {
	requires javafx.controls;
	requires javafx.fxml;

	opens gui to javafx.fxml;
	exports gui;
}