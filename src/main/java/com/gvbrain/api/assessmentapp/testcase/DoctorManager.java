package com.gvbrain.api.assessmentapp.testcase;

import com.gvbrain.api.ApiToken;
import io.restassured.response.Response;

import java.util.HashMap;

public class DoctorManager extends ApiToken {
    private String tokenPattern = "brainPlatform";

    /**
     * 获取脑健康师信息,测评方案信息
     * @return
     */
    public Response getDoctorInfo(){
        return getResponseFromYaml(
                "/api/assessmentapp/doctorManager/restDoctor.yaml",
                null,
                tokenPattern
                );
    }

    /**
     * 重置密码
     */
    public Response changePwd(String currentPassword,String newPassword,String renewPassword){
        HashMap<String,Object> map = new HashMap<>();
        map.put("currentPassword",currentPassword);
        map.put("newPassword",newPassword);
        map.put("renewPassword",renewPassword);
        map.put("_file", "/data/assessmentapp/assessmentPlanManager/changePassword.json");
        return getResponseFromYaml(
                "/api/assessmentapp/doctorManager/changePassword.yaml",
                map,
                tokenPattern
        );
    }

    /**
     * 问题反馈
     */
    public Response feedBack(String feedBackInfo){
        HashMap<String,Object> map = new HashMap<>();
        map.put("_body",String.format("{\n" +
                "  \"feedbackDescribe\": \"%s\"\n" +
                "}" , feedBackInfo));
        System.out.println(map);
        return getResponseFromYaml(
                "/api/assessmentapp/doctorManager/feedBack.yaml",
                map,tokenPattern
        );
    }
}
