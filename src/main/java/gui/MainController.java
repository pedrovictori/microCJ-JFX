package gui;

import agents.Cell3D;
import core.Cell;
import core.World;
import geom.Point3D;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import update.Updatable;
import update.Update;
import update.UpdateFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {
	@FXML public Button bPause;
	@FXML public Label lTime;
	@FXML public HBox hbMain;
	private VBox vbRight;
	private Label lInfo = new Label();
	private final Group cellsRoot = new Group();
	private final Xform axisGroup = new Xform();
	private final Xform nodes = new Xform();
	final PerspectiveCamera camera = new PerspectiveCamera(true);
	private final Xform cameraXform = new Xform();
	private final Xform cameraXform2 = new Xform();
	private final Xform cameraXform3 = new Xform();
	private static final double CAMERA_INITIAL_DISTANCE = -450;
	private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
	private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
	private static final double CAMERA_NEAR_CLIP = 0.1;
	private static final double CAMERA_FAR_CLIP = 10000.0;
	private static final double AXIS_LENGTH = 250.0;
	private static final double CONTROL_MULTIPLIER = 0.1;
	private static final double SHIFT_MULTIPLIER = 10.0;
	private static final double MOUSE_SPEED = 0.1;
	private static final double ROTATION_SPEED = 2.0;
	private static final double TRACK_SPEED = 0.3;
	private double mousePosX;
	private double mousePosY;
	private double mouseOldX;
	private double mouseOldY;
	private double mouseDeltaX;
	private double mouseDeltaY;
	private int cells = 100;
	private double cellRad = 10;
	private List<Point3D> cellCenters = new ArrayList<>();
	private Pane graphPane;
	private Cell3D selectedCell;
	private Map<Integer, Xform> cellRefs = new HashMap<>();
	private Color[] palette = {Color.AQUA, Color.CORAL, Color.DARKSEAGREEN, Color.DARKORANGE, Color.BLUE, Color.GREEN, Color.YELLOW, Color.YELLOWGREEN, Color.PURPLE, Color.BROWN, Color.ORANGE, Color.ORCHID}; //todo improve
	private Map<String, Material> materialPalette = new HashMap<>();
	private Scene mainScene;
	private boolean paused = true;

	public void initialize() {
		cellsRoot.getChildren().add(nodes);
		cellsRoot.setDepthTest(DepthTest.ENABLE);
		buildCamera();
		buildAxes();
		generateWorld();
		graphPane = new StackPane();
		SubScene genesScene = new SubScene(graphPane, 900, 900);
		SubScene cellsScene = new SubScene(cellsRoot, 900, 900, true, SceneAntialiasing.BALANCED);
		Separator separator = new Separator();
		separator.setOrientation(Orientation.VERTICAL);
		Separator separator2 = new Separator();
		separator.setOrientation(Orientation.HORIZONTAL);
		vbRight = new VBox(genesScene, separator2, lInfo);
		hbMain.getChildren().addAll(cellsScene, separator, vbRight);
		handleMouse(cellsScene, nodes);
		cellsScene.setCamera(camera);
		bPause.setOnAction(event -> {
			paused = !paused;
			if(paused){
				World.INSTANCE.pause();
				bPause.setText("Start");
			}
			else{
				World.INSTANCE.start();
				bPause.setText("Pause");
			}
		});
	}

	void setScene(Scene scene) {
		mainScene = scene;
		handleKeyboard(mainScene, nodes);
	}

	private void buildCamera() {
		cellsRoot.getChildren().add(cameraXform);
		cameraXform.getChildren().add(cameraXform2);
		cameraXform2.getChildren().add(cameraXform3);
		cameraXform3.getChildren().add(camera);
		cameraXform3.setRotateZ(180.0);

		camera.setNearClip(CAMERA_NEAR_CLIP);
		camera.setFarClip(CAMERA_FAR_CLIP);
		camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
		cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
		cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
	}

	private void buildAxes() {
		final PhongMaterial redMaterial = new PhongMaterial();
		redMaterial.setDiffuseColor(Color.DARKRED);
		redMaterial.setSpecularColor(Color.RED);

		final PhongMaterial greenMaterial = new PhongMaterial();
		greenMaterial.setDiffuseColor(Color.DARKGREEN);
		greenMaterial.setSpecularColor(Color.GREEN);

		final PhongMaterial blueMaterial = new PhongMaterial();
		blueMaterial.setDiffuseColor(Color.DARKBLUE);
		blueMaterial.setSpecularColor(Color.BLUE);

		final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
		final Box yAxis = new Box(1, AXIS_LENGTH, 1);
		final Box zAxis = new Box(1, 1, AXIS_LENGTH);

		xAxis.setMaterial(redMaterial);
		yAxis.setMaterial(greenMaterial);
		zAxis.setMaterial(blueMaterial);

		axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
		axisGroup.setVisible(false);
		nodes.getChildren().addAll(axisGroup);
	}

	private void handleMouse(Node scene, final Node root) {
		scene.setOnMousePressed(me -> {
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
			mouseOldX = me.getSceneX();
			mouseOldY = me.getSceneY();
		});
		scene.setOnMouseDragged(me -> {
			mouseOldX = mousePosX;
			mouseOldY = mousePosY;
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
			mouseDeltaX = (mousePosX - mouseOldX);
			mouseDeltaY = (mousePosY - mouseOldY);

			double modifier = 1.0;

			if (me.isControlDown()) {
				modifier = CONTROL_MULTIPLIER;
			}
			if (me.isShiftDown()) {
				modifier = SHIFT_MULTIPLIER;
			}
			if (me.isPrimaryButtonDown()) {
				cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
				cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
			} else if (me.isSecondaryButtonDown()) {
				double z = camera.getTranslateZ();
				double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
				camera.setTranslateZ(newZ);
			} else if (me.isMiddleButtonDown()) {
				cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
				cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
			}
		});
	}

	private void handleKeyboard(Scene scene, final Node root) {
		scene.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case Z:
					cameraXform2.t.setX(0.0);
					cameraXform2.t.setY(0.0);
					camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
					cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
					cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
					break;
				case X:
					axisGroup.setVisible(!axisGroup.isVisible());
					break;
			}
		});
	}

	private void generateWorld() {
		final PhongMaterial redMaterial = new PhongMaterial();
		redMaterial.setDiffuseColor(Color.DARKRED);
		redMaterial.setSpecularColor(Color.RED);

		final PhongMaterial greyMaterial = new PhongMaterial();
		redMaterial.setDiffuseColor(Color.DARKGREY);
		redMaterial.setSpecularColor(Color.GREY);

		//generate palette with a color for each mutation group, and for cells with no mutation group
		materialPalette.put("no-group", greyMaterial);
		//todo maybe move this to a new class
		List<String> mutationGroupNames = World.INSTANCE.getTumor().getMutationGroupsNames();
		for (int i = 0; i < mutationGroupNames.size(); i++) {
			Color color = palette[i];
			final PhongMaterial material = new PhongMaterial();
			material.setDiffuseColor(color);
			material.setSpecularColor(color.brighter());
			materialPalette.put(mutationGroupNames.get(i), material);
		}

		for (Cell cell : World.INSTANCE.getTumor().getCellList()) {
			addNewCell(cell);
		}


		/*
		This block happens at every step of the simulation.
		* First, update the visualization of the currently selected cell's gene diagram
		* Second, get all updates from the update queue and execute them.
		 */
		World.INSTANCE.getPaceMaker().addListener(() -> {
			Platform.runLater(() -> {
				lTime.setText("Time point: " + World.INSTANCE.getPaceMaker().getStep());
				if (selectedCell != null) {
					selectedCell.getGeneDiagram().updateActivationStatus(); //update currently shown gene graph
					lInfo.setText(selectedCell.getCell().getInfo()); //update currently shown cell info
				}
				while (World.INSTANCE.getRemainingGuiUpdates() > 0) {
					Update<UpdateFlag, Updatable> update = World.INSTANCE.getUpdateFromGuiQueue(); //retrieve an update from the queue, in priority order
					switch (update.getFlag()) {
						case DEAD_CELL:
							nodes.getChildren().remove(cellRefs.get(((Cell) update.getUpdatable()).getId())); //finds the target cell in the map, and remove it from the 3D representation
							break;
						case NECROTIC_CELL:
							//finds the cell Xform, get the Cell3d from it (only element of the group and the XForm) and paint it red.
							((Cell3D) cellRefs.get(((Cell) update.getUpdatable()).getId()).getChildren().get(0)).setMaterial(redMaterial);
							break;
						case NEW_CELL:
							addNewCell((Cell) update.getUpdatable());
							break;
					}
				}
			});
		});
	}

	private void addNewCell(Cell cell) {
		Cell3D cell3d = new Cell3D(cell);

		//assign the corresponding color to each cell, and a special color for cells with no mutation group);
		cell3d.setMaterial(materialPalette.get(cell.getMutationGroupName().orElse("no-group")));
		Xform cellXform = new Xform();
		cellXform.getChildren().add(cell3d);
		nodes.getChildren().add(cellXform);
		addEventToCell(cell3d);
		cellRefs.put(cell.getId(), cellXform);
	}

	private void addEventToCell(Cell3D cell) {
		cell.setOnMouseClicked(event -> {
			selectedCell = cell;
			graphPane.getChildren().clear();
			graphPane.getChildren().add(cell.getGeneDiagram());
			lInfo.setText(cell.getCell().getInfo());
		});
	}
}
