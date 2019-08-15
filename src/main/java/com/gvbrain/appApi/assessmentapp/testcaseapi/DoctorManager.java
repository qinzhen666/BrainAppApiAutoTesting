package com.gvbrain.appApi.assessmentapp.testcaseapi;

import com.gvbrain.appApi.ApiToken;
import io.restassured.response.Response;

public class DoctorManager extends ApiToken {
    private String tokenPattern = "brainPlatform";

    /**
     * 获取脑健康师信息,测评方案信息
     * @return
     */
    public Response getDoctorInfo(){
        return getResponseFromYaml(
                "/api/brainPFApp/doctorManager/restDoctor.yaml",
                null,
                tokenPattern
                );
    }
}
