package com.apromore.compliance_centre.riskobligationregister;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import com.apromore.compliance_centre.riskobligationregister.RiskObligationModel;

public class CSVHelper {
    public static String TYPE = "text/csv";

    // check if the file is CSV
    public static boolean hasCSVFormat(MultipartFile file) {

        return TYPE.equals(file.getContentType());
    }

    public static List<RiskObligationModel> csvToRiskObligations(InputStream is) {
        // Ensure that the csv file uploaded is in UTF-8 format!!!
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT
                        .withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<RiskObligationModel> risk_obligations = new ArrayList<RiskObligationModel>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {

                // define the risk/obligation type
                String risk_obligation_type =
                        csvRecord.get("ID").startsWith("R") ? "risk" : "obligation";

                // get the control for that risk/obligation
                List<ControlModel> controls = new ArrayList<ControlModel>();
                ControlModel control = new ControlModel(csvRecord.get("Type"),
                        csvRecord.get("Control Name"), csvRecord.get("Control Description"));
                controls.add(control);

                // create the risk/obligation
                RiskObligationModel risk_obligation = new RiskObligationModel(risk_obligation_type,
                        // "risk",
                        csvRecord.get("Name"), csvRecord.get("Description"),
                        csvRecord.get("Category"), csvRecord.get("Sub-Category"));

                // add the controls to that risk/obligation
                risk_obligation.setControls(controls);
                risk_obligations.add(risk_obligation);
            }

            return risk_obligations;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    public static Map<String, String> csvToMap(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        // Check if file has .csv extension
        if (!file.getOriginalFilename().endsWith(".csv")) {
            throw new IllegalArgumentException("File is not a valid CSV");
        }

        HashMap<String, String> resultMap = new HashMap<>();

        try (Reader reader = new InputStreamReader(file.getInputStream());
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            for (CSVRecord record : csvParser) {
                if (record.size() < 2) {
                    continue;
                }

                String key = record.get(0);
                String value = record.get(1);

                resultMap.put(key, value);
            }
        }

        return resultMap;
    }

}
