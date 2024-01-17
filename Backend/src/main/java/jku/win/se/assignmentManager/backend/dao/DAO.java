package jku.win.se.assignmentManager.backend.dao;

import java.util.List;

public interface DAO<T> {
	void save(T t);
	List<T> getAll();
	T get(String id);
	void delete(T t);
}
