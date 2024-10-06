package com.apromore.compliance_centre.utils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.apromore.compliance_centre.process.attributes.AttributeDataType;

public class Utils {
	public static String toValidColumnName(String input) {
		if (input == null) {
			throw new IllegalArgumentException("Input cannot be null");
		}

		// Remove any non-alphanumeric character, but keep underscores
		String sanitized = input.replaceAll("[^a-zA-Z0-9_]", "");

		// If the result starts with a digit, prepend with an underscore
		if (sanitized.matches("^\\d.*")) {
			sanitized = "_" + sanitized;
		}

		return sanitized.toLowerCase();
	}

	public static String toValidTableName(String input) {
		if (input == null || input.trim().isEmpty()) {
			throw new IllegalArgumentException("Input cannot be null or empty");
		}

		// Replace any non-alphanumeric characters with an underscore
		String sanitized = input.replaceAll("[^a-zA-Z0-9]", "_");

		// If the name starts with a digit, prefix it with an underscore
		if (Character.isDigit(sanitized.charAt(0))) {
			sanitized = "_" + sanitized;
		}

		// Limit the length to 63, accounting for the possible additional underscore
		if (sanitized.length() > 63) {
			sanitized = sanitized.substring(0, 63);
		}

		return sanitized.toLowerCase();
	}


	public static Object sanitizeDatabaseValue(String value, AttributeDataType dataType) {
		if (value == null) {
			return null;
		}

		switch (dataType) {
			case TEXT:
				return value;
			case INT:
				return Integer.parseInt(value);
			case FLOAT:
				return Float.parseFloat(value);
			case TIMESTAMP:
				// You'll have to implement the conversion here based on your timestamp format
				return value;
			default:
				throw new IllegalArgumentException("Unknown data type: " + dataType);
		}
	}


	// public static <S, D> List<D> mapObjects(List<S> source, Class<D> destinationClass) {
	// return source.stream().map(element -> map(element, destinationClass))
	// .collect(Collectors.toList());
	// }

	public static <S, D> List<D> mapList(List<S> source, Function<S, D> mapper) {
		return source.stream().map(mapper).collect(Collectors.toList());
	}

}
