package jku.win.se.assignmentManager.backend.service;

import java.util.ArrayList;
import java.util.List;

import jku.win.se.assignmentManager.backend.dao.TaskDao;
import jku.win.se.assignmentManager.backend.dto.Competence;
import jku.win.se.assignmentManager.backend.dto.CompetenceTask;
import jku.win.se.assignmentManager.backend.dto.Task;
import jku.win.se.assignmentManager.backend.dto.WeightedCompetence;
import jku.win.se.assignmentManager.backend.dto.WeightedTask;
import jku.win.se.assignmentManager.backend.enumeration.CompetenceType;

public class CompetenceService {
	
	public static boolean addWeightedTasksToCompetence(Competence c, List<CompetenceTask> competenceTasks, TaskDao taskDao) {
		List<CompetenceTask> compTasksForComp = getCompTasksByComp(c, competenceTasks);
		if(compTasksForComp.size() > 0) {
			for(CompetenceTask ct : compTasksForComp) {
				Task task = taskDao.getByTaskId(ct.getTask_id());
				if(task != null) {
					c.getWeightedTasks().addAll(getWeightedTaskForCompetenceTask(ct, task, c.getCompetenceId()));
				}
			}
		}
		if(!c.getChildren().isEmpty()) {
			for(Competence child : c.getChildren()) {
				if(addWeightedTasksToCompetence(child, competenceTasks, taskDao)) {
					c.setHasTasks(true);
				}
			}
		}
		if(!c.isHasTasks()) {
			c.setHasTasks(!c.getWeightedTasks().isEmpty());
		}
		return c.isHasTasks();
	}
	
	public static List<WeightedTask> getWeightedTaskForCompetenceTask(CompetenceTask ct, Task t, String competenceId){
		List<WeightedTask> wts = new ArrayList<>();
		if(ct.getComp_prim() != null) {
			for(WeightedCompetence wc : ct.getComp_prim()) {
				if(wc.getComp_id().equals(competenceId)) {
					wts.add(new WeightedTask(t, wc.getWeight(), CompetenceType.PRIMARY));
				}
			}
		}
		if(ct.getComp_sec() != null) {
			for(WeightedCompetence wc : ct.getComp_sec()) {
				if(wc.getComp_id().equals(competenceId)) {
					wts.add(new WeightedTask(t, wc.getWeight(), CompetenceType.SECONDARY));
				}
			}
		}
		return wts;
	}
	
	public static boolean getMatchingCompetences(Competence c, String searchTerm) {
		if(c.getDescription().get("de").toLowerCase().contains(searchTerm)
				|| c.getDescription().get("en").toLowerCase().contains(searchTerm)) {
			return true;
		} else if(!c.getChildren().isEmpty()) {
			boolean childMatching = false;
			for(Competence child : c.getChildren()) {
				if(getMatchingCompetences(child, searchTerm)) {
					childMatching = true;
				}
			}
			return childMatching;
		}
		return false;
	}
	
	public static Competence getCompById(String id, List<Competence> comps) {
		for(Competence c : comps) {
			if(c.getCompetenceId().equals(id)) {
				return c;
			}
		}
		return null;
	}
	
	public static List<CompetenceTask> getCompTasksByComp(Competence c, List<CompetenceTask> competenceTasks) {
		List<CompetenceTask> compTasks = new ArrayList<>();
		for(CompetenceTask ct : competenceTasks) {
			if(ct.getComp_prim() != null) {
				for(WeightedCompetence wc : ct.getComp_prim()) {
					if(wc.getComp_id().equals(c.getCompetenceId())) {
						compTasks.add(ct);
					}
				}
			}
			if(ct.getComp_sec() != null) {
				for(WeightedCompetence wc : ct.getComp_sec()) {
					if(wc.getComp_id().equals(c.getCompetenceId())) {
						compTasks.add(ct);
					}
				}
			}
		}
		return compTasks;
	}

}
