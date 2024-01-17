package jku.win.se.assignmentManager.backend.response;

public class TaskExistsResponse {
	
	private boolean taskExists;
	
	public TaskExistsResponse(boolean taskExists) {
		this.taskExists = taskExists;
	}

	public boolean isTaskExists() {
		return taskExists;
	}

	public void setTaskExists(boolean taskExists) {
		this.taskExists = taskExists;
	}

}
