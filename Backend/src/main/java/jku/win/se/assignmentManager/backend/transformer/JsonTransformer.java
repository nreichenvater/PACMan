package jku.win.se.assignmentManager.backend.transformer;

import jku.win.se.assignmentManager.backend.controller.ServerController;
import spark.ResponseTransformer;


public class JsonTransformer implements ResponseTransformer {

	public String render(Object model) throws Exception {
		return ServerController.GSON.toJson(model);
	}
}