package com.metrostate.projectone.utils;

public class Printer {

	// ANSI codes to change the console color, use reset it to default.
	public static final String RESET = "\u001B[0m";

	public enum Color {
		RED("\u001B[31m"),
		YELLOW("\u001B[33m"),
		BLUE("\u001B[34m"),
		PURPLE("\u001B[35m"),
		CYAN("\u001B[36m");

		private final String code;

		Color(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	public static void println(String message, Color type, boolean reset) {
		printInner(message, type, reset);
	}

	public static void println(String message, Color type) {
		printInner(message, type, true);
	}

	public static void println(String message) {
		System.out.println(message);
	}

	public static void print(String message) {
		System.out.print(message);
	}

	private static void printInner(String message, Color type, boolean reset) {
		System.out.println(type.getCode() + message + (reset ? RESET : ""));
	}
}
