package jku.win.se.assignmentManager.backend.response;

public class ErrorResponse {

	private String error;
	
	public ErrorResponse(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	@Override
	public String toString() {
		return "Error: " + error;
	}
}
