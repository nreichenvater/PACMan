package jku.win.se.assignmentManager.backend.service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jku.win.se.assignmentManager.backend.config.Constants;
import jku.win.se.assignmentManager.backend.dto.Cell;
import jku.win.se.assignmentManager.backend.dto.Task;
import jku.win.se.assignmentManager.backend.enumeration.CellSourceType;

public class JupyterWriteService {

	public static String generateNotebookJson(List<Task> tasks, String title, String tutor, String info, Map<String,String> metadata,boolean includeGradingTable) {
		StringBuilder json = new StringBuilder();
		json.append("{\n \"cells\": [\n");
		for(int i=0; i<tasks.size(); i++) {
			int exerciseNumber = i+1;
			tasks.get(i).getMetadata().put("exerciseNumber", Integer.toString(exerciseNumber));
		}
		json.append(getNotebookHeaderSection(title, tutor, info, metadata, tasks, includeGradingTable));
		for(int i=0; i<tasks.size(); i++) {
			json.append(transformTask(tasks.get(i)));
			if(tasks.size()-i>1) {
				json.append("  {\n   \"cell_type\": \"markdown\",\n   \"metadata\": {},\n   \"source\": [\n    \"________________\"\n   ]\n  },\n");
			}
		}
		json.deleteCharAt(json.lastIndexOf(","));
		json.append(" ]");
		json.append(getAddidionalFields());
		json.append("}");
		return json.toString();
	}
	
	public static String generateTaskJson(Task t) {
		StringBuilder json = new StringBuilder();
		json.append("{\n \"cells\": [\n");
		json.append(transformTask(t));
		json.deleteCharAt(json.lastIndexOf(","));
		json.append(" ]\n}");
		return json.toString();
	}
	
	private static String getNotebookHeaderSection(String title, String tutor, String info, Map<String,String> metadata, List<Task> tasks, boolean includeGradingTable) {
		StringBuilder json = new StringBuilder();
		json.append("  {\n   \"cell_type\": \"markdown\",\n");
		json.append("   \"metadata\": {\n");
		json.append("    \"element_type\":\"assignment_title\",\n");
		for(Map.Entry<String, String> entry : metadata.entrySet()) {
			if(entry.getKey() != "tutor") {
				json.append("    \""+entry.getKey()+"\": "+getMetadataStringRep(entry.getValue())+",\n");
			}
		}	
		json.deleteCharAt(json.lastIndexOf(","));
		json.append("   },\n   \"source\": [\n");
		json.append("    \"# " + title + "\"\n   ]\n  },\n");
	
		json.append("  {\n   \"cell_type\": \"markdown\",\n");
		json.append("   \"metadata\": {\n    \"element_type\":\"student_data\"\n   },\n");
		json.append("   \"source\": [\n");
		json.append(getCellSourceStringRep(new Cell(info)));
		json.append("\n   ]\n  },\n");
		if(includeGradingTable) {
			json.append(getGradingTableStringRp(tasks));
		}
		json.append("  {\n   \"cell_type\": \"markdown\",\n   \"metadata\": {\n    \"element_type\": \"tutor_data\"\n   },\n   \"source\": [\n    \"**Tutor:** "+tutor+"\"\n   ]\n  },");
		return json.toString();
	}
	
	private static String getGradingTableStringRp(List<Task> tasks) {
		StringBuilder json = new StringBuilder();
		json.append("  {\n   \"cell_type\": \"markdown\",\n");
		json.append("   \"metadata\": {\"element_type\": \"grading_table\"},\n");
		json.append("   \"source\": [\n");
		json.append("    \"| Exercise    | Max Points   | Result  |\\n\",\n");
		json.append("    \"| :---------- | :----------: | ------: |\\n\",\n");
		int sumPoints = 0;
		for(int i=0; i<tasks.size(); i++) {
			json.append("    \"|"+tasks.get(i).getMetadata().get("exerciseNumber")+"            | "+tasks.get(i).getMetadata().get("max_points")+"            |         |\\n\",\n");
			sumPoints = sumPoints + Integer.parseInt(tasks.get(i).getMetadata().get("max_points"));
		}
		json.append("    \"|_____________|______________|_________|\\n\",\n");
		json.append("    \"|Sum          |"+sumPoints+"            |         |\"");
		json.append("\n   ]\n  },\n");
		return json.toString();
	}
	
	private static String transformTask(Task t) {
		StringBuilder json = new StringBuilder();
		json.append("  {\n   \"cell_type\": \"markdown\",\n   \"metadata\": {\n");
		for(Map.Entry<String, String> entry : t.getMetadata().entrySet()) {
			if(entry.getKey() != "max_points") {
				json.append("    \""+entry.getKey()+"\": "+getMetadataStringRep(entry.getValue())+",\n");
			}
		}
		if(t.getTags() != null && t.getTags().size() > 0) {
			json.append("\"tags\":[");
			for(int i=0; i<t.getTags().size(); i++) {
				json.append("\""+t.getTags().get(i)+"\"");
				if(i<t.getTags().size()-1) {
					json.append(",");
				}
			}
			json.append("]");
		} else {
			json.deleteCharAt(json.lastIndexOf(","));	
		}
		json.append("   },\n   \"source\": [\n");
		String exercise = t.getMetadata().get("language").equals("de") ? Constants.EXERCISE_GERMAN : Constants.EXERCISE_ENGLISH;
		if(t.getMetadata().get("exerciseNumber") != null) {
			exercise = exercise + " " + t.getMetadata().get("exerciseNumber");
		}
		exercise = exercise + ": ";
		String points = t.getMetadata().get("language").equals("de") ? Constants.POINTS_GERMAN : Constants.POINTS_ENGLISH;
		json.append("    \"## " + exercise + t.getTitle());
		if(Integer.parseInt(t.getMetadata().get("max_points")) > 0) json.append(" ["+t.getMetadata().get("max_points")+" "+points+"]");
		json.append("\"\n   ]\n  },\n");
		t.getCells().stream().forEach(c -> json.append(transformCell(c)));
		return json.toString();
	}
	
	private static String transformCell(Cell c) {
		StringBuilder json = new StringBuilder();
		json.append("  {\n   \"cell_type\": \""+c.getType().toString().toLowerCase()+"\",\n");
		json.append("   \"metadata\": {");
		for(Map.Entry<String, String> entry : c.getMetadata().entrySet()) {
			if(entry.getKey() != "tutor") {
				json.append("    \""+entry.getKey()+"\": "+getMetadataStringRep(entry.getValue())+",\n");
			}
		}	
		json.deleteCharAt(json.lastIndexOf(","));
		json.append("},\n");
		if(c.getType() == CellSourceType.CODE) {
			json.append("   \"execution_count\": null,\n");
			if(c.getOutputs() != null) {
				json.append("   \"outputs\": [\n");
				for(int i=1; i<=c.getOutputs().size(); i++) {
					json.append(c.getOutputs().get(i-1).toString());
					if(i<c.getOutputs().size()) {json.append(",");}
				}
				json.append("],\n");	
			} else {
				json.append("   \"outputs\": [],\n");
			}
		}
		json.append("   \"source\": [\n");
		json.append(getCellSourceStringRep(c));
		json.append("\n   ]\n  },\n");
		return json.toString();
	}
	
	private static String getCellSourceStringRep(Cell c) {
		String lines[] = c.getSource().split("\\r?\\n");
		StringBuilder json = new StringBuilder();
		for(int i=0; i<lines.length; i++) {
			if(i > 0) {
				json.append("\n");
			}
			json.append("    \""+lines[i].replaceAll("\"", "\\\\\"")+"\\n\""); //.replaceAll("\\", "\\\\")
			if(lines.length - i > 1) {
				json.append(",");
			}
		}
		return json.toString();
	}
	
	private static String getMetadataStringRep(String val) {
		if(val.equals("true") || val.equals("false")) {
			return val;
		}
		else if (Pattern.matches("[0-9]+", val)) {
		    return val;
		}
		return "\""+val+"\"";
	}
	
	public static String getAddidionalFields() {
		return ",\r\n"
				+"\"metadata\": {"
		    +"\"kernelspec\": {"
		    + "\"name\": \"python\","
		     + "\"display_name\": \"Python (Pyodide)\","
		     + "\"language\": \"python\""
		   + "},"
		    +"\"language_info\": {"
		     + "\"codemirror_mode\": {"
		      +  "\"name\": \"python\","
		      +  "\"version\": 3"
		      +"},"
		     + "\"file_extension\": \".py\","
		     + "\"mimetype\": \"text/x-python\","
		     + "\"name\": \"python\","
		     + "\"nbconvert_exporter\": \"python\","
		     + "\"pygments_lexer\": \"ipython3\","
		     + "\"version\": \"3.8\""
		    +"}"
		  +"},\r\n" 
				+" \"nbformat\": 4,\r\n" 
				+" \"nbformat_minor\": 1\n";
	}
}
