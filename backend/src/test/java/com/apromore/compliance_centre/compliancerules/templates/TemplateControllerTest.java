package com.apromore.compliance_centre.compliancerules.templates;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldDto;
import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldType;
import com.apromore.compliance_centre.process.attributes.AttributeType;
import com.apromore.compliance_centre.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class TemplateControllerTest {

    @InjectMocks
    private TemplateController templateController;

    @Mock
    private TemplateService templateService;

    private MockMvc mockMvc;

    private static TemplateDto mockTemplateDto;

    @BeforeAll
    public static void beforeAll() {
        mockTemplateDto =
            new TemplateDto()
                .setId(1L)
                .setRule("Precedes")
                .setDescription("Task P must precede task Q")
                .setExample("A credit check task must precede a credit offer")
                .setCategory(TemplateCategory.CONTROL_FLOW.toString())
                .setFormFields(
                    Arrays.asList(
                        new FormFieldDto()
                            .setName("task_p")
                            .setLabel("Task P")
                            .setType(FormFieldType.SELECT)
                            .setSelectFrom(AttributeType.ACTIVITY),
                        new FormFieldDto().setName("column_break").setType(FormFieldType.COLUMN_BREAK),
                        new FormFieldDto().setName("rule_text").setLabel("proceeds").setType(FormFieldType.TEXT),
                        new FormFieldDto().setName("column_break_2").setType(FormFieldType.COLUMN_BREAK),
                        new FormFieldDto()
                            .setName("task_q")
                            .setLabel("Task Q")
                            .setType(FormFieldType.SELECT)
                            .setSelectFrom(AttributeType.ACTIVITY)
                    )
                );
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(templateController).build();
    }

    @Test
    public void getAllTemplates_ReturnsTemplates() throws Exception {
        when(templateService.getAllTemplates()).thenReturn(Collections.singletonList(mockTemplateDto));

        mockMvc.perform(get("/api/compliance-rule-templates")).andExpect(status().isOk());
    }

    @Test
    public void getAllTemplates_ImportsTemplatesIfNone() throws Exception {
        when(templateService.getAllTemplates()).thenReturn(Collections.emptyList());
        when(templateService.importTemplates()).thenReturn(Collections.singletonList(mockTemplateDto));

        mockMvc.perform(get("/api/compliance-rule-templates")).andExpect(status().isOk());
    }

    @Test
    public void getTemplate_ReturnsTemplate() throws Exception {
        when(templateService.getTemplate(1L)).thenReturn(mockTemplateDto);

        var result = mockMvc
            .perform(get("/api/compliance-rule-templates/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data").value(mockTemplateDto))
            .andReturn();

        System.out.println("result: " + result.getResponse().getContentAsString());
        System.out.println("result2: ");
    }

    @Test
    public void getTemplate_ReturnsNotFoundWhenTemplateMissing() throws Exception {
        when(templateService.getTemplate(1L)).thenReturn(null);

        mockMvc.perform(get("/api/compliance-rule-templates/1")).andExpect(status().isNotFound());
    }

    @Test
    public void getTemplateByCategory_ReturnsTemplates() throws Exception {
        when(templateService.getTemplatesByCategory(TemplateCategory.RESOURCE))
            .thenReturn(Collections.singletonList(mockTemplateDto));

        mockMvc.perform(get("/api/compliance-rule-templates/category/RESOURCE")).andExpect(status().isOk());
    }

    @Test
    public void getTemplateByCategory_ReturnsBadRequestOnInvalidCategory() throws Exception {
        mockMvc
            .perform(get("/api/compliance-rule-templates/category/INVALID_CATEGORY"))
            .andExpect(status().isBadRequest());
    }
}
