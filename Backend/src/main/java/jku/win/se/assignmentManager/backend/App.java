package jku.win.se.assignmentManager.backend;

import static spark.Spark.before;
import static spark.Spark.options;

import jku.win.se.assignmentManager.backend.controller.ServerController;

public class App {

	public static void main(String[] args) {
		new ServerController().start();
		
		options("/*",
		        (request, response) -> {

		            String accessControlRequestHeaders = request
		                    .headers("Access-Control-Request-Headers");
		            if (accessControlRequestHeaders != null) {
		                response.header("Access-Control-Allow-Headers",
		                        accessControlRequestHeaders);
		            }

		            String accessControlRequestMethod = request
		                    .headers("Access-Control-Request-Method");
		            if (accessControlRequestMethod != null) {
		                response.header("Access-Control-Allow-Methods",
		                        accessControlRequestMethod);
		            }

		            return "OK";
		        });
		
		before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
	}
}
