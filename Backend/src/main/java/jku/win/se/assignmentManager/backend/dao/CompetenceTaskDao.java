package jku.win.se.assignmentManager.backend.dao;

import java.util.List;
import java.util.stream.Collectors;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import jku.win.se.assignmentManager.backend.dto.CompetenceTask;

public class CompetenceTaskDao implements DAO<CompetenceTask> {
	
	private Datastore ds;
	
	public CompetenceTaskDao(Datastore ds) {
		this.ds = ds;
	}

	@Override
	public void save(CompetenceTask t) {
		ds.save(t);
	}

	@Override
	public List<CompetenceTask> getAll() {
		return ds.find(CompetenceTask.class).stream().collect(Collectors.toList());
	}

	@Override
	public CompetenceTask get(String id) {
		return ds.find(CompetenceTask.class).filter(Filters.eq("_id",id)).first();
	}
	
	public CompetenceTask getByTaskId(String taskId) {
		return ds.find(CompetenceTask.class).filter(Filters.eq("task_id",taskId)).first();
	}

	@Override
	public void delete(CompetenceTask t) {
		ds.delete(t);
	}

}
