package jku.win.se.assignmentManager.backend.request;

import java.util.List;

import jku.win.se.assignmentManager.backend.dto.CompetenceTask;

public class AssignmentMapping {

	private String assignment_id;
	private String competence_model;
	private List<CompetenceTask> tasks;
	private String _id;
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getAssignment_id() {
		return assignment_id;
	}
	public void setAssignment_id(String assignment_id) {
		this.assignment_id = assignment_id;
	}
	public String getCompetence_model() {
		return competence_model;
	}
	public void setCompetence_model(String competence_model) {
		this.competence_model = competence_model;
	}
	public List<CompetenceTask> getTasks() {
		return tasks;
	}
	public void setTasks(List<CompetenceTask> tasks) {
		this.tasks = tasks;
	}
	
	
}
