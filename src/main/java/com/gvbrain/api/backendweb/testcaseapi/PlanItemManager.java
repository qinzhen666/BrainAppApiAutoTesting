package com.gvbrain.api.backendweb.testcaseapi;

import com.gvbrain.api.ApiToken;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PlanItemManager extends ApiToken {
    private String tokenPattern = "brainBackend";

    /**
     * 查询测评方案信息
     * @param assessmentPlanName
     * @return
     */
    public Response queryPlan(String assessmentPlanName){
        HashMap<String,Object> map = new HashMap<>();
        map.put("assessmentPlanName",assessmentPlanName);
        return getResponseFromHar(
                "/data/backendweb/planitemManager/queryplan.har.json",
                ".*/rest/backend/assessmentPlan/query.*",
                map,
                tokenPattern
        );
    }

    /**
     * 编辑固定测评方案
     * @param map
     * @return
     */
    public Response updatePlan(HashMap<String,Object> map){
        return getResponseFromHar(
                "/data/backendweb/planitemManager/updateGudingPlan.har.json",
                ".*/rest/backend/assessmentPlan/update.*",
                map,
                tokenPattern
        );
    }

    /**
     * 新增固定测评方案，以下均为必填项
     * @param planDescribe
     * @param planName
     * @param classify
     * @param hospitalUids
     * @param items
     * @return
     */
    public Response insertPlan(String planDescribe, String planName, Integer classify, List<Integer> hospitalUids, List<Integer> items){
        HashMap<String,Object> map = new HashMap<>();
        map.put("assessmentPlanDescribe",planDescribe);
        map.put("assessmentPlanName",planName);
        map.put("classify",classify);
        map.put("hospitalUids",hospitalUids);
        map.put("items",items);
        return getResponseFromHar(
                "/data/backendweb/planitemManager/insertGudingPlan.har.json",
                ".*/rest/backend/assessmentPlan/insert.*",
                map,
                tokenPattern
        );
    }


    /**
     * 测评方案禁用:0|启用:1
     * @param status
     * @param uid
     * @return
     */
    public Response disablePlan(Integer status,Integer uid){
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",status);
        map.put("uid",uid);
        return getResponseFromHar(
                "/data/backendweb/planitemManager/disablePlan.har.json",
                ".*/rest/backend/assessmentPlan/disable.*",
                map,
                tokenPattern
        );
    }

}
