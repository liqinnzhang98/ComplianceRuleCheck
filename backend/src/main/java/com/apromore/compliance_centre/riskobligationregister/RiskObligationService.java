package com.apromore.compliance_centre.riskobligationregister;

import java.util.List;

public interface RiskObligationService {
    /**
     * Return a list of all risks and obligations
     * 
     * @return
     */
    List<RiskObligationDto> getAllRiskObligations();

    /**
     * Get an existing riskObligation by it's id
     * 
     * @param riskObligationId
     * @return
     */
    RiskObligationDto getRiskObligationById(long riskObligationId);

    /**
     * Create a new empty riskObligation
     * 
     * @param riskObligationDto
     * @return
     */
    RiskObligationDto createNewRiskObligation(RiskObligationDto riskObligationDto);

    /**
     * Return a list of all controls
     * 
     * @return
     */
    List<ControlDto> getAllControls();

    /**
     * Get an existing control by it's id
     * 
     * @param controlId
     * @return
     */
    ControlDto getControlById(long controlId);

    /**
     * Create a new empty control
     * 
     * @param controlDto
     * @return
     */
    ControlDto createNewControl(ControlDto controlDto);

    /**
     * Update an existing control
     * 
     * @param controlDto
     * @return
     */
    ControlDto updateControl(long controlId, ControlDto controlDto);

}
