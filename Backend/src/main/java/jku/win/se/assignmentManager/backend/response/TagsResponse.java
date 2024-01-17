package jku.win.se.assignmentManager.backend.response;

import java.util.List;

import jku.win.se.assignmentManager.backend.dto.Tag;

public class TagsResponse {

	private List<Tag> tags;
	
	public TagsResponse(List<Tag> tags) {
		this.tags = tags;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
}
