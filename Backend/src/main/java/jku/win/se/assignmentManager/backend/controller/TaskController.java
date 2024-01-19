package jku.win.se.assignmentManager.backend.controller;

import java.util.List;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.testcontainers.shaded.org.bouncycastle.util.encoders.Base64;

import jku.win.se.assignmentManager.backend.config.Constants;
import jku.win.se.assignmentManager.backend.dao.CompetenceTaskDao;
import jku.win.se.assignmentManager.backend.dao.FileDao;
import jku.win.se.assignmentManager.backend.dao.TaskDao;
import jku.win.se.assignmentManager.backend.dto.CompetenceTask;
import jku.win.se.assignmentManager.backend.dto.Task;
import jku.win.se.assignmentManager.backend.request.TaskRequest;
import jku.win.se.assignmentManager.backend.response.ErrorResponse;
import jku.win.se.assignmentManager.backend.response.SuccessResponse;
import jku.win.se.assignmentManager.backend.response.TaskExistsResponse;
import jku.win.se.assignmentManager.backend.service.JupyterWriteService;
import jku.win.se.assignmentManager.backend.util.StringUtils;
import jku.win.se.assignmentManager.backend.util.WebtokenUtils;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;

public class TaskController {
	
	private TaskDao taskDao;
	private CompetenceTaskDao competenceTaskDao;
	private FileDao fileDao;
	
	public TaskController(TaskDao taskDao, CompetenceTaskDao competenceTaskDao, FileDao fileDao) {
		this.taskDao = taskDao;
		this.competenceTaskDao = competenceTaskDao;
		this.fileDao = fileDao;
	}
	
	public void initRoutes(String path, ResponseTransformer transformer) {
		Spark.post(path, this::upsertTask);
		Spark.get(path+"/:taskid/:lang", this::checkTaskExists, transformer);
		Spark.delete(path+"/:id", this::deleteTask);
	}
	
	private Object checkTaskExists(Request request, Response response) {
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
		
		List<Task> ts = taskDao.getAllByTaskId(request.params(":taskid"));
		if(ts.size() <= 0) {
			response.status(Constants.STATUS_CODE_OK);
			return new TaskExistsResponse(false);
		}
		for(Task t : ts) {
			if(t.getMetadata().get("language").equals(request.params(":lang"))){
				response.status(Constants.STATUS_CODE_OK);
				return new TaskExistsResponse(true);
			}
		}
		response.status(Constants.STATUS_CODE_OK);
		return new TaskExistsResponse(false);
	}

	private Object upsertTask(Request request, Response response) {
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
		TaskRequest taskRequest = null;
		try {
			taskRequest = ServerController.GSON.fromJson(body, TaskRequest.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(taskRequest == null) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		String validationError = taskRequest.validateBody();
		if(!StringUtils.isEmptyOrNull(validationError)) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(validationError);
		}
		
		Task t = new Task();
		t.setTitle(taskRequest.getTitle());
		t.setMetadata(taskRequest.getMetadata());
		t.setCells(taskRequest.getCells());
		t.setNote(taskRequest.getNote());
		if(taskRequest.getTags() != null && !taskRequest.getTags().isEmpty()) {
			t.setTags(taskRequest.getTags());
		}
		
		taskRequest.getFile().setBinContent(new Binary(BsonBinarySubType.BINARY, Base64.decode(taskRequest.getFile().getStringContent())));
		String fileId = fileDao.saveWithId(taskRequest.getFile());
		t.setFile(fileId);
		
		CompetenceTask ct = competenceTaskDao.getByTaskId(t.getMetadata().get("task_id"));
		if(ct != null) {
			t.setComp_prim(ct.getComp_prim());
			t.setComp_sec(ct.getComp_sec());
		}
		
		t.setJson(JupyterWriteService.generateTaskJson(t));
		
		taskDao.upsert(t);
		
		response.status(Constants.STATUS_CODE_OK);
		return new SuccessResponse("The task was saved successfully");
	}
	
	private Object deleteTask(Request request, Response response) {
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
		response.type("application/json");
		Task t = taskDao.get(request.params(":id"));
		if(t != null) {
			taskDao.delete(t);
			response.status(Constants.STATUS_CODE_OK);
			return new SuccessResponse("The task was deleted successfully");
		}
		response.status(Constants.STATUS_CODE_BAD_REQUEST);
		return new ErrorResponse("The task could not be found");
	}
	
}
