package jku.win.se.assignmentManager.backend.dao;

import java.util.List;
import java.util.stream.Collectors;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import jku.win.se.assignmentManager.backend.dto.Notebook;

public class NotebookDao implements DAO<Notebook> {
	
	private Datastore ds;
	
	public NotebookDao(Datastore ds) {
		this.ds = ds;
	}

	@Override
	public void save(Notebook t) {
		ds.save(t);
	}

	@Override
	public List<Notebook> getAll() {
		return ds.find(Notebook.class).stream().collect(Collectors.toList());
	}

	@Override
	public Notebook get(String id) {
		return ds.find(Notebook.class).filter(Filters.eq("_id", id)).first();
	}

	@Override
	public void delete(Notebook t) {
		ds.delete(t);
	}

}
