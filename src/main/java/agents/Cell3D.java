package agents;
/**
 * @author Pedro Victori
 */
/*
Copyright 2019 Pedro Victori

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import core.Cell;
import geom.Point3D;
import graph_diagram.GeneDiagram;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

public class Cell3D extends Sphere{
	private Cell cell;
	private GeneDiagram geneDiagram;

	public Cell3D(Cell cell) {
		super(cell.getRadius());
		this.cell = cell;
		moveTo(cell.getLocation());
		updateGraph();
	}

	public void moveTo(Point3D target) {
		Translate translation = new Translate(target.getX(), target.getY(), target.getZ());
		getTransforms().clear();
		getTransforms().addAll(translation);
		setLocation(target);
	}

	private void updateGraph() {
		if (geneDiagram == null) {
			geneDiagram = new GeneDiagram(getCell().getGeneGraph(), 300.);
		}
		geneDiagram.updateActivationStatus();
	}

	public GeneDiagram getGeneDiagram() {
		updateGraph();
		return geneDiagram;
	}

	public Point3D getLocation() {
		return getCell().getLocation();
	}

	public void setLocation(Point3D location) {
		getCell().setLocation(location);
	}

	public Cell getCell() {
		return cell;
	}
}
