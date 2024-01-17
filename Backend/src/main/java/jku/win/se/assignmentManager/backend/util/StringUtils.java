package jku.win.se.assignmentManager.backend.util;

import java.util.List;

public class StringUtils {
	public static boolean isEmptyOrNull(String s) {
		if(s == null || s.trim().length() <= 0) {
			return true;
		}
		return false;
	}
	public static String combine(List<String> list) {
		String res = "";
		for(String s : list) {
			res = res + s;
		}
		return res;
	}
}
