package com.gvbrain.appApi.assessmentapp.testcaseapi;

import com.gvbrain.appApi.ApiToken;
import io.restassured.response.Response;

import java.io.File;
import java.util.HashMap;

public class AssessmentRecordManager extends ApiToken {

    private String tokenPattern = "brainPlatform";

    /**
     * 上传图片
     */
    public Response uploadFile(String filePath){
        // /Users/qinzhen/Documents/TestDev/APITest/BrainAppApiAutoTesting/src/main/resources/data/assessmentapp/assessmentRecordManager/CTD4.jpg
        HashMap<String,Object> map = new HashMap<>();
        map.put("_postPara",true);
        map.put("sequence","0");
        map.put("_multiPath",filePath);
        return getResponseFromYaml(
                "/api/brainPFApp/assessmentRecordManager/uploadPic.yaml",
                map,
                tokenPattern
        );
    }
}
