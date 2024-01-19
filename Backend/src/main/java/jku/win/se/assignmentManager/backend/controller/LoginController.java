package jku.win.se.assignmentManager.backend.controller;

import jku.win.se.assignmentManager.backend.config.Constants;
import jku.win.se.assignmentManager.backend.dao.UserDao;
import jku.win.se.assignmentManager.backend.dto.User;
import jku.win.se.assignmentManager.backend.request.LoginRequest;
import jku.win.se.assignmentManager.backend.response.ErrorResponse;
import jku.win.se.assignmentManager.backend.response.LoginResponse;
import jku.win.se.assignmentManager.backend.util.WebtokenUtils;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;

public class LoginController {
	
	private UserDao userDao;
	
	public LoginController(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void initRoutes(String path, ResponseTransformer transformer) {
		Spark.post(path, this::login, transformer);
	}
	
	private Object login(Request request, Response response) {
		
		String body = request.body();
		if(body == null || body.length() <= 0) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		
		LoginRequest loginRequest = null;
		try {
			loginRequest = ServerController.GSON.fromJson(body, LoginRequest.class);
		}
		catch(Exception e) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();
		User user = this.userDao.findByUsername(username);
		if(user == null) {
			response.status(Constants.STATUS_CODE_NOT_FOUND);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_UNKOWN_USER);
		}
		
		String userPw = user.getPassword();
		boolean pwValid = userPw.equals(password);
		if(!pwValid) {
			response.status(Constants.STATUS_CODE_UNAUTHORIZED);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_CREDENTIALS);
		}
		
		String token = WebtokenUtils.createWebtoken(username);
		if(token == null) {
			response.status(Constants.STATUS_CODE_INTERNAL_SERVER_ERROR);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_INTERNAL_SERVER_ERROR);
		}
		
		response.header("Access-Control-Expose-Headers", Constants.HEADER_AUTHORIZATION);
		response.header(Constants.HEADER_AUTHORIZATION, token);
		response.status(Constants.STATUS_CODE_OK);
		response.type("application/json");
		return new LoginResponse(username); 
	}

}
