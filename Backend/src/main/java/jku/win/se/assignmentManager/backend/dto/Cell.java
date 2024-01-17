package jku.win.se.assignmentManager.backend.dto;

import java.util.List;
import java.util.Map;

import jku.win.se.assignmentManager.backend.enumeration.CellSourceType;

public class Cell {

	private CellSourceType type;
	private String source;
	private Map<String, String> metadata;
	private List<Output> outputs;
	
	public Cell() {}
	public Cell(String source) {
		this.source = source;
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	public CellSourceType getType() {
		return type;
	}

	public void setType(CellSourceType type) {
		this.type = type;
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
	
}
