package jku.win.se.assignmentManager.backend.request;

import java.util.List;

import jku.win.se.assignmentManager.backend.dto.Competence;

public class CompetenceModelRequest {
	
	private String competencemodel_id;
	private List<Competence> competences;
	
	public String getCompetencemodel_id() {
		return competencemodel_id;
	}
	public void setCompetencemodel_id(String competencemodel_id) {
		this.competencemodel_id = competencemodel_id;
	}
	public List<Competence> getCompetences() {
		return competences;
	}
	public void setCompetences(List<Competence> competences) {
		this.competences = competences;
	}

}
