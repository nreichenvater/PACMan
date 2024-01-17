package jku.win.se.assignmentManager.backend.controller;

import com.google.gson.Gson;

import jku.win.se.assignmentManager.backend.config.MongoDBConfig;
import jku.win.se.assignmentManager.backend.dao.CompetenceDao;
import jku.win.se.assignmentManager.backend.dao.CompetenceTaskDao;
import jku.win.se.assignmentManager.backend.dao.FileDao;
import jku.win.se.assignmentManager.backend.dao.NotebookDao;
import jku.win.se.assignmentManager.backend.dao.TagDao;
import jku.win.se.assignmentManager.backend.dao.TaskDao;
import jku.win.se.assignmentManager.backend.dao.UserDao;
import jku.win.se.assignmentManager.backend.dto.User;
import jku.win.se.assignmentManager.backend.transformer.JsonTransformer;

public class ServerController {
	
	public static final Gson GSON = new Gson();
	public static final JsonTransformer TRANSFORMER = new JsonTransformer();
	
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
	
	private TaskDao taskDao;
	private TagDao tagDao;
	private UserDao userDao;
	private NotebookDao notebookDao;
	private CompetenceDao competenceDao;
	private CompetenceTaskDao competenceTaskDao;
	private FileDao fileDao;
	
	private TaskController taskController;
	private TagController tagController;
	private TasksController tasksController;
	private LoginController loginController;
	private UserController userController;
	private NotebookController notebookController;
	private CompetenceController competenceController;
	private FileController fileController;
	
	public ServerController() {
		userDao = new UserDao(MongoDBConfig.getInstance().getDatastore());
		taskDao = new TaskDao(MongoDBConfig.getInstance().getDatastore());
		notebookDao = new NotebookDao(MongoDBConfig.getInstance().getDatastore());
		competenceDao = new CompetenceDao(MongoDBConfig.getInstance().getDatastore());
		competenceTaskDao = new CompetenceTaskDao(MongoDBConfig.getInstance().getDatastore());
		tagDao = new TagDao(MongoDBConfig.getInstance().getDatastore());
		fileDao = new FileDao(MongoDBConfig.getInstance().getDatastore());
		
		taskController = new TaskController(taskDao, competenceTaskDao, fileDao);
		tagController = new TagController(tagDao);
		tasksController = new TasksController(taskDao);
		loginController = new LoginController(userDao);
		userController = new UserController();
		notebookController = new NotebookController(notebookDao, taskDao, tagDao);
		competenceController = new CompetenceController(competenceDao, taskDao, competenceTaskDao);
		fileController = new FileController(fileDao);
	}
	
	public void start() {
		userDao.save(new User("admin","100"));
		tagController.initRoutes("/tag", TRANSFORMER);
		taskController.initRoutes("/task", TRANSFORMER);
		tasksController.initRoutes("/tasks", TRANSFORMER);
		loginController.initRoutes("/login", TRANSFORMER);
		userController.initRoutes("/user", TRANSFORMER);
		notebookController.initRoutes("/notebook", TRANSFORMER);
		competenceController.initRoutes("/competence", TRANSFORMER);
		fileController.initRoutes("/file", TRANSFORMER);
	}

}
