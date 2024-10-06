package com.apromore.compliance_centre.compliancerules.templates;

public enum TemplateCategory {
    CONTROL_FLOW("Control Flow"),
    RESOURCE("Resource"),
    TIME("Time"),
    DATA("Data");

    public final String label;

    private TemplateCategory(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
