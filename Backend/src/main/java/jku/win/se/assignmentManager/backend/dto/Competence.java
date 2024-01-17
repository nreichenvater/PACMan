package jku.win.se.assignmentManager.backend.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.morphia.annotations.Entity;

@Entity
public class Competence extends AbstractEntity {
	
	private String competenceId;
	private boolean is_parent;
	private Map<String,String> description;
	private Dependency[] dependencies;
	private List<Competence> children;
	private List<WeightedTask> weightedTasks; //for tree view in frontend
	private boolean hasTasks; // for tree view in frontend
	
	private Competence() {
		children = new ArrayList<>();
		weightedTasks = new ArrayList<>();
	}

	public boolean isHasTasks() {
		return hasTasks;
	}

	public void setHasTasks(boolean hasTasks) {
		this.hasTasks = hasTasks;
	}


	public List<WeightedTask> getWeightedTasks() {
		return weightedTasks;
	}

	public void setWeightedTasks(List<WeightedTask> weightedTasks) {
		this.weightedTasks = weightedTasks;
	}

	public List<Competence> getChildren() {
		return children;
	}

	public void setChildren(List<Competence> children) {
		this.children = children;
	}

	public String getCompetenceId() {
		return competenceId;
	}

	public void setCompetenceId(String competenceId) {
		this.competenceId = competenceId;
	}

	public boolean isParent() {
		return is_parent;
	}

	public void setParent(boolean isParent) {
		this.is_parent = isParent;
	}

	public Map<String, String> getDescription() {
		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;
	}

	public Dependency[] getDependencies() {
		return dependencies;
	}

	public void setDependencies(Dependency[] dependencies) {
		this.dependencies = dependencies;
	}
	
}
