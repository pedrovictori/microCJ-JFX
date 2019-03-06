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

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cmcastil
 */
public class Main extends Application {

    final Group cellsRoot = new Group();
    final Xform axisGroup = new Xform();
    final Xform world = new Xform();
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
    private List<Xform> cellShapes = new ArrayList<>();
    private List<Point3D> cellCenters = new ArrayList<>();

    //   private void buildScene() {
    //       cellsRoot.getChildren().add(world);
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
        world.getChildren().addAll(axisGroup);
    }

    private void handleMouse(Node scene, final Node root) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
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
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED);
                }
                else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX*MOUSE_SPEED*modifier;
                    camera.setTranslateZ(newZ);
                }
                else if (me.isMiddleButtonDown()) {
                    cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);
                    cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);
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

    private void generateCells() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(Color.WHITE);
        whiteMaterial.setSpecularColor(Color.LIGHTBLUE);

        final PhongMaterial greyMaterial = new PhongMaterial();
        greyMaterial.setDiffuseColor(Color.DARKGREY);
        greyMaterial.setSpecularColor(Color.GREY);

        Random rand = new Random();

        for (; cellShapes.size() < cells;) { //the loop runs until all cells are created
            if (cellShapes.size() == 0) {//if no cells, create first cell
                Sphere cell = new Sphere(cellRad);
                cell.setMaterial(greyMaterial);
                Xform cellx = new Xform();
                cellx.getChildren().add(cell);
                world.getChildren().add(cellx);
                cellShapes.add(cellx);
                cellCenters.add(Point3D.ZERO);
            }

            else {

                Xform cellx = cellShapes.get(cellShapes.size() - 1);
                Point3D newCentre = centreForProliferation(cellx.getChildren().get(0));
                cellCenters.add(newCentre);
                Sphere cell = new Sphere(cellRad);
                cell.setMaterial(greyMaterial);
                Xform ncellx = new Xform();
                ncellx.getChildren().add(cell);
                world.getChildren().add(ncellx);
                cellShapes.add(ncellx);

                //move cell
                Translate translation = new Translate(newCentre.getX(), newCentre.getY(), newCentre.getZ());
                cell.getTransforms().clear();
                cell.getTransforms().addAll(translation);


            }
        }
    }

    private Point3D randomPointAtDistance(double distance, Point3D origin) { Random rand = new Random();
        Point3D randomPoint = new Point3D(rand.nextInt(100) - 50, rand.nextInt(100) - 50, rand.nextInt(100) - 50);
        double x1 = origin.getX();
        double y1 = origin.getY();
        double z1 = origin.getZ();
        double x2 = randomPoint.getX();
        double y2 = randomPoint.getY();
        double z2 = randomPoint.getZ();
        double d = Math.sqrt( //d = sqrt((x2 - x1)^2 + (y2 - y1)^2 + (z2 - z1)^2)
                Math.pow(x2 - x1, 2) +
                        Math.pow(y2 - y1, 2) +
                        Math.pow(z2 - z1, 2));

        double u = distance / d;
        double x3 = (1 - u) * x1 + u * x2;
        double y3 = (1 - u) * y1 + u * y2;
        double z3 = (1 - u) * z1 + u * z2;
        return new Point3D(x3, y3, z3);
    }

    private Point3D findEmptyCenter(double distance, Point3D origin) {

        Point3D ec = randomPointAtDistance(distance, origin);
        boolean empty = true;
        for (Point3D p: cellCenters) {
            if (p.distance(ec) < (cellRad * 2)) {
                empty = false;
            }
        }
        if (empty) {
            return ec;
        } else {
            return findEmptyCenter(distance+0.005, origin); //that number was determined via trial and error as the lowest one needed to avoid an stack overflow error
        }
    }

    /**
     * Returns the centre for a new cell Sphere that is touching the original Sphere at a random location
     * @param origin
     * @return the centre of the new cell
     */
    private Point3D centreForProliferation(Node origin) {
        Point3D originCenter = new Point3D(origin.getTranslateX(), origin.getTranslateY(), origin.getTranslateZ());
        return findEmptyCenter(cellRad*2, originCenter);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        cellsRoot.getChildren().add(world);
        cellsRoot.setDepthTest(DepthTest.ENABLE);

        // buildScene();
        buildCamera();
        buildAxes();
        generateCells();
        Parent genesRoot = FXMLLoader.load(getClass().getResource("/fxml/genes.fxml"));
        SubScene genesScene = new SubScene(genesRoot, 1024, 1024);
        SubScene cellsScene = new SubScene(cellsRoot, 1024, 1024, true, SceneAntialiasing.BALANCED);
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);

        HBox root = new HBox(cellsScene, separator, genesScene);

        Scene mainScene = new Scene(root, 2048, 1024, true);
        mainScene.setFill(Color.CORNSILK);

        handleKeyboard(mainScene, world);
        handleMouse(cellsScene, world);

        primaryStage.setTitle("microC2");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        cellsScene.setCamera(camera);
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

}
