package jku.win.se.assignmentManager.backend.request;

import java.util.List;
import java.util.Map;

import jku.win.se.assignmentManager.backend.dto.Cell;
import jku.win.se.assignmentManager.backend.dto.TaskFile;
import jku.win.se.assignmentManager.backend.util.StringUtils;

public class TaskRequest {
	
	private Map<String, String> metadata;
	private List<Cell> cells;
	private List<String> tags;
	private String title;
	private String note;
	private TaskFile file;
	
	public TaskRequest() {}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public List<Cell> getCells() {
		return cells;
	}

	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public TaskFile getFile() {
		return file;
	}

	public void setFile(TaskFile file) {
		this.file = file;
	}

	public String validateBody() {
		if(StringUtils.isEmptyOrNull(title)) {
			return "Bitte geben Sie einen korrekten Titel für die Aufgabe ein.";
		}
		/*if(StringUtils.isEmptyOrNull(taskId)) {
			return "Bitte geben Sie eine korrekte Task-ID ein.";
		} */
		//deletable, editable and language must be set
		if(cells.size() <= 0) {
			return "Bitte geben Sie zumindest ein Element für die Aufgabe an.";
		}
		return "";
	}

}
