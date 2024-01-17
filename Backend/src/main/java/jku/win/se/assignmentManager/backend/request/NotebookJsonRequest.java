package jku.win.se.assignmentManager.backend.request;

import java.util.List;

import jku.win.se.assignmentManager.backend.dto.JsonCell;

public class NotebookJsonRequest {
	
	//mapping object for notebook; metadata not necessary 
	
	private List<JsonCell> cells;
	private int nbformat;
	private int nbformat_minor;
	public List<JsonCell> getCells() {
		return cells;
	}
	public void setCells(List<JsonCell> cells) {
		this.cells = cells;
	}
	public int getNbformat() {
		return nbformat;
	}
	public void setNbformat(int nbformat) {
		this.nbformat = nbformat;
	}
	public int getNbformat_minor() {
		return nbformat_minor;
	}
	public void setNbformat_minor(int nbformat_minor) {
		this.nbformat_minor = nbformat_minor;
	}

}
