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

	// https://stackoverflow.com/questions/4105795/pretty-print-json-in-java
    /**
     * This method is from the link above (it has been modified slightly).
     * simple-json does not have a built-in prettifier, so we had to find it elsewhere.
     * We could change the main JSON library to something like GSON or a similar alternative,
     * but I'm afraid it's too late for that.
     *
     * @param jsonString The JSON string to be prettified.
     * @return A formatted JSON string.
     */

    public static String prettify(String jsonString) {
        StringBuilder prettyJSONBuilder = new StringBuilder();
        int indentLevel = 0;
        boolean inQuote = false;
        for (char charFromUnformattedJson : jsonString.toCharArray()) {
            switch (charFromUnformattedJson) {
                case '"':
                    // switch the quoting status
                    inQuote = !inQuote;
                    prettyJSONBuilder.append(charFromUnformattedJson);
                    break;
                case ' ':
                    // For space: ignore the space if it is not being quoted.
                    if (inQuote) {
                        prettyJSONBuilder.append(charFromUnformattedJson);
                    }
                    break;
                case '{':
                case '[':
                    // Starting a new block: increase the indent level
                    prettyJSONBuilder.append(charFromUnformattedJson);
                    indentLevel++;
                    indentNewLine(indentLevel, prettyJSONBuilder);
                    break;
                case '}':
                case ']':
                    // Ending a new block; decrease the indent level
                    indentLevel--;
                    indentNewLine(indentLevel, prettyJSONBuilder);
                    prettyJSONBuilder.append(charFromUnformattedJson);
                    break;
                case ',':
                    // Ending a json item; create a new line after
                    prettyJSONBuilder.append(charFromUnformattedJson);
                    if (!inQuote) {
                        indentNewLine(indentLevel, prettyJSONBuilder);
                    }
                    break;
                default:
                    prettyJSONBuilder.append(charFromUnformattedJson);
            }
        }
        return prettyJSONBuilder.toString();
    }

    private static void indentNewLine(int indentLevel, StringBuilder stringBuilder) {
        stringBuilder.append("\n");
        stringBuilder.append("  ".repeat(Math.max(0, indentLevel)));
    }
}
