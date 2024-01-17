package jku.win.se.assignmentManager.backend.request;

import java.util.ArrayList;
import java.util.List;

import jku.win.se.assignmentManager.backend.dto.JsonCell;

public class TaskJsonRequest {

	private List<JsonCell> cells;
	
	public TaskJsonRequest() {
		cells = new ArrayList<>();
	}

	public List<JsonCell> getCells() {
		return cells;
	}

	public void setCells(List<JsonCell> cells) {
		this.cells = cells;
	}
	
}
