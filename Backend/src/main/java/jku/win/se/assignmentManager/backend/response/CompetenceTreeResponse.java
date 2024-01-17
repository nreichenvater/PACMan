package jku.win.se.assignmentManager.backend.response;

import java.util.List;

import jku.win.se.assignmentManager.backend.dto.Competence;

public class CompetenceTreeResponse {

	private List<Competence> nodes;
	
	public CompetenceTreeResponse(List<Competence> nodes) {
		this.nodes = nodes;
	}

	public List<Competence> getNodes() {
		return nodes;
	}

	public void setNodes(List<Competence> nodes) {
		this.nodes = nodes;
	}
	
}
