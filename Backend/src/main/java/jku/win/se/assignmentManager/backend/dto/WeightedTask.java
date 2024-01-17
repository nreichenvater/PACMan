package jku.win.se.assignmentManager.backend.dto;

import jku.win.se.assignmentManager.backend.enumeration.CompetenceType;

public class WeightedTask {
	
	private Task task;
	private String weight;
	private CompetenceType competenceType;
	
	public WeightedTask(Task task, String weight, CompetenceType competenceType) {
		this.task = task;
		this.weight = weight;
		this.competenceType = competenceType;
	}
	
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}

	public CompetenceType getCompetenceType() {
		return competenceType;
	}

	public void setCompetenceType(CompetenceType competenceType) {
		this.competenceType = competenceType;
	}
	
}
