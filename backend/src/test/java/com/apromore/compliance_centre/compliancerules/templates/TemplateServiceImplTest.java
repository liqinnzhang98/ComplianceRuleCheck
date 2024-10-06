package com.apromore.compliance_centre.compliancerules.templates;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldDto;
import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldModel;
import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldType;
import com.apromore.compliance_centre.process.attributes.AttributeType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

class TemplateServiceImplTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TemplateServiceImpl templateService;

    private TemplateModel mockTemplateModel;

    private TemplateDto mockTemplateDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockTemplateModel =
            new TemplateModel()
                .setId(1L)
                .setRule("Precedes")
                .setDescription("Task P must precede task Q")
                .setExample("A credit check task must precede a credit offer")
                .setCategory(TemplateCategory.CONTROL_FLOW)
                .setFormFields(
                    Arrays.asList(
                        new FormFieldModel()
                            .setName("task_p")
                            .setLabel("Task P")
                            .setType(FormFieldType.SELECT)
                            .setSelectFrom(AttributeType.ACTIVITY),
                        new FormFieldModel().setName("column_break").setType(FormFieldType.COLUMN_BREAK),
                        new FormFieldModel().setName("rule_text").setLabel("proceeds").setType(FormFieldType.TEXT),
                        new FormFieldModel().setName("column_break_2").setType(FormFieldType.COLUMN_BREAK),
                        new FormFieldModel()
                            .setName("task_q")
                            .setLabel("Task Q")
                            .setType(FormFieldType.SELECT)
                            .setSelectFrom(AttributeType.ACTIVITY)
                    )
                )
                .setFormFieldsOrder(Arrays.asList("task_p", "column_break", "rule_text", "column_break_2", "task_q"));

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

    @Test
    void testGetAllTemplates() {
        when(templateRepository.findAll()).thenReturn(Arrays.asList(mockTemplateModel));
        when(modelMapper.map(eq(mockTemplateModel), eq(TemplateDto.class))).thenReturn(new TemplateDto());

        List<TemplateDto> result = templateService.getAllTemplates();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllTemplatesMap() {
        when(templateRepository.findAll()).thenReturn(Arrays.asList(mockTemplateModel));
        when(modelMapper.map(eq(mockTemplateModel), eq(TemplateDto.class))).thenReturn(mockTemplateDto);

        HashMap<Long, TemplateDto> resultMap = templateService.getAllTemplatesMap();
        assertEquals(1, resultMap.size());
        assertTrue(resultMap.containsKey(1L));
    }

    @Test
    void testGetTemplatesByCategory() {
        TemplateCategory category = mockTemplateModel.getCategory();
        when(templateRepository.findByCategory(category)).thenReturn(Arrays.asList(mockTemplateModel));
        when(modelMapper.map(eq(mockTemplateModel), eq(TemplateDto.class))).thenReturn(new TemplateDto());

        List<TemplateDto> result = templateService.getTemplatesByCategory(category);
        assertEquals(1, result.size());
    }

    @Test
    void testGetTemplateFound() {
        long id = mockTemplateModel.getId();
        when(templateRepository.findById(id)).thenReturn(Optional.of(mockTemplateModel));
        when(modelMapper.map(eq(mockTemplateModel), eq(TemplateDto.class))).thenReturn(new TemplateDto());

        TemplateDto result = templateService.getTemplate(id);
        assertNotNull(result);
    }

    @Test
    void testGetTemplateNotFound() {
        long id = 1L;
        when(templateRepository.findById(id)).thenReturn(Optional.empty());

        TemplateDto result = templateService.getTemplate(id);
        assertNull(result);
    }

    @Test
    void testGetTemplatesFromFileIOException() throws IOException {
        String fileName = "classpath:data/compliance-rule-templates.json";
        Resource mockResource = mock(Resource.class);
        when(resourceLoader.getResource(fileName)).thenReturn(mockResource);
        when(mockResource.getFile()).thenThrow(new IOException());

        assertThrows(
            RuntimeException.class,
            () -> {
                templateService.importTemplates();
            }
        );
    }
}
