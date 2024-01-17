package jku.win.se.assignmentManager.backend.dto;

import java.util.List;

public class Output {

	private String name;
	private String output_type;
	private List<String> text;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOutput_type() {
		return output_type;
	}
	public void setOutput_type(String output_type) {
		this.output_type = output_type;
	}
	public List<String> getText() {
		return text;
	}
	public void setText(List<String> text) {
		this.text = text;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append("\"name\": \"" + name + "\",\n");
		sb.append("\"output_type\": \"" + output_type + "\",\n");
		sb.append("\"text\": [\n");
		for(int i=1; i<=text.size(); i++) {
			sb.append("\"" + text.get(i-1).replace("\n", "") + "\"");
			if(i<text.size()) {
				sb.append(",");
			}
			sb.append("\n");
		}
		sb.append("]\n");
		sb.append("}\n");
		return sb.toString();
	}
}
