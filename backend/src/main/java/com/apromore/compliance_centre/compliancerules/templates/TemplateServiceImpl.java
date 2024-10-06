package com.apromore.compliance_centre.compliancerules.templates;

import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldDto;
import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldModel;
import com.apromore.compliance_centre.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<TemplateDto> getAllTemplates() {
        return StreamSupport
            .stream(templateRepository.findAll().spliterator(), false)
            .map(template -> templateModelMapper(template))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public HashMap<Long, TemplateDto> getAllTemplatesMap() {
        HashMap<Long, TemplateDto> templateMap = new HashMap<>();

        List<TemplateDto> templates = getAllTemplates();

        for (TemplateDto template : templates) {
            templateMap.put(template.getId(), template);
        }

        return templateMap;
    }

    @Override
    public List<TemplateDto> getTemplatesByCategory(TemplateCategory category) {
        return StreamSupport
            .stream(templateRepository.findByCategory(category).spliterator(), false)
            .map(template -> templateModelMapper(template))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public TemplateDto getTemplate(Long id) {
        Optional<TemplateModel> template = templateRepository.findById(id);
        if (template.isPresent()) return templateModelMapper(template.get());
        return null;
    }

    public List<TemplateDto> importTemplates() {
        List<TemplateDto> templates = getTemplatesFromFile();

        List<TemplateModel> templateModels = templates
            .stream()
            .map(template -> modelMapper.map(template, TemplateModel.class))
            .collect(Collectors.toList());

        for (TemplateModel templateModel : templateModels) {
            var formFields = templateModel.getFormFields();
            List<String> formFieldsOrder = new ArrayList<>();

            for (FormFieldModel formField : formFields) {
                formField.setTemplate(templateModel);
                formFieldsOrder.add(formField.getName());
            }

            templateModel.setFormFieldsOrder(formFieldsOrder);
        }

        templateRepository.deleteAll(templateModels);
        templateRepository.saveAll(templateModels);
        return getAllTemplates();
    }

    private List<TemplateDto> getTemplatesFromFile() {
        var fileName = "classpath:data/compliance-rule-templates.json";
        var file = resourceLoader.getResource(fileName);
        try {
            return objectMapper.readValue(file.getFile(), new TypeReference<List<TemplateDto>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error while reading templates from file", e);
        }
    }

    private TemplateDto templateModelMapper(TemplateModel model) {
        var templateDto = modelMapper.map(model, TemplateDto.class);
        // sort form fields by fields order
        var formFieldsMap = model.getFormFieldsMap();
        var sortedFormFields = new ArrayList<FormFieldDto>();

        for (String formFieldName : model.getFormFieldsOrder()) {
            sortedFormFields.add(modelMapper.map(formFieldsMap.get(formFieldName), FormFieldDto.class));
        }

        templateDto.setFormFields(sortedFormFields);

        return templateDto;
    }
}
