package com.apromore.compliance_centre.compliancerules;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apromore.compliance_centre.response.Response;
import com.apromore.compliance_centre.response.ResponseError;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ComplianceRulesController {
    @Autowired
    private ComplianceRulesService rulesService;

    ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/compliance-rules")
    public Response<ComplianceRulesDto> saveComplianceRules(@RequestBody @Valid ComplianceRulesDto dto) {
        return Response.ok(rulesService.saveComplianceRules(dto));
    }

    @PostMapping("/compliance-rules/{id}")
    public Response<ComplianceRulesDto> editComplianceRules(@PathVariable long id,
            @RequestBody @Valid ComplianceRulesDto dto) {
        return Response.ok(rulesService.updateComplianceRules(dto, id));
    }

    @GetMapping("/processes/{id}/compliance-rules")
    public Response<List<ComplianceRulesDto>> getComplianceRulesByProcess(@PathVariable Long id) {
        return Response.ok(rulesService.getComplianceRulesByProcess(id));
    }

    @GetMapping("/control/{id}/compliance-rules")
    public Response<List<ComplianceRulesDto>> getComplianceRulesByControl(@PathVariable Long id) {
        return Response.ok(rulesService.getComplianceRulesByControl(id));
    }

    @DeleteMapping("/compliance-rules/{id}")
    public Response<String> deleteComplianceRuleById(@PathVariable Long id) {
        if (rulesService.existsById(id)) {
            rulesService.deleteComplianceRuleById(id);
            return Response.ok("Compliance rule with ID " + id + " has been deleted.");
        } else {
            return Response.notFound(List.of(new ResponseError("Compliance Rule", "Compliance rule not found")));
        }
    }

}