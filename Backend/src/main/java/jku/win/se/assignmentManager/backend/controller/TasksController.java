package jku.win.se.assignmentManager.backend.controller;

import java.util.ArrayList;
import java.util.List;

import jku.win.se.assignmentManager.backend.dao.TaskDao;
import jku.win.se.assignmentManager.backend.dto.Task;
import jku.win.se.assignmentManager.backend.response.ErrorResponse;
import jku.win.se.assignmentManager.backend.response.TasksResponse;
import jku.win.se.assignmentManager.backend.service.JupyterWriteService;
import jku.win.se.assignmentManager.backend.util.StringUtils;
import jku.win.se.assignmentManager.backend.util.WebtokenUtils;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;

public class TasksController {
	
	private TaskDao taskDao;
	
	public TasksController(TaskDao taskDao) {
		this.taskDao = taskDao;
	}
	
	public void initRoutes(String path, ResponseTransformer transformer) {
		Spark.get(path, this::filterTasks, transformer);
	}
	
	private Object filterTasks(Request request, Response response) {
		String authorization = request.headers("Authorization");
		if(StringUtils.isEmptyOrNull(authorization)) {
			response.status(ServerController.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(ServerController.ERROR_MESSAGE_WRONG_REQUEST);
		}
		if(!WebtokenUtils.isTokenValid(authorization)) {
			response.status(ServerController.STATUS_CODE_UNAUTHORIZED);
			response.type("application/json");
			return new ErrorResponse(ServerController.ERROR_MESSAGE_SESSION_EXPIRED);
		}
		
		/*
		 * 1 Fetch all tasks from DB
		 * 2 check for query params (searchTerm and language)
		 * 3 filter by searchTerm (title, taskId, combined metadata, combined cellsource)
		 * 4 if language is set, filter by language
		 * 5 if language is not set, add existing taskIds in other languages to result
		 * 
		 */
		
		List<Task> allTasks = taskDao.getAll();
		List<Task> filteredTasks = new ArrayList<Task>();
		
		String searchTerm = request.queryParams("searchTerm").toLowerCase();
		String language = request.queryParams("language");
		
		if(StringUtils.isEmptyOrNull(searchTerm) && StringUtils.isEmptyOrNull(language)) {
			response.type("application/json");
			response.status(ServerController.STATUS_CODE_OK);
			return new TasksResponse(allTasks);
		}
		
		if(!StringUtils.isEmptyOrNull(searchTerm)) {
			for(Task t : allTasks) {
				if(t.getTitle().toLowerCase().contains(searchTerm)) {
					filteredTasks.add(t);
				} else if(t.getMetadata().get("task_id").toLowerCase().contains(searchTerm)) {
					filteredTasks.add(t);
				} else if(t.getCombinedMetadataAsString().toLowerCase().contains(searchTerm)) {
					filteredTasks.add(t);
				} else if(t.getCombinedCellSourcesAsString().toLowerCase().contains(searchTerm)) {
					filteredTasks.add(t);
				} else if(t.getTags().stream().filter(tag -> tag.toLowerCase().contains(searchTerm)).findAny().isPresent()) {
					filteredTasks.add(t);
				}
				
			}
		}
		
		if(!StringUtils.isEmptyOrNull(language)) {
			List<Task> tasksToRemove = new ArrayList<>();
			for(Task t : allTasks) {
				if(!t.getMetadata().get("language").equals(language)) {
					tasksToRemove.add(t);
				}
			}
			filteredTasks.removeAll(tasksToRemove);
		} else {
			List<Task> tasksAllLanguages = new ArrayList<Task>();
			for(Task tf : filteredTasks) {
				for(Task ta : allTasks) {
					if(tf.getMetadata().get("task_id").equals(ta.getMetadata().get("task_id"))
							&& !tasksAllLanguages.contains(ta)) {
						tasksAllLanguages.add(ta);
					}
				}
			}
			filteredTasks = tasksAllLanguages;
		}
		
		response.type("application/json");
		response.status(ServerController.STATUS_CODE_OK);
		return new TasksResponse(filteredTasks);
	}
}
