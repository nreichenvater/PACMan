package jku.win.se.assignmentManager.backend.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.morphia.annotations.Entity;

@Entity("Task")
public class Task extends AbstractEntity {
	
	private String title;
	private Map<String, String> metadata;
	private List<String> tags;
	private List<Cell> cells;
	private String note;
	private List<WeightedCompetence> comp_prim;
	private List<WeightedCompetence> comp_sec;
	private String json;
	private String file;
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public List<WeightedCompetence> getComp_prim() {
		return comp_prim;
	}
	public void setComp_prim(List<WeightedCompetence> comp_prim) {
		this.comp_prim = comp_prim;
	}
	public List<WeightedCompetence> getComp_sec() {
		return comp_sec;
	}
	public void setComp_sec(List<WeightedCompetence> comp_sec) {
		this.comp_sec = comp_sec;
	}
	public Task() {
		cells = new ArrayList<>();
		tags = new ArrayList<>();
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
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public List<Cell> getCells() {
		return cells;
	}
	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public String getCombinedCellSourcesAsString() {
		StringBuilder sb = new StringBuilder();
		cells.stream().forEach(c -> sb.append(c.getSource()+" "));
		return sb.toString();
	}
	
	public String getCombinedMetadataAsString() {
		StringBuilder sb = new StringBuilder();
		metadata.entrySet().stream().forEach(md -> sb.append(md.getKey()+" ")); //add task metadata keys
		metadata.entrySet().stream().forEach(md -> sb.append(md.getValue()+" ")); //add task metadata values
		return sb.toString();
	}
}
