package jku.win.se.assignmentManager.backend.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import jku.win.se.assignmentManager.backend.dto.Task;

public class TaskDao implements DAO<Task>{
	
	private Datastore ds;
	
	public TaskDao(Datastore ds) {
		this.ds = ds;
	}

	@Override
	public void save(Task t) {
		ds.save(t);
	}

	@Override
	public List<Task> getAll() {
		return ds.find(Task.class).stream().collect(Collectors.toList());
	}

	@Override
	public Task get(String id) {
		return ds.find(Task.class).filter(Filters.eq("_id",id)).first();
	}

	public Task getByTaskId(String taskId) {
		return ds.find(Task.class).filter(Filters.eq("metadata.task_id",taskId)).first();
	}
	
	public List<Task> getAllByTaskId(String taskId) {
		return ds.find(Task.class).filter(Filters.eq("metadata.task_id",taskId)).stream().collect(Collectors.toList());
	}
	
	public List<Task> getTasksByTaskIds(List<String> taskIds){
		List<Task> tasks = new ArrayList<>();
		taskIds.forEach(tid -> {
			tasks.addAll(getAllByTaskId(tid));
		});
		return tasks;
	}
	
	public void upsert(Task newTask) {
		//if task with combination of taskId and language exists, override it by manipulating existing task (mongo object id immutable)
		Query<Task> query = ds.find(Task.class);
		String taskId = newTask.getMetadata().get("task_id");
		String lang = newTask.getMetadata().get("language");
		query.filter(Filters.eq("metadata.task_id", taskId));
		query.filter(Filters.eq("metadata.language", lang));
		if(query.count() > 0) {
			Task existingTask = query.first();
			existingTask.setTitle(newTask.getTitle());
			existingTask.setMetadata(newTask.getMetadata());
			existingTask.setCells(newTask.getCells());
			existingTask.setTags(newTask.getTags());
			existingTask.setJson(newTask.getJson());
			existingTask.setNote(newTask.getNote());
			existingTask.setFile(newTask.getFile());
			ds.save(existingTask);
		} else {
			ds.save(newTask);
		}
	}

	@Override
	public void delete(Task t) {
		ds.delete(t);
	}
}
