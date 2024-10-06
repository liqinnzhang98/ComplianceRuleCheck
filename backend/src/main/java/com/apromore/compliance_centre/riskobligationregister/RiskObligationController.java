package com.apromore.compliance_centre.riskobligationregister;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.apromore.compliance_centre.response.Response;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/riskObligation")
public class RiskObligationController {
    @Autowired
    private RiskObligationService riskObligationService;

    @Autowired
    private CSVService csvService;

    @GetMapping
    public Response<List<RiskObligationDto>> getAllRiskObligations() {
        return Response.ok(riskObligationService.getAllRiskObligations());
    }

    @PostMapping
    public Response<RiskObligationDto> createNewRiskObligation(@RequestBody @Valid RiskObligationDto riskObligationDto) {
        return Response.ok(riskObligationService.createNewRiskObligation(riskObligationDto));
    }

    @GetMapping("/{id}")
    public Response<RiskObligationDto> getRiskObligationById(@PathVariable long id) {
        RiskObligationDto riskObligationDto = riskObligationService.getRiskObligationById(id);
        if (riskObligationDto == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found");
        }
        return Response.ok(riskObligationDto);
    }

    @GetMapping("/control")
    public Response<List<ControlDto>> getAllControls() {
        return Response.ok(riskObligationService.getAllControls());
    }

    @PostMapping("/control")
    public Response<ControlDto> createNewControl(@RequestBody @Valid ControlDto controlDto) {
        return Response.ok(riskObligationService.createNewControl(controlDto));
    }

    @PutMapping("/control/{id}")
    public Response<ControlDto> editControl(@PathVariable long id, @RequestBody @Valid ControlDto controlDto) {
        return Response.ok(riskObligationService.updateControl(id, controlDto));
    }

    @GetMapping("/control/{id}")
    public Response<ControlDto> getControlById(@PathVariable long id) {
        ControlDto controlDto = riskObligationService.getControlById(id);
        if (controlDto == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found");
        }
        return Response.ok(controlDto);
    }

    @PostMapping("/upload")
    public Response<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";

        if (CSVHelper.hasCSVFormat(file)) {
            try {
                csvService.save(file);
                return Response.ok("file saved successfully: " + file.getOriginalFilename() + "!");
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!" + e.getMessage();
                return Response.ok(message);
            }
        }

        message = "Please upload a csv file!";
        return Response.ok(message);
    }
}