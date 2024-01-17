package jku.win.se.assignmentManager.backend.dto;

import dev.morphia.annotations.Entity;

@Entity("Tag")
public class Tag extends AbstractEntity {
	
	private String tag;
	
	public Tag() {}
	
	public Tag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	@Override
	public String toString() {
		return tag;
	}
}
