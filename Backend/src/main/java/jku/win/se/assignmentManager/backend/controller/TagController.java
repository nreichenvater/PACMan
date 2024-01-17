package jku.win.se.assignmentManager.backend.controller;

import jku.win.se.assignmentManager.backend.dao.TagDao;
import jku.win.se.assignmentManager.backend.dto.Tag;
import jku.win.se.assignmentManager.backend.response.ErrorResponse;
import jku.win.se.assignmentManager.backend.response.SuccessResponse;
import jku.win.se.assignmentManager.backend.response.TagsResponse;
import jku.win.se.assignmentManager.backend.util.StringUtils;
import jku.win.se.assignmentManager.backend.util.WebtokenUtils;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;

public class TagController {

	private TagDao tagDao;
	
	public TagController(TagDao tagDao) {
		this.tagDao = tagDao;
	}
	
	public void initRoutes(String path, ResponseTransformer transformer) {
		Spark.post(path, this::createTag, transformer);
		Spark.get(path, this::getTags, transformer);
		Spark.delete(path+"/:tag", this::deleteTag, transformer);
	}
	
	private Object createTag(Request request, Response response) {
		//check if user is logged in with valid token
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
		
		String body = request.body();
		Tag t = null;
		try {
			t = ServerController.GSON.fromJson(body, Tag.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(t == null || t.getTag() == null) {
			response.status(ServerController.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(ServerController.ERROR_MESSAGE_WRONG_REQUEST);
		}
		
		tagDao.upsert(t);
		
		return new SuccessResponse("Der Tag wurde erfolgreich gespeichert");
	}
	
	private Object getTags(Request request, Response response) {
		//check if user is logged in with valid token
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
		
		return new TagsResponse(tagDao.getAll());
	}
	
	private Object deleteTag(Request request, Response response) {
		//check if user is logged in with valid token
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
		
		Tag t = tagDao.get(request.params(":tag"));
		if(t == null) {
			response.status(ServerController.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse("The given tag does not exist");
		}
		tagDao.delete(t);
		
		return new SuccessResponse("The tag was removed successfully");
	}
}
