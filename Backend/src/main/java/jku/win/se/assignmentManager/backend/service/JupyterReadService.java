package jku.win.se.assignmentManager.backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jku.win.se.assignmentManager.backend.config.Constants;
import jku.win.se.assignmentManager.backend.dao.NotebookDao;
import jku.win.se.assignmentManager.backend.dao.TagDao;
import jku.win.se.assignmentManager.backend.dto.Cell;
import jku.win.se.assignmentManager.backend.dto.JsonCell;
import jku.win.se.assignmentManager.backend.dto.Notebook;
import jku.win.se.assignmentManager.backend.dto.Tag;
import jku.win.se.assignmentManager.backend.dto.Task;
import jku.win.se.assignmentManager.backend.request.NotebookJsonRequest;
import jku.win.se.assignmentManager.backend.request.TaskJsonRequest;
import jku.win.se.assignmentManager.backend.util.StringUtils;

public class JupyterReadService {

	public static Task readTask(TaskJsonRequest tjr, TagDao tagDao) {
		Task t = new Task();
		
		for(JsonCell jc : tjr.getCells()) {
			if(getElementType(jc).equals(Constants.TASK_TITLE)) {
				String title = jc.getSource().get(0);
				if(title.contains("[") && title.contains("]")) {
					title = title.substring(title.indexOf(':')+2, title.indexOf('[')-1).replace("\\n", "").replace("\n", "");
				} else {
					title = title.substring(title.indexOf(':')+2, title.length()).replace("\\n", "").replace("\n", "");
				}
				t.setTitle(title);
				
				Map<String, String> md = jc.getMetadata();
				t.setMetadata(md);
			
				if(jc.getTags() != null && jc.getTags().size() > 0) {
					jc.getTags().stream().forEach(tag -> tagDao.upsert(new Tag(tag)));
					t.setTags(jc.getTags());
				}
			} else {
				Cell c = new Cell();
				c.setType(jc.getType());
				c.setSource(StringUtils.combine(jc.getSource()).replace("\\", "\\\\"));
				c.setMetadata(jc.getMetadata());
				c.setOutputs(jc.getOutputs());
				t.getCells().add(c);
			}
		}
		
		t.setJson(JupyterWriteService.generateTaskJson(t));
		
		return t;
	}
	
	public static List<Task> readNotebook(NotebookJsonRequest nbjr, String nbBody, TagDao tagDao, NotebookDao notebookDao) {
		//save complete notebook
		saveNotebook(nbjr,notebookDao,nbBody);
		
		Set<String> taskIds = new HashSet<>(); //unique insert
		for(JsonCell jc : nbjr.getCells()) {
			String taskId = jc.getMetadata().get("task_id");
			if(!StringUtils.isEmptyOrNull(taskId)) {
				taskIds.add(taskId);
			}
		}
		
		List<Task> tasks = new ArrayList<>();
		
		for(String t : taskIds) {
			TaskJsonRequest tjr = new TaskJsonRequest();
			for(JsonCell jc : nbjr.getCells()) {
				if(jc.getMetadata().get("task_id") != null && jc.getMetadata().get("task_id").equals(t)) {
					tjr.getCells().add(jc);
				}
			}
			tasks.add(readTask(tjr, tagDao));
		}
		return tasks;
	}
	
	public static void saveNotebook(NotebookJsonRequest nbjr, NotebookDao notebookDao, String nbBody) {
		Notebook nb = new Notebook();
		nb.setTitle(nbjr.getCells().get(0).getMetadata().get("assignment_id"));
		nb.setMetadata(nbjr.getCells().get(0).getMetadata());
		nb.setFileIds(new ArrayList<>());
		nb.setJson(nbBody);
		notebookDao.save(nb);
	}
	
	private static String getElementType(JsonCell jc) {
		String et = jc.getMetadata().get("element_type");
		if(!StringUtils.isEmptyOrNull(et)) {
			return et;
		}
		return "";
	}
}
