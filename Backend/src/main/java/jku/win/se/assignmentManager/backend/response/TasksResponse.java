package jku.win.se.assignmentManager.backend.response;

import java.util.List;

import jku.win.se.assignmentManager.backend.dto.Task;

public class TasksResponse {

	private List<Task> tasks;
	
	public TasksResponse(List<Task> tasks) {
		this.tasks = tasks;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
}
