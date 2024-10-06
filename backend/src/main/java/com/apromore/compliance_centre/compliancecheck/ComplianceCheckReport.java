package com.apromore.compliance_centre.compliancecheck;

import com.apromore.compliance_centre.compliancerules.ComplianceRulesDto;
import com.apromore.compliance_centre.riskobligationregister.ControlDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComplianceCheckReport {

    private record ReportStatistic(String caseId, Integer value) {}

    public record ReportBreaches(ComplianceRulesDto rule, ControlDto control, List<String> breachedCaseIds) {}

    private record ReportStatsModel(ReportStatistic max, ReportStatistic min, double average) {}

    int totalCases;
    int totalBreachedCases;
    ReportStatsModel stats;
    List<ReportBreaches> breaches;

    public ComplianceCheckReport(
        int totalCases,
        int totalBreachedCases,
        String maxCaseId,
        int maxValue,
        String minCaseId,
        int minValue,
        double average,
        List<ReportBreaches> breaches
    ) {
        this.totalCases = totalCases;
        this.totalBreachedCases = totalBreachedCases;
        this.stats =
            new ReportStatsModel(
                new ReportStatistic(maxCaseId, maxValue),
                new ReportStatistic(minCaseId, minValue),
                average
            );
        this.breaches = breaches;
    }
}
