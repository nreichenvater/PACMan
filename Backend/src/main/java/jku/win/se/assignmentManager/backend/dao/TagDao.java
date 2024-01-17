package jku.win.se.assignmentManager.backend.dao;

import java.util.List;
import java.util.stream.Collectors;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import jku.win.se.assignmentManager.backend.dto.Tag;

public class TagDao implements DAO<Tag> {
	
	private Datastore ds;
	
	public TagDao(Datastore ds) {
		this.ds = ds;
	}

	@Override
	public void save(Tag t) {
		System.out.println("saving tag " + t.getTag());
		ds.save(t);
	}

	@Override
	public List<Tag> getAll() {
		Query<Tag> q = ds.find(Tag.class);
		System.out.println(q.count());
		return ds.find(Tag.class).stream().collect(Collectors.toList());
	}

	@Override
	public Tag get(String tag) {
		return ds.find(Tag.class).filter(Filters.eq("tag",tag)).first();
	}

	@Override
	public void delete(Tag t) {
		Query<Tag> query = ds.find(Tag.class);
		query.filter(Filters.eq("tag", t.getTag()));
		Tag tag = query.first();
		if(tag != null) {
			ds.delete(tag);
		}
	}
	
	public void upsert(Tag t) {
		Query<Tag> query = ds.find(Tag.class);
		query.filter(Filters.eq("tag", t.getTag()));
		if(query.first() != null) {
			return;
		}
		save(t);
	}

}
