package com.apromore.compliance_centre.riskObligationRegister;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.apromore.compliance_centre.compliancerules.templates.TemplateCategory;
import com.apromore.compliance_centre.compliancerules.templates.TemplateDto;
import com.apromore.compliance_centre.compliancerules.templates.TemplateModel;
import com.apromore.compliance_centre.compliancerules.templates.TemplateServiceImpl;
import com.apromore.compliance_centre.compliancerules.templates.TemplateType;
import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldDto;
import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldModel;
import com.apromore.compliance_centre.compliancerules.templates.formfields.FormFieldType;
import com.apromore.compliance_centre.process.attributes.AttributeType;
import com.apromore.compliance_centre.riskobligationregister.ControlDto;
import com.apromore.compliance_centre.riskobligationregister.ControlModel;
import com.apromore.compliance_centre.riskobligationregister.ControlRepository;
import com.apromore.compliance_centre.riskobligationregister.RiskObligationDto;
import com.apromore.compliance_centre.riskobligationregister.RiskObligationModel;
import com.apromore.compliance_centre.riskobligationregister.RiskObligationRepository;
import com.apromore.compliance_centre.riskobligationregister.RiskObligationServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

public class RiskObligationServiceImplTest {

    private final RiskObligationRepository riskObligationRepository = mock(RiskObligationRepository.class);
    private final ControlRepository controlRepository = mock(ControlRepository.class);
    private final ModelMapper modelMapper = new ModelMapper();
    private final TemplateServiceImpl templateService = mock(TemplateServiceImpl.class);
    private final RiskObligationServiceImpl riskObligationService = new RiskObligationServiceImpl(
        riskObligationRepository,
        controlRepository,
        modelMapper,
        templateService
    );
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
    public void testGetAllControls() {
        List<ControlModel> controlModels = new ArrayList<>();
        controlModels.add(new ControlModel().setName("Control 1"));
        controlModels.add(new ControlModel().setName("Control 2"));
        when(controlRepository.findAll()).thenReturn(controlModels);

        List<ControlDto> controlDtos = riskObligationService.getAllControls();

        assertEquals(controlModels.size(), controlDtos.size());
        for (int i = 0; i < controlDtos.size(); i++) {
            ControlModel model = controlModels.get(i);
            ControlDto dto = controlDtos.get(i);
            assertEquals(model.getName(), dto.getName());
        }
    }

    @Test
    public void testGetControlByExistingId() {
        ControlModel controlModel = new ControlModel();
        controlModel.setId(1L);
        controlModel.setName("Test Control");
        when(controlRepository.findById(1L)).thenReturn(Optional.of(controlModel));

        ControlDto controlDto = riskObligationService.getControlById(1L);

        assertNotNull(controlDto);
        assertEquals(controlDto.getId(), controlModel.getId());
        assertEquals(controlDto.getName(), controlModel.getName());
    }

    @Test
    public void testGetControlByNonExistingId() {
        when(controlRepository.findById(2L)).thenReturn(Optional.empty());

        ControlDto controlDto = riskObligationService.getControlById(2L);

        assertNull(controlDto);
    }

    @Test
    public void testCreateNewControl() {
        ControlDto controlDto = new ControlDto();
        controlDto.setTemplates(Arrays.asList(Arrays.asList(new TemplateDto())));

        ControlModel controlModel = new ControlModel();
        controlModel.setId(1L);

        when(controlRepository.save(any(ControlModel.class))).thenReturn(controlModel);

        ControlDto resultDto = riskObligationService.createNewControl(controlDto);

        assertNotNull(resultDto);
        assertEquals(controlModel.getId(), resultDto.getId());
    }

    @Test
    public void testGetAllRiskObligations() {
        List<RiskObligationModel> riskObligationModels = new ArrayList<>();
        riskObligationModels.add(new RiskObligationModel().setName("Risk 1").setControls(List.of()));
        riskObligationModels.add(new RiskObligationModel().setName("Obligation 2").setControls(List.of()));
        when(riskObligationRepository.findAll()).thenReturn(riskObligationModels);

        List<RiskObligationDto> riskObligationDtos = riskObligationService.getAllRiskObligations();

        assertEquals(riskObligationModels.size(), riskObligationDtos.size());
        for (int i = 0; i < riskObligationDtos.size(); i++) {
            RiskObligationModel model = riskObligationModels.get(i);
            RiskObligationDto dto = riskObligationDtos.get(i);
            assertEquals(model.getName(), dto.getName());
        }
    }

    @Test
    public void testGetRiskObligationByExistingId() {
        RiskObligationModel riskObligationModel = new RiskObligationModel();
        riskObligationModel.setId(1L);
        riskObligationModel.setName("Test RiskObligation");
        when(riskObligationRepository.findById(1L)).thenReturn(Optional.of(riskObligationModel));

        RiskObligationDto riskObligationDto = riskObligationService.getRiskObligationById(1L);

        assertNotNull(riskObligationDto);
        assertEquals(riskObligationDto.getId(), riskObligationModel.getId());
        assertEquals(riskObligationDto.getName(), riskObligationModel.getName());
    }

    @Test
    public void testGetRiskObligationByNonExistingId() {
        when(riskObligationRepository.findById(2L)).thenReturn(Optional.empty());

        RiskObligationDto riskObligationDto = riskObligationService.getRiskObligationById(2L);

        assertNull(riskObligationDto);
    }

    @Test
    public void testCreateNewRiskObligation() {
        ControlDto controlDto1 = new ControlDto();
        controlDto1.setId(1L);
        ControlDto controlDto2 = new ControlDto();
        controlDto2.setId(2L);
        List<ControlDto> controlDtos = List.of(controlDto1, controlDto2);

        RiskObligationDto riskObligationDto = new RiskObligationDto();
        riskObligationDto.setName("Obligation");
        riskObligationDto.setType("Type");
        riskObligationDto.setDescription("Description");
        riskObligationDto.setCategory("Category");
        riskObligationDto.setControls(controlDtos);

        ControlModel controlModel1 = new ControlModel();
        controlModel1.setId(1L);
        ControlModel controlModel2 = new ControlModel();
        controlModel2.setId(2L);
        List<ControlModel> controlModels = List.of(controlModel1, controlModel2);
        when(controlRepository.findById(1L)).thenReturn(Optional.of(controlModel1));
        when(controlRepository.findById(2L)).thenReturn(Optional.of(controlModel2));

        RiskObligationModel riskObligationModel = new RiskObligationModel();
        riskObligationModel.setName("Obligation");
        riskObligationModel.setType("Type");
        riskObligationModel.setDescription("Description");
        riskObligationModel.setCategory("Category");
        riskObligationModel.setControls(controlModels);
        when(riskObligationRepository.save(any(RiskObligationModel.class))).thenReturn(riskObligationModel);

        RiskObligationDto result = riskObligationService.createNewRiskObligation(riskObligationDto);
        assertNotNull(result);
        assertEquals(controlDtos.size(), result.getControls().size());
        verify(riskObligationRepository, times(1)).save(riskObligationModel);
    }

    @Test
    public void testCreateNewRiskObligationWithoutControl() {
        List<ControlDto> controlDtos = List.of();

        RiskObligationDto riskObligationDto = new RiskObligationDto();
        riskObligationDto.setName("Obligation");
        riskObligationDto.setType("Type");
        riskObligationDto.setDescription("Description");
        riskObligationDto.setCategory("Category");
        riskObligationDto.setSubCategory("SubCategory");
        riskObligationDto.setControls(controlDtos);

        List<ControlModel> controlModels = List.of();

        RiskObligationModel riskObligationModel = new RiskObligationModel();
        riskObligationModel.setName("Obligation");
        riskObligationModel.setType("Type");
        riskObligationModel.setDescription("Description");
        riskObligationModel.setCategory("Category");
        riskObligationModel.setSubCategory("SubCategory");
        riskObligationModel.setControls(controlModels);
        when(riskObligationRepository.save(any(RiskObligationModel.class))).thenReturn(riskObligationModel);

        RiskObligationDto result = riskObligationService.createNewRiskObligation(riskObligationDto);
        assertNotNull(result);
        assertEquals(controlDtos.size(), result.getControls().size());
        verify(riskObligationRepository, times(1)).save(riskObligationModel);
    }
}
