package jku.win.se.assignmentManager.backend.response;

import java.util.List;

import jku.win.se.assignmentManager.backend.dto.Notebook;

public class NotebooksResponse {
	
	private List<Notebook> notebooks;
	
	public NotebooksResponse(List<Notebook> notebooks) {
		this.notebooks = notebooks;
	}

	public List<Notebook> getNotebooks() {
		return notebooks;
	}

	public void setNotebooks(List<Notebook> notebooks) {
		this.notebooks = notebooks;
	}
	
}
