package com.apromore.compliance_centre.riskobligationregister;

import java.io.IOException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CSVService {
    @Autowired
    RiskObligationRepository repository;

    @Autowired
    ModelMapper modelMapper;

    public void save(MultipartFile file) {
        try {
            List<RiskObligationModel> risk_obligations = CSVHelper.csvToRiskObligations(file.getInputStream());
            repository.saveAll(risk_obligations);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public List<RiskObligationModel> getAllRiskObligations() {
        return (List<RiskObligationModel>) repository.findAll();
    }
}