package io.github.dantetam.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonProcessor {

	/*
	 * Convert a raw JSON string such as from an HTTP request to a deep Java object (a JsonElement).
	 */
	public static JsonElement parseStringToObject(String json) {
		JsonParser parser = new JsonParser();
        // The JsonElement is the root node. It can be an object, array, null or
        // java primitive.
        JsonElement element = parser.parse(json);
        return element;
	}
	
}
