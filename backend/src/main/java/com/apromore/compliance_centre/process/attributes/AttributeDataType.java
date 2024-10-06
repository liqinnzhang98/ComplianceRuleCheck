package com.apromore.compliance_centre.process.attributes;

public enum AttributeDataType {
	INT, FLOAT, TEXT, TIMESTAMP;

	@Override
	public String toString() {
		switch (this) {
			case INT:
				return "INT";
			case FLOAT:
				return "FLOAT";
			case TEXT:
				return "TEXT";
			case TIMESTAMP:
				return "TIMESTAMP";
			default:
				return "TEXT";
		}
	}
}
