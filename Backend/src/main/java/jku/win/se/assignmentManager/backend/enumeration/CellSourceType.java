package jku.win.se.assignmentManager.backend.enumeration;

import com.google.gson.annotations.SerializedName;

public enum CellSourceType {
	@SerializedName("markdown")
	MARKDOWN,
	@SerializedName("code")
	CODE,
	@SerializedName("raw")
	RAW
}
