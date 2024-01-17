package jku.win.se.assignmentManager.backend.response;

public class NotebookResponse {
	
	private String json;
	private String title;
	
	public NotebookResponse(String title, String json) {
		this.json = json;
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
