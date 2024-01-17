package jku.win.se.assignmentManager.backend.dto;

import org.bson.types.Binary;

import dev.morphia.annotations.Entity;

@Entity("TaskFile")
public class TaskFile extends AbstractEntity {
	
	private String fileType;
	private String fileName;
	private String stringContent;
	private Binary binContent;
	
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getStringContent() {
		return stringContent;
	}
	public void setStringContent(String stringContent) {
		this.stringContent = stringContent;
	}
	public Binary getBinContent() {
		return binContent;
	}
	public void setBinContent(Binary binContent) {
		this.binContent = binContent;
	}
	
}
