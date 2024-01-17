package jku.win.se.assignmentManager.backend.dto;

import java.util.List;
import java.util.Map;

import jku.win.se.assignmentManager.backend.enumeration.CellSourceType;

public class JsonCell {

	private CellSourceType cell_type;
	private List<String> source;
	private Map<String, String> metadata;
	private List<Output> outputs;
	private List<String> tags;
	
	public JsonCell(List<String> source) {
		this.source = source;
	}
	
	public List<String> getSource() {
		return source;
	}
	public void setSource(List<String> source) {
		this.source = source;
	}

	public CellSourceType getType() {
		return cell_type;
	}

	public void setType(CellSourceType type) {
		this.cell_type = type;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public List<Output> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<Output> outputs) {
		this.outputs = outputs;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
}

