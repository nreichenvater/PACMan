package jku.win.se.assignmentManager.backend.controller;

import jku.win.se.assignmentManager.backend.config.Constants;
import jku.win.se.assignmentManager.backend.response.ErrorResponse;
import jku.win.se.assignmentManager.backend.response.SuccessResponse;
import jku.win.se.assignmentManager.backend.util.WebtokenUtils;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;

public class UserController {
	
	public void initRoutes(String path, ResponseTransformer transformer) {
		Spark.get(path, this::isUserLoggedIn, transformer);
	}
	
	private Object isUserLoggedIn(Request request, Response response) {
		String token = request.headers("authorization");
		if(token == null || token.equals("")) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		if(!WebtokenUtils.isTokenValid(token)) {
			response.status(Constants.STATUS_CODE_UNAUTHORIZED);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_CREDENTIALS);
		}
		response.type("application/json");
		return new SuccessResponse("user is logged in/token is valid");
	}
}
