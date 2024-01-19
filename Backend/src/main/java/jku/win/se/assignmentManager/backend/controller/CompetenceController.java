package jku.win.se.assignmentManager.backend.controller;

import java.util.ArrayList;
import java.util.List;

import jku.win.se.assignmentManager.backend.config.Constants;
import jku.win.se.assignmentManager.backend.dao.CompetenceDao;
import jku.win.se.assignmentManager.backend.dao.CompetenceTaskDao;
import jku.win.se.assignmentManager.backend.dao.TaskDao;
import jku.win.se.assignmentManager.backend.dto.Competence;
import jku.win.se.assignmentManager.backend.dto.CompetenceTask;
import jku.win.se.assignmentManager.backend.dto.Dependency;
import jku.win.se.assignmentManager.backend.dto.Task;
import jku.win.se.assignmentManager.backend.request.CompetenceModelRequest;
import jku.win.se.assignmentManager.backend.request.MappingFileRequest;
import jku.win.se.assignmentManager.backend.response.CompetenceTreeResponse;
import jku.win.se.assignmentManager.backend.response.ErrorResponse;
import jku.win.se.assignmentManager.backend.response.SuccessResponse;
import jku.win.se.assignmentManager.backend.service.CompetenceService;
import jku.win.se.assignmentManager.backend.service.JupyterWriteService;
import jku.win.se.assignmentManager.backend.util.StringUtils;
import jku.win.se.assignmentManager.backend.util.WebtokenUtils;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;

public class CompetenceController {
	
	private CompetenceDao competenceDao;
	private TaskDao taskDao;
	private CompetenceTaskDao competenceTaskDao;
	
	public CompetenceController(CompetenceDao competenceDao, TaskDao taskDao, CompetenceTaskDao competenceTaskDao) {
		this.competenceDao = competenceDao;
		this.taskDao = taskDao;
		this.competenceTaskDao = competenceTaskDao;
	}
	
	public void initRoutes(String path, ResponseTransformer transformer) {
		Spark.post(path+"/model", this::saveModel, transformer);
		Spark.post(path+"/mapping", this::saveMapping, transformer);
		Spark.delete(path+"/mapping", this::deleteMappingAndModel, transformer);
		Spark.get(path+"/tree", this::getTree, transformer);
	}
	
	private Object saveModel(Request request, Response response) {
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
		
		String body = request.body();
		CompetenceModelRequest cmr = null;
		try {
			cmr = ServerController.GSON.fromJson(body, CompetenceModelRequest.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(cmr == null) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		if(cmr.getCompetences().size() <= 0) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		
		//save as tree
		for(Competence c : cmr.getCompetences()) {
			for(Dependency d : c.getDependencies()) {
				if(d.getType().equals("ist_Teil_von")) {
					Competence parent = CompetenceService.getCompById(d.getId(), cmr.getCompetences());
					if(parent != null) {
						parent.getChildren().add(c);
					}
				}
			}
		}
		
		//only upsert parents
		for(Competence c : cmr.getCompetences()) {
			if(c.isParent()) {
				competenceDao.upsert(c);
			}
		}
		
		response.status(200);
		return new SuccessResponse("The competence model was saved successfully");
	}
	
	private Object saveMapping(Request request, Response response) {
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
		
		String body = request.body();
		System.out.println(body);
		MappingFileRequest mfr = null;
		try {
			mfr = ServerController.GSON.fromJson(body, MappingFileRequest.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(mfr == null) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			response.type("application/json");
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		//save weighted competences on existing tasks, but also save CompetenceTask objects for not yet existing tasks
		for(CompetenceTask ct : mfr.getAssignmentMapping().getTasks()) {
			//possibly more than one task with same taskId for different languages
			List<Task> tasks = taskDao.getAllByTaskId(ct.getTask_id());
			for(Task t : tasks) {
				if(t.getComp_prim() == null) {
					t.setComp_prim(new ArrayList<>());
				}
				if(t.getComp_sec() == null) {
					t.setComp_sec(new ArrayList<>());
				}
				t.getComp_prim().addAll(ct.getComp_prim());
				t.getComp_sec().addAll(ct.getComp_sec());
				taskDao.save(t);
			}
			competenceTaskDao.save(ct);
		}
		
		response.status(200);
		return new SuccessResponse("The mapping file was saved successfully");
	}
	
	public Object getTree(Request request, Response response) {
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
		
		List<CompetenceTask> competenceTasks = competenceTaskDao.getAll();
		List<Competence> competences = competenceDao.getAll();
		
		//check if there is a searchQuery, if so, first filter competences
		String searchTerm = request.queryParams("searchTerm").toLowerCase();
		if(!StringUtils.isEmptyOrNull(searchTerm)) {
			List<Competence> matchingParentCompetences = new ArrayList<>();
			for(Competence c : competences) {
				if(CompetenceService.getMatchingCompetences(c, searchTerm)) {
					matchingParentCompetences.add(c);
				}
			}
			competences = matchingParentCompetences;
		}
		
		for(Competence c : competences) {
			CompetenceService.addWeightedTasksToCompetence(c, competenceTasks, taskDao);
		}
		
		return new CompetenceTreeResponse(competences);
	}
	
	public Object deleteMappingAndModel(Request request, Response response) {
		response.type("application/json");
		//check if user is logged in with valid token
		String authorization = request.headers("Authorization");
		if(StringUtils.isEmptyOrNull(authorization)) {
			response.status(Constants.STATUS_CODE_BAD_REQUEST);
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_REQUEST);
		}
		if(!WebtokenUtils.isTokenValid(authorization)) {
			response.status(Constants.STATUS_CODE_UNAUTHORIZED);
			return new ErrorResponse(Constants.ERROR_MESSAGE_WRONG_CREDENTIALS);
		}
		
		//delete CompetenceTasks
		List<CompetenceTask> competenceTasks = competenceTaskDao.getAll();
		competenceTasks.stream().forEach(ct -> competenceTaskDao.delete(ct));
		
		//remove competences from task
		List<Task> tasks = taskDao.getAll();
		tasks.stream().forEach(t -> {
			t.setComp_prim(null);
			t.setComp_sec(null);
			t.setJson(JupyterWriteService.generateTaskJson(t));
			taskDao.save(t);
		});
		
		//delete competences
		List<Competence> competences = competenceDao.getAll();
		System.out.println("found " + competences.size() + " competences");
		competences.stream().forEach(c -> competenceDao.delete(c));
		
		return new SuccessResponse("All competence mappings were removed");
	}

}
