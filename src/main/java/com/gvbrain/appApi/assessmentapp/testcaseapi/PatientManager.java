package com.gvbrain.appApi.assessmentapp.testcaseapi;

import com.gvbrain.appApi.ApiToken;
import io.restassured.response.Response;

import java.util.HashMap;

public class PatientManager extends ApiToken {

    private String tokenPattern = "brainPlatform";

    public Response getPatientInfoByList(HashMap<String,Object> map){
        map.put("_file","/data/restPatientByList.json");
        return getResponseFromYaml(
                "/api/brainPFApp/patientManager/restPatientsList.yaml",
                map,
                tokenPattern
        );
    }

}
