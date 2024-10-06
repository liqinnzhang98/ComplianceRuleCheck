package com.apromore.compliance_centre.riskobligationregister;

import com.apromore.compliance_centre.compliancerules.templates.TemplateDto;
import com.apromore.compliance_centre.compliancerules.templates.TemplateService;
import com.apromore.compliance_centre.compliancerules.templates.TemplateServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RiskObligationServiceImpl implements RiskObligationService {

    @Autowired
    private RiskObligationRepository riskObligationRepository;

    @Autowired
    private ControlRepository controlRepository;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private ModelMapper modelMapper;

    private HashMap<Long, TemplateDto> templatesMap;

    private HashMap<Long, TemplateDto> getTemplatesMap() {
        if (templatesMap == null || templatesMap.isEmpty()) {
            templatesMap = templateService.getAllTemplatesMap();
        }
        return templatesMap;
    }

    public RiskObligationServiceImpl(RiskObligationRepository riskObligationRepository,
            ControlRepository controlRepository,
            ModelMapper modelMapper,
            TemplateService templateService) {
        this.controlRepository = controlRepository;
        this.riskObligationRepository = riskObligationRepository;
        this.modelMapper = modelMapper;
        this.templateService = templateService;
    }

    @Override
    public List<ControlDto> getAllControls() {
        return StreamSupport
            .stream(controlRepository.findAll().spliterator(), false)
            .map(control -> controlModelMapper(control))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ControlDto getControlById(long controlId) {
        Optional<ControlModel> control = controlRepository.findById(controlId);
        if (control.isPresent()) return controlModelMapper(control.get());
        return null;
    }

    @Override
    public ControlDto createNewControl(ControlDto controlDto) {
        ControlModel control = modelMapper.map(controlDto, ControlModel.class);

        if (controlDto.getTemplates() != null) {
            control.setMappedTemplateIds(
                controlDto
                    .getTemplates()
                    .stream()
                    .map(templates -> templates.stream().map(TemplateDto::getId).collect(Collectors.toList()))
                    .collect(Collectors.toList())
            );
        }

        return controlModelMapper(controlRepository.save(control));
    }

    @Override
    public List<RiskObligationDto> getAllRiskObligations() {
        return StreamSupport
            .stream(riskObligationRepository.findAll().spliterator(), false)
            .map(riskObligation -> riskObligationModelMapper(riskObligation))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public RiskObligationDto getRiskObligationById(long riskObligationId) {
        Optional<RiskObligationModel> riskObligation = riskObligationRepository.findById(riskObligationId);
        if (riskObligation.isPresent()) return modelMapper.map(riskObligation.get(), RiskObligationDto.class);
        return null;
    }

    @Override
    public RiskObligationDto createNewRiskObligation(RiskObligationDto riskObligationDto) {
        List<ControlModel> controls = new ArrayList<>();
        for (ControlDto control : riskObligationDto.getControls()) {
            Optional<ControlModel> controlModel = controlRepository.findById(control.getId());
            if (controlModel.isPresent()) {
                controls.add(controlModel.get());
            }
        }

        RiskObligationModel riskObligation = new RiskObligationModel()
            .setName(riskObligationDto.getName())
            .setType(riskObligationDto.getType())
            .setDescription(riskObligationDto.getDescription())
            .setCategory(riskObligationDto.getCategory())
            .setControls(controls);

        if (riskObligationDto.getSubCategory() != null) {
            riskObligation.setSubCategory(riskObligationDto.getSubCategory());
        }

        return riskObligationModelMapper(riskObligationRepository.save(riskObligation));
    }

    ControlDto controlModelMapper(ControlModel model) {
        ControlDto dto = modelMapper.map(model, ControlDto.class);
        var templatesMap = getTemplatesMap();
        var mappedTemplateId = model.getMappedTemplateIds();

        if (mappedTemplateId != null) {
            dto.setTemplates(
                mappedTemplateId
                    .stream()
                    .map(ids -> {
                        return ids.stream().map(id -> templatesMap.get(id)).collect(Collectors.toList());
                    })
                    .collect(Collectors.toList())
            );
        }

        return dto;
    }

    RiskObligationDto riskObligationModelMapper(RiskObligationModel model) {
        RiskObligationDto dto = modelMapper.map(model, RiskObligationDto.class);
        dto.setControls(
            model.getControls().stream().map(control -> controlModelMapper(control)).collect(Collectors.toList())
        );

        return dto;
    }

    @Override
    public ControlDto updateControl(long controlId, ControlDto controlDto) {
        ControlModel control = modelMapper.map(controlDto, ControlModel.class);
        control.setId(controlId);

        if (controlDto.getTemplates() != null) {
            control.setMappedTemplateIds(
                controlDto
                    .getTemplates()
                    .stream()
                    .map(templates -> templates.stream().map(TemplateDto::getId).collect(Collectors.toList()))
                    .collect(Collectors.toList())
            );
        }

        return controlModelMapper(controlRepository.save(control));
    }
}