package com.gvbrain.api.backendweb.testcaseapi;

import com.gvbrain.api.ApiToken;
import io.restassured.response.Response;
import java.util.HashMap;

public class RecordManager extends ApiToken {
    private String tokenPattern = "brainBackend";

    /**
     *根据患者id查询测评报告
     * @param patientId
     * @return
     */
    public Response findRecordById(Integer patientId){
        HashMap<String,Object> map = new HashMap<>();
        map.put("uid",patientId);
        return getResponseFromHar(
                "/data/backendweb/recordManager/findRecordById.har.json",
                ".*/rest/backend/patient/records.*",
                map,
                tokenPattern
        );
    }


    /**
     *根据医生id查询测评人次
     * @param doctorId
     * @return
     */
    public Response recordsCountByDocId(Integer doctorId){
        HashMap<String,Object> map = new HashMap<>();
        map.put("uid",doctorId);
        return getResponseFromYaml(
                "/api/backendweb/recordManger/recordsCountByDocId.yaml",
                map,
                tokenPattern
        );
    }

    /**
     * 查询测评报告
     * @param patientId
     * @param startTime
     * @return
     */
    public Response findRecordByStartTime(Integer patientId,String startTime){
        HashMap<String,Object> map = new HashMap<>();
        map.put("uid",patientId);
        map.put("startTime",startTime);
        return getResponseFromHar(
                "/",
                ".*/record/patient/record.*",
                map,
                tokenPattern
        );
    }


}
