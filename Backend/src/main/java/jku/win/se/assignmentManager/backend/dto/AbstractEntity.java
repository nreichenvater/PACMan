package jku.win.se.assignmentManager.backend.dto;

import java.util.Date;
import java.util.UUID;

import dev.morphia.annotations.Id;
import dev.morphia.annotations.PrePersist;

public abstract class AbstractEntity {
	@Id
	private String id;
	private Date createdAt;
	private long createdAtTimeStamp;
	private Date updatedAt;
	private long updatedAtTimeStamp;
	// may be empty
	private String creator;
	private String editor;
	public AbstractEntity() {
		this.id = UUID.randomUUID().toString();
		this.createdAt = new Date();
		this.createdAtTimeStamp = this.createdAt.getTime();
	}

	@PrePersist
	public void trackUpdate() {
		this.updatedAt = new Date();
		this.updatedAtTimeStamp = this.updatedAt.getTime();
	}
	
	public String getId() {
		return id;
	}

}