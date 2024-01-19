package jku.win.se.assignmentManager.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;

import jku.win.se.assignmentManager.backend.config.Constants;
import jku.win.se.assignmentManager.backend.dao.NotebookDao;
import jku.win.se.assignmentManager.backend.dao.TagDao;
import jku.win.se.assignmentManager.backend.dao.TaskDao;
import jku.win.se.assignmentManager.backend.dto.Notebook;
import jku.win.se.assignmentManager.backend.dto.Task;
import jku.win.se.assignmentManager.backend.request.NotebookJsonRequest;
import jku.win.se.assignmentManager.backend.request.NotebookRequest;
import jku.win.se.assignmentManager.backend.request.TaskJsonRequest;
import jku.win.se.assignmentManager.backend.response.ErrorResponse;
import jku.win.se.assignmentManager.backend.response.NotebookResponse;
import jku.win.se.assignmentManager.backend.response.NotebooksResponse;
import jku.win.se.assignmentManager.backend.response.PreviewResponse;
import jku.win.se.assignmentManager.backend.response.SuccessResponse;
import jku.win.se.assignmentManager.backend.service.JupyterReadService;
import jku.win.se.assignmentManager.backend.service.JupyterWriteService;
import jku.win.se.assignmentManager.backend.util.StringUtils;
import jku.win.se.assignmentManager.backend.util.WebtokenUtils;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;

public class NotebookController {
	
	private NotebookDao notebookDao;
	private TaskDao taskDao;
	private TagDao tagDao;
	
	public NotebookController(NotebookDao notebookDao, TaskDao taskDao, TagDao tagDao) {
		this.notebookDao = notebookDao;
		this.taskDao = taskDao;
		this.tagDao = tagDao;
	}
	
	public void initRoutes(String path, ResponseTransformer transformer) {
		Spark.post(path, this::saveNotebook, transformer);
		Spark.post(path+"/upload", this::saveUploadNotebook, transformer);
		Spark.post(path+"/preview", this::getNotebookPreview, transformer);
		Spark.delete(path+"/:id", this::deleteNotebook, transformer);
		Spark.get(path, this::get, transformer);
	}
	
	private Object saveNotebook(Request request, Response response) {
		String authorization = request.headers("Authorization");
		if(StringUtils.isEmptyOrNull(authorization)) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		if(!WebtokenUtils.isTokenValid(authorization)) {
			response.status(Constants.STATUS_CODE_UNAUTHORIZED);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_CREDENTIALS);
		}
		
		//check if body is valid and mandatory data is provided
		String body = request.body();
		NotebookRequest notebookRequest = null;
		try {
			notebookRequest = ServerController.GSON.fromJson(body, NotebookRequest.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(notebookRequest == null) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		String validationError = notebookRequest.validateBody();
		if(!StringUtils.isEmptyOrNull(validationError)) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(validationError);
		}
		
		String json = JupyterWriteService.generateNotebookJson(notebookRequest.getTasks(), notebookRequest.getTitle(), notebookRequest.getMetadata().get("tutor"), notebookRequest.getInfo(), notebookRequest.getMetadata(), notebookRequest.isIncludeGradingTable());
		
		Notebook notebook = new Notebook();
		notebook.setTitle(notebookRequest.getTitle());
		notebook.setMetadata(notebookRequest.getMetadata());
		notebook.setJson(json);
		notebook.setFileIds(notebookRequest.getTasks().stream().filter(t -> t.getFile() != null).map(t -> t.getFile()).collect(Collectors.toList()));
		
		notebookDao.save(notebook);
		
		response.status(Constants.STATUS_CODE_OK);
		return new NotebookResponse(notebook.getMetadata().get("assignment_id"), json);
	}
	
	private Object get(Request request, Response response) {
		response.type("application/json");
		String authorization = request.headers("Authorization");
		if(StringUtils.isEmptyOrNull(authorization)) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		if(!WebtokenUtils.isTokenValid(authorization)) {
			response.status(Constants.STATUS_CODE_UNAUTHORIZED);
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_CREDENTIALS);
		}
		
		String id = request.queryParams("id");
		if(!StringUtils.isEmptyOrNull(id)) {
			Notebook nb = notebookDao.get(id);
			if(nb != null) {
				response.status(Constants.STATUS_CODE_OK);
				return new NotebookResponse(nb.getMetadata().get("assignment_id"), nb.getJson());
			}
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		
		List<Notebook> allNotebooks = notebookDao.getAll();
		
		String searchTerm = request.queryParams("searchTerm");
		if(!StringUtils.isEmptyOrNull(searchTerm)) {
			searchTerm = searchTerm.toLowerCase();
			List<Notebook> filteredNotebooks = new ArrayList<>();
			for(Notebook nb : allNotebooks) {
				if(nb.getTitle().toLowerCase().contains(searchTerm)
						|| nb.getJson().toLowerCase().contains(searchTerm)) {
					filteredNotebooks.add(nb);
				}
			}
			response.status(Constants.STATUS_CODE_OK);
			return new NotebooksResponse(filteredNotebooks);
		}
		
		response.status(Constants.STATUS_CODE_OK);
		return new NotebooksResponse(allNotebooks);
	}

	private Object deleteNotebook(Request request, Response response) {
		//check if user is logged in with valid token
		String authorization = request.headers("Authorization");
		if(StringUtils.isEmptyOrNull(authorization)) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		if(!WebtokenUtils.isTokenValid(authorization)) {
			response.status(Constants.STATUS_CODE_UNAUTHORIZED);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_CREDENTIALS);
		}
		
		Notebook nb = notebookDao.get(request.params(":id"));
		if(nb != null) {
			notebookDao.delete(nb);
			response.status(Constants.STATUS_CODE_OK);
			return new SuccessResponse("The notebook was deleted successfully");
		}
		response.status(Constants.STATUS_CODE_BAD_REQUEST);
		return new ErrorResponse("The notebook could not be found");
	}
	
	private Object saveUploadNotebook(Request request, Response response) {
		String authorization = request.headers("Authorization");
		if(StringUtils.isEmptyOrNull(authorization)) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		if(!WebtokenUtils.isTokenValid(authorization)) {
			response.status(Constants.STATUS_CODE_UNAUTHORIZED);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_CREDENTIALS);
		}
		
		String body = request.body();
		JsonElement je = ServerController.GSON.fromJson(body, JsonElement.class);
		NotebookJsonRequest nbjr = null;
		TaskJsonRequest tjr = null;
		try {
			nbjr = ServerController.GSON.fromJson(je, NotebookJsonRequest.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(nbjr == null) {
			try {
				tjr = ServerController.GSON.fromJson(je, TaskJsonRequest.class);
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(tjr == null) {
				response.status(Constants.STATUS_CODE_BAD_REQUEST);
				response.type("application/json");
				return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
			}
			Task t = JupyterReadService.readTask(tjr, tagDao);
			taskDao.upsert(t);
			return new SuccessResponse("The task was saved successfully");
		}
		List<Task> tasks = JupyterReadService.readNotebook(nbjr, body, tagDao, notebookDao);
		for(Task t : tasks) {
			System.out.println("saving task " + t.getMetadata().get("task_id"));
			taskDao.upsert(t);
		}
		return new SuccessResponse("The notebook tasks were saved successfully");
	}
	
	private Object getNotebookPreview(Request request, Response response) {
		String authorization = request.headers("Authorization");
		if(StringUtils.isEmptyOrNull(authorization)) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		if(!WebtokenUtils.isTokenValid(authorization)) {
			response.status(Constants.STATUS_CODE_UNAUTHORIZED);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_CREDENTIALS);
		}
		//check if body is valid and mandatory data is provided
		String body = request.body();
		NotebookRequest notebookRequest = null;
		try {
			notebookRequest = ServerController.GSON.fromJson(body, NotebookRequest.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(notebookRequest == null) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		String validationError = notebookRequest.validateBody();
		if(!StringUtils.isEmptyOrNull(validationError)) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(validationError);
		}
		
		String json = JupyterWriteService.generateNotebookJson(notebookRequest.getTasks(), notebookRequest.getTitle(), notebookRequest.getMetadata().get("tutor"), notebookRequest.getInfo(), notebookRequest.getMetadata(), notebookRequest.isIncludeGradingTable());
		response.status(Constants.STATUS_CODE_OK);
		response.type("application/json");
		return new PreviewResponse(json);
	}
}
