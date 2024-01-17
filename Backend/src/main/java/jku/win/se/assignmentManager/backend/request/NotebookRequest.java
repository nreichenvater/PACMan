package jku.win.se.assignmentManager.backend.request;

import java.util.List;
import java.util.Map;

import jku.win.se.assignmentManager.backend.dto.Task;
import jku.win.se.assignmentManager.backend.util.StringUtils;

public class NotebookRequest {

	private String title;
	private Map<String,String> metadata;
	private List<Task> tasks;
	private String info;
	private boolean includeGradingTable;
	
	public String getInfo() {
		return info;
	}

	public boolean isIncludeGradingTable() {
		return includeGradingTable;
	}

	public void setIncludeGradingTable(boolean includeGradingTable) {
		this.includeGradingTable = includeGradingTable;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public NotebookRequest() {}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	public String validateBody() {
		if(StringUtils.isEmptyOrNull(title)) {
			return "Please enter a title for the notebook";
		}
		if(tasks.size() <= 0) {
			return "Please add at least one task to the notebook";
		}
		if(StringUtils.isEmptyOrNull(metadata.get("tutor"))) {
			return "Please add a tutor to the notebook";
		}
		return "";
	}
	
}
