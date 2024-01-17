package jku.win.se.assignmentManager.backend.dao;

import java.util.List;
import java.util.stream.Collectors;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import jku.win.se.assignmentManager.backend.dto.TaskFile;

public class FileDao implements DAO<TaskFile>{
	
	private Datastore ds;
	
	public FileDao(Datastore ds) {
		this.ds = ds;
	}

	@Override
	public void save(TaskFile t) {
		ds.save(t);
	}
	
	public String saveWithId(TaskFile t) {
		return ds.save(t).getId();
	}

	@Override
	public List<TaskFile> getAll() {
		return ds.find(TaskFile.class).stream().collect(Collectors.toList());
	}

	@Override
	public TaskFile get(String id) {
		return ds.find(TaskFile.class).filter(Filters.eq("_id",id)).first();
	}

	@Override
	public void delete(TaskFile t) {
		ds.delete(t);
	}

}
