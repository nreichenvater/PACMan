package jku.win.se.assignmentManager.backend.dao;

import java.util.List;
import java.util.stream.Collectors;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import jku.win.se.assignmentManager.backend.dto.User;

public class UserDao implements DAO<User>{
	
	private Datastore ds;
	
	public UserDao(Datastore ds) {
		this.ds = ds;
	}

	@Override
	public void save(User t) {
		if(ds == null) {
			System.out.println("datastore is null");
		}
		ds.save(t);
	}
	
	public void upsert(User u) {
		Query<User> q = ds.find(User.class);
		if(q.count()>0) {
			q.filter(Filters.eq("username", u.getUsername()));
		}
		User existingUser = q.first();
		if(existingUser == null) {
			save(u);
		}
	}

	@Override
	public List<User> getAll() {
		return ds.find(User.class).stream().collect(Collectors.toList());
	}

	@Override
	public User get(String id) {
		return ds.find(User.class).filter(Filters.eq("_id",id)).first();
	}
	
	public User findByUsername(String username) {
		return ds.find(User.class).filter(Filters.eq("username",username)).first();
	}

	@Override
	public void delete(User t) {
		// TODO Auto-generated method stub
		
	}

}
