package jku.win.se.assignmentManager.backend.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.morphia.annotations.Entity;

@Entity("Notebook")
public class Notebook extends AbstractEntity {
	
	private String title;
	private Map<String,String> metadata;
	private String json;
	private List<String> fileIds;
	
	public Notebook() {}

	public Notebook(String title, Map<String, String> metadata, String json) {
		this.title = title;
		this.metadata = metadata;
		this.json = json;
		fileIds = new ArrayList<>();
	}

	public Notebook(String title, Map<String, String> metadata) {
		super();
		this.title = title;
		this.metadata = metadata;
	}

	public Notebook(String title) {
		super();
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public List<String> getFileIds() {
		return fileIds;
	}

	public void setFileIds(List<String> fileIds) {
		this.fileIds = fileIds;
	}
	
}
