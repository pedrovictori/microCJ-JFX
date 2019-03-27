/*
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package gui;

import agents.Cell3D;
import core.Cell;
import core.World;
import geom.Point3D;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.*;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import update.Updatable;
import update.Update;
import update.UpdateFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cmcastil
 */
public class Main extends Application {

	final Group cellsRoot = new Group();
	final Xform axisGroup = new Xform();
	final Xform nodes = new Xform();
	final PerspectiveCamera camera = new PerspectiveCamera(true);
	final Xform cameraXform = new Xform();
	final Xform cameraXform2 = new Xform();
	final Xform cameraXform3 = new Xform();
	private static final double CAMERA_INITIAL_DISTANCE = -450;
	private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
	private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
	private static final double CAMERA_NEAR_CLIP = 0.1;
	private static final double CAMERA_FAR_CLIP = 10000.0;
	private static final double AXIS_LENGTH = 250.0;
	private static final double HYDROGEN_ANGLE = 104.5;
	private static final double CONTROL_MULTIPLIER = 0.1;
	private static final double SHIFT_MULTIPLIER = 10.0;
	private static final double MOUSE_SPEED = 0.1;
	private static final double ROTATION_SPEED = 2.0;
	private static final double TRACK_SPEED = 0.3;

	double mousePosX;
	double mousePosY;
	double mouseOldX;
	double mouseOldY;
	double mouseDeltaX;
	double mouseDeltaY;

	private int cells = 100;
	private double cellRad = 10;
	private List<Point3D> cellCenters = new ArrayList<>();
	private Pane graphPane;

	private Cell3D selectedCell;
	private Map<Integer, Xform> cellRefs = new HashMap<>();
	private Color[] palette = {Color.BLUE, Color.GREEN, Color.YELLOW, Color.YELLOWGREEN, Color.PURPLE, Color.BROWN, Color.ORANGE, Color.ORCHID}; //todo improve
	private Map<String, Material> materialPalette = new HashMap<>();

	//   private void buildScene() {
	//       cellsRoot.getChildren().add(nodes);
	//   }
	private void buildCamera() {
		System.out.println("buildCamera()");
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
		System.out.println("buildAxes()");
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
		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseOldX = me.getSceneX();
				mouseOldY = me.getSceneY();
			}
		});
		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
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
			}
		});
	}

	private void handleKeyboard(Scene scene, final Node root) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
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
				if (selectedCell != null)
					selectedCell.getGeneDiagram().updateActivationStatus(); //update currently shown gene graph
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

	@Override
	public void start(Stage primaryStage) {

		cellsRoot.getChildren().add(nodes);
		cellsRoot.setDepthTest(DepthTest.ENABLE);

		// buildScene();
		buildCamera();
		buildAxes();
		generateWorld();
		graphPane = new StackPane();
		SubScene genesScene = new SubScene(graphPane, 1024, 1024);
		SubScene cellsScene = new SubScene(cellsRoot, 1024, 1024, true, SceneAntialiasing.BALANCED);
		Separator separator = new Separator();
		separator.setOrientation(Orientation.VERTICAL);

		HBox root = new HBox(cellsScene, separator, genesScene);

		Scene mainScene = new Scene(root, 2048, 1024, true);
		mainScene.setFill(Color.CORNSILK);

		handleKeyboard(mainScene, nodes);
		handleMouse(cellsScene, nodes);

		primaryStage.setTitle("microC2");
		primaryStage.setScene(mainScene);
		primaryStage.show();

		cellsScene.setCamera(camera);

		World.INSTANCE.start();
	}

	/**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case the application can not be
	 * launched through deployment artifacts, e.g., in IDEs with limited FX
	 * support. NetBeans ignores main().
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	private void addEventToCell(Cell3D cell) {
		cell.setOnMouseClicked(event -> {
			selectedCell = cell;
			graphPane.getChildren().clear();
			graphPane.getChildren().add(cell.getGeneDiagram());
		});
	}

}
