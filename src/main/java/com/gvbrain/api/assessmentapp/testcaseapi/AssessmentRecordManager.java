package com.gvbrain.api.assessmentapp.testcaseapi;

import com.gvbrain.api.ApiToken;
import io.restassured.response.Response;

import java.util.HashMap;

public class AssessmentRecordManager extends ApiToken {

    private String tokenPattern = "brainPlatform";

    /**
     * 上传图片,
     * 1、有post queryParam的话需要加入_postPara参数作为判断标识
     * 2、上传文件需要将图片地址加入_multiPath参数中
     */
    public Response uploadFile(String filePath){
        HashMap<String,Object> map = new HashMap<>();
        map.put("_postPara",true);
        map.put("sequence","0");
        map.put("_multiPath",filePath);
        return getResponseFromYaml(
                "/api/assessmentapp/assessmentRecordManager/uploadPic.yaml",
                map,
                tokenPattern
        );
    }

    /**
     * 新增测评报告
     */
    public Response addRecord(HashMap<String,Object> map){
        map.put("_file", "/data/assessmentapp/assessmentRecordManager/addRecord.json");
        return getResponseFromYaml(
                "/api/assessmentapp/assessmentRecordManager/addRecord.yaml",
                map,
                tokenPattern
        );
    }

    /**
     * 查询测评报告
     */
    public Response searchRecord(Integer patientUid){
        HashMap<String,Object> map = new HashMap<>();
        map.put("patientUid",patientUid);
        map.put("pageNumber",0);
        return getResponseFromYaml(
                "/api/assessmentapp/assessmentRecordManager/searchRecord.yaml",
                map,
                tokenPattern
        );
    }
}
