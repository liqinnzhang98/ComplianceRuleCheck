package com.apromore.compliance_centre.compliancerules.templates;

import com.apromore.compliance_centre.response.Response;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/compliance-rule-templates")
public class TemplateController {

    @Autowired
    TemplateService templateService;

    @Value("${recommender.hostname:localhost}")
    private String recommenderHostname;

    @Value("${recommender.port:8000}")
    private String recommenderPort;

    @GetMapping
    public Response<List<TemplateDto>> getAllTemplates(
        @RequestParam("control_description") Optional<String> controlDescription
    ) {
        List<TemplateDto> templates = templateService.getAllTemplates();

        // If called for the first time, import templates
        // if (templates == null || templates.isEmpty()) {
        templates = templateService.importTemplates();
        // }

        // if passing control description, fetch recommendations and integrate into template DTO
        try {
            if (controlDescription.isPresent()) {
                String recommenderURL =
                    "http://" +
                    recommenderHostname +
                    ":" +
                    recommenderPort +
                    "?control_description=" +
                    controlDescription.get();
                RestTemplate restTemplate = new RestTemplate();
                TemplateRecommendationDto recommendations = restTemplate.getForObject(
                    recommenderURL,
                    TemplateRecommendationDto.class
                );
                if (recommendations != null) {
                    for (TemplateDto template : templates) {
                        float recommendation = recommendations
                            .getRecommendations()
                            .get(template.getCategory() + ": " + template.getDescription());
                        template.setRecommendation(recommendation);
                    }
                }
            }
        } catch (Exception e) {

        }
        return Response.ok(templates);
    }

    @GetMapping("/{id}")
    public Response<TemplateDto> getTemplate(@PathVariable("id") Long id) {
        TemplateDto template = templateService.getTemplate(id);
        if (template == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "template not found");
        }
        return Response.ok(template);
    }

    @GetMapping("/category/{category}")
    public Response<List<TemplateDto>> getTemplatesByCategory(@PathVariable("category") String categoryString) {
        try {
            categoryString = categoryString.toUpperCase();
            TemplateCategory category = TemplateCategory.valueOf(categoryString);
            return Response.ok(templateService.getTemplatesByCategory(category));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "category not found");
        }
    }
}
