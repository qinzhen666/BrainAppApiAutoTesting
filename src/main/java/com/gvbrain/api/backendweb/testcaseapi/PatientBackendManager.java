package com.gvbrain.api.backendweb.testcaseapi;

import com.gvbrain.api.ApiToken;
import io.restassured.response.Response;

import java.util.HashMap;

public class PatientBackendManager extends ApiToken {
    private String tokenPattern = "brainBackend";

    public Response selectPatient(){
        return null;
    }

    public Response searchPatientByName(String patientName){
        HashMap<String,Object> map = new HashMap<>();
        map.put("patientName",patientName);
        return getResponseFromHar(
                "/data/backendweb/patientBackendManger/restPatient.har.json",
                ".*rest/backend/patients.*",
                map,
                tokenPattern
        );
    }
}
