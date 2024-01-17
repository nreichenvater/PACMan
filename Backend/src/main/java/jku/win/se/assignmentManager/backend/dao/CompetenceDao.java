package jku.win.se.assignmentManager.backend.dao;

import java.util.List;
import java.util.stream.Collectors;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import jku.win.se.assignmentManager.backend.dto.Competence;

public class CompetenceDao implements DAO<Competence> {
	
	private Datastore ds;
	
	public CompetenceDao(Datastore ds) {
		this.ds = ds;
	}

	@Override
	public void save(Competence t) {
		ds.save(t);
	}
	
	public void upsert(Competence t) {
		Competence existingCompetence = getByCompetenceId(t.getCompetenceId());
		if(existingCompetence != null) {
			existingCompetence.setDependencies(t.getDependencies());
			existingCompetence.setDescription(t.getDescription());
			existingCompetence.setParent(t.isParent());
			ds.save(existingCompetence);
		} else {
			ds.save(t);
		}
	}

	@Override
	public List<Competence> getAll() {
		return ds.find(Competence.class).stream().collect(Collectors.toList());
	}

	@Override
	public Competence get(String id) {
		return ds.find(Competence.class).filter(Filters.eq("_id", id)).first();
	}
	
	public Competence getByCompetenceId(String competenceId) {
		return ds.find(Competence.class).filter(Filters.eq("competenceId", competenceId)).first();
	}

	@Override
	public void delete(Competence t) {
		System.out.println("deleting competence " + t.getCompetenceId());
		ds.delete(t);
	}
	
}
