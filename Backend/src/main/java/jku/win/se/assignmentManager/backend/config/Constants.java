package jku.win.se.assignmentManager.backend.config;

public class Constants {
	public static final int STATUS_CODE_OK = 200;
	public static final int STATUS_CODE_BAD_REQUEST = 400;
	public static final int STATUS_CODE_NOT_FOUND = 404;
	public static final int STATUS_CODE_UNAUTHORIZED = 401;
	public static final int STATUS_CODE_INTERNAL_SERVER_ERROR = 500;
	
	public static final String ERROR_MESSAGE_WRONG_REQUEST = "The request you submitted does not contain the required data. Please try again.";
	public static final String ERROR_MESSAGE_UNKOWN_USER = "The user with the provided username could not be found.";
	public static final String ERROR_MESSAGE_WRONG_CREDENTIALS = "Username and password do not match.";
	public static final String ERROR_MESSAGE_INTERNAL_SERVER_ERROR = "Your request could not be processed due to technical issues. Please try again later.";
	public static final String ERROR_MESSAGE_SESSION_EXPIRED = "Your session has expired. Please log in again.";
	
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String TASK_TITLE = "task_title";
	public static final String EXERCISE_GERMAN = "Aufgabe";
	public static final String EXERCISE_ENGLISH = "Exercise";
	public static final String POINTS_GERMAN = "Punkte";
	public static final String POINTS_ENGLISH = "Points";
}
