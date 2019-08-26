package com.gvbrain.api.assessmentapp.testcase;

import com.gvbrain.api.ApiToken;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;

public class AssessmentPlanManager extends ApiToken {

    private String tokenPattern = "brainPlatform";

    /**
     * 查询当前登陆用户的自定义测评方案
     * @return
     */
    public Response getAssessmentPlan(){
        return getResponseFromYaml(
                "/api/assessmentapp/assessmentPlanManager/restAssessmentPlan.yaml",
                null,
                tokenPattern
        );
    }

    /**
     * #新增自定义测评方案
     * @param assessmentPlanDescribe 方案描述
     * @param assessmentPlanName 方案名称
     * @param items 测评项目UID集合
     * @return
     */
    public Response createAssessmentPlan(String assessmentPlanDescribe, String assessmentPlanName, List<Integer> items){
        HashMap<String,Object> map = new HashMap<>();
        map.put("assessmentPlanDescribe",assessmentPlanDescribe);
        map.put("assessmentPlanName",assessmentPlanName);
        map.put("items",items);
        map.put("_file", "/data/assessmentapp/assessmentPlanManager/createPlan.json");
        return getResponseFromYaml(
                "/api/assessmentapp/assessmentPlanManager/createAssessmentPlan.yaml",
                map,
                tokenPattern
        );
    }

    /**
     * 删除自定义测评方案
     * @param uid 方案uid
     * @return
     */
    public Response deleteAssessmentPlan(Integer uid){
        HashMap<String,Object> map = new HashMap<>();
        map.put("uid",uid);
        return getResponseFromYaml(
                "/api/assessmentapp/assessmentPlanManager/deleteAssessmentPlan.yaml",
                map,
                tokenPattern
        );
    }

    public void deleteAllAssessmentPlan(){
        List<Integer> uidList = getAssessmentPlan().path("body.uid");
        uidList.forEach(uid->{
            deleteAssessmentPlan(uid);
        });
    }

    /**
     * 设置测评方案是否显示
     * @param uid 方案uid
     * @return
     */
    public Response setIsShow(Integer uid){
        HashMap<String,Object> map = new HashMap<>();
        map.put("uid",uid);
        return getResponseFromYaml(
                "/api/assessmentapp/assessmentPlanManager/setAssessmentIsShow",
                map,
                tokenPattern
        );
    }

    public  Response updateAssessmentPlan(String assessmentPlanDescribe, String assessmentPlanName, List<Integer> items,Integer uid){
        HashMap<String,Object> map = new HashMap<>();
        map.put("assessmentPlanDescribe",assessmentPlanDescribe);
        map.put("assessmentPlanName",assessmentPlanName);
        map.put("items",items);
        map.put("uid",uid);
        map.put("_file", "/data/assessmentapp/assessmentPlanManager/updatePlan.json");
        return getResponseFromYaml(
                "/api/assessmentapp/assessmentPlanManager/updateAssessmentPlan.yaml",
                map,
                tokenPattern
        );
    }
}
