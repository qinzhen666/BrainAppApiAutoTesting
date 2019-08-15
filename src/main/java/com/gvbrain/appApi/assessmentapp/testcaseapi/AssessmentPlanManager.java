package com.gvbrain.appApi.assessmentapp.testcaseapi;

import com.gvbrain.appApi.ApiToken;
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
                "/api/brainPFApp/assessmentPlanManager/restAssessmentPlan.yaml",
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
        map.put("_file","/data/assessmentapp/assessmentPlanManager/createAssessmentPlan.json");
        return getResponseFromYaml(
                "/api/brainPFApp/assessmentPlanManager/createAssessmentPlan.yaml",
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
                "/api/brainPFApp/assessmentPlanManager/deleteAssessmentPlan.yaml",
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
                "/api/brainPFApp/assessmentPlanManager/setAssessmentIsShow",
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
        map.put("_file","/data/assessmentapp/assessmentPlanManager/updateAssessmentPlan.json");
        return getResponseFromYaml(
                "/api/brainPFApp/assessmentPlanManager/updateAssessmentPlan.yaml",
                map,
                tokenPattern
        );
    }
}
