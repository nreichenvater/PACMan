package jku.win.se.assignmentManager.backend.controller;

import jku.win.se.assignmentManager.backend.dao.FileDao;
import jku.win.se.assignmentManager.backend.dto.Task;
import jku.win.se.assignmentManager.backend.dto.TaskFile;
import jku.win.se.assignmentManager.backend.response.ErrorResponse;
import jku.win.se.assignmentManager.backend.util.StringUtils;
import jku.win.se.assignmentManager.backend.util.WebtokenUtils;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;

public class FileController {
	
	private FileDao fileDao;
	
	public FileController(FileDao fileDao) {
		this.fileDao = fileDao;
	}

	public void initRoutes(String path, ResponseTransformer transformer) {
		Spark.get(path+"/:id", this::getFile, transformer);
		Spark.delete(path+"/:id", this::deleteFile);
	}
	
	private Object getFile(Request request, Response response) {
		response.type("application/json");
		String authorization = request.headers("Authorization");
		if(StringUtils.isEmptyOrNull(authorization)) {
			response.status(ServerController.STATUS_CODE_BAD_REQUEST);
			return new ErrorResponse(ServerController.ERROR_MESSAGE_WRONG_REQUEST);
		}
		if(!WebtokenUtils.isTokenValid(authorization)) {
			response.status(ServerController.STATUS_CODE_UNAUTHORIZED);
			return new ErrorResponse(ServerController.ERROR_MESSAGE_WRONG_CREDENTIALS);
		}
		TaskFile tf = fileDao.get(request.params(":id"));
		if(tf == null) {
			response.status(ServerController.STATUS_CODE_BAD_REQUEST);
			return new ErrorResponse("The requested file could not be found");
		}
		response.status(ServerController.STATUS_CODE_OK);
		return tf;
	}
	
	private Object deleteFile(Request request, Response response) {
		return null;
	}
}
