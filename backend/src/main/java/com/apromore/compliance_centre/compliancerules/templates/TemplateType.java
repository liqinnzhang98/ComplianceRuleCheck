package com.apromore.compliance_centre.compliancerules.templates;

public enum TemplateType {
    CONTROL_FLOW_PRECEDES("Precedes"),
    CONTROL_FLOW_DIRECTLY_FOLLOWED_BY("Directly Followed By"),
    CONTROL_FLOW_EVENTUALLY_FOLLOWED_BY("Eventually Followed By"),
    CONTROL_FLOW_EXISTS("Exists"),
    RESOURCE_PERFORMED_BY("Performed By"),
    RESOURCE_SEGREGATED_FROM("Segregated From"),
    RESOURCE_USER_SEGREGATED_FROM("User Segregated From"),
    RESOURCE_BOUNDED_WITH("Bounded with"),
    RESOURCE_ROLE_BOUNDED_WITH("Role Bounded With"),
    TIME_WITHIN_LEADS_TO("Within - Leads To"),
    TIME_WITHIN_TASK_TIME("Within - Task Time"),
    TIME_AT_LEAST_AFTER("At Least After"),
    TIME_EXACTLY_AT("Exactly At"),
    TIME_EXACTLY_AFTER("Exactly After"),
    DATA_NOT_EQUAL("Does not equal"),
    DATA_EQUALS("Equals"),
    DATA_IS_GREATER_THAN("Is greater than"),
    DATA_IS_GREATER_THAN_OR_EQUALS("Is greater than or equals"),
    DATA_IS_LESS_THAN("Is less than"),
    DATA_IS_LESS_THAN_OR_EQUALS("Is less than or equals");

    public final String label;

    private TemplateType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public static TemplateType fromString(String label) {
        for (TemplateType type : TemplateType.values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        return null;
    }
}
