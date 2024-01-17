package jku.win.se.assignmentManager.backend.dto;

import java.util.List;

import dev.morphia.annotations.Entity;

@Entity
public class CompetenceTask extends AbstractEntity {
	private String task_id;
	private List<WeightedCompetence> comp_prim;
	private List<WeightedCompetence> comp_sec;
	
	public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	public List<WeightedCompetence> getComp_prim() {
		return comp_prim;
	}
	public void setComp_prim(List<WeightedCompetence> comp_prim) {
		this.comp_prim = comp_prim;
	}
	public List<WeightedCompetence> getComp_sec() {
		return comp_sec;
	}
	public void setComp_sec(List<WeightedCompetence> comp_sec) {
		this.comp_sec = comp_sec;
	}

}
