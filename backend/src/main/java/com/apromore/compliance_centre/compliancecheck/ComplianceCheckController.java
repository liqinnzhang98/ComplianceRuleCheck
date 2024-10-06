package com.apromore.compliance_centre.compliancecheck;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.apromore.compliance_centre.compliancerules.ComplianceRulesDto;
import com.apromore.compliance_centre.compliancerules.ComplianceRulesModel;
import com.apromore.compliance_centre.response.Response;
import com.apromore.compliance_centre.response.ResponseError;

@RestController
@RequestMapping("/api")
public class ComplianceCheckController {
    @Autowired
    ComplianceCheckService complianceCheckService;

    @GetMapping("/processes/{processId}/report")
    public Response<ComplianceCheckDto>getLastestReportForProcess(@PathVariable long processId) {
        List<ComplianceCheckDto> reports = complianceCheckService.getAllComplianceChecksForProcess(processId);
        Optional<ComplianceCheckDto> reportLatest = reports.stream().max(Comparator.comparing(v -> v.getRunAt()));
        if (reportLatest.isPresent()) {
            reportLatest.get().setReport(complianceCheckService.getReportResults(reportLatest.get().getId()));
            return Response.ok(reportLatest.get());
        }
        return Response.notFound(List.of(new ResponseError("Report", "Report not found")));
    }

    @GetMapping("/processes/{processId}/reports")
    public Response<List<ComplianceCheckDto>>getAllReportsForProcess(@PathVariable long processId) {
        return Response.ok(complianceCheckService.getAllComplianceChecksForProcess(processId));
    }

    @GetMapping("/processes/{processId}/reports/{reportId}")
    public Response<ComplianceCheckDto>getReport(@PathVariable long processId, @PathVariable long reportId) {
        ComplianceCheckDto report = complianceCheckService.getComplianceCheckById(reportId);
        report.setReport(complianceCheckService.getReportResults(report.getId()));
        return Response.ok(report);
    }

    @GetMapping("/reports")
    public Response<List<ComplianceCheckDto>>getAllReports() {
        return Response.ok(complianceCheckService.getAllComplianceChecks());
    }

    @PostMapping("processes/{processId}/check-compliance")
    public Response<ComplianceCheckDto>checkProcessCompliance(@PathVariable long processId, @RequestBody List<ComplianceRulesDto> allRules) {
        if (allRules.isEmpty() || allRules == null) {
            return Response.badRequest(List.of(new ResponseError("Report", "Rules not found")));
        }
        return Response.ok(complianceCheckService.checkProcessCompliance(processId, allRules));
    }
}
