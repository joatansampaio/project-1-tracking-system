package com.metrostate.projectone.utils;

import org.json.simple.JSONObject;

public class JsonHelper {
	public static String getString(Object object, String property) {
		var item = Validate(object, property);
		if (item == null) return null;

		return item.get(property).toString();
	}

	public static Float getFloat(Object object, String property) {
		var item = Validate(object, property);
		if (item == null) return null;

		return ((Number) item.get(property)).floatValue();
	}

	public static Long getLong(Object object, String property) {
		var item = Validate(object, property);
		if (item == null) return null;

		return ((Number) item.get(property)).longValue();
	}

	private static JSONObject Validate(Object object, String property) {
		if (object == null) {
			return null;
		}
		var item = (JSONObject) object;
		if (item.get(property) == null) {
			return null;
		}
		return item;
	}
}
