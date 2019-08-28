package com.gvbrain.api.assessmentapp.testcaseapi;

import com.gvbrain.api.ApiToken;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;

public class PatientManager extends ApiToken {

    private String tokenPattern = "brainPlatform";

    /**
     * 根据患者姓名与手机号查询患者信息
     * @param patientName
     * @param mobilePhone
     * @return
     */
    public Response getPatientByNamePhone(String patientName, String mobilePhone){
        HashMap<String,Object> map = new HashMap<>();
        map.put("patientName",patientName);
        map.put("mobilephone",mobilePhone);
        return getResponseFromYaml(
                "/api/assessmentapp/patientManager/restPatientsNamePhone.yaml",
                map,
                tokenPattern
        );
    }

    public Response getPatientInfoByList(HashMap<String,Object> map){
        map.put("_file", "/data/assessmentapp/patientManager/restPatientByList.json");
        return getResponseFromYaml(
                "/api/assessmentapp/patientManager/restPatientsList.yaml",
                map,
                tokenPattern
        );
    }

    public Response getAllPatientInfoByList(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("_file", "/data/assessmentapp/patientManager/restPatientByList.json");
        return getResponseFromYaml(
                "/api/assessmentapp/patientManager/restPatientsList.yaml",
                map,
                tokenPattern
        );
    }

    public Response createPatient(HashMap<String,Object> map){
        map.put("_file", "/data/assessmentapp/patientManager/createPatient.json");
        return getResponseFromYaml(
                "/api/assessmentapp/patientManager/createPatient.yaml",
                map,
                tokenPattern
        );
    }

    /**
     * 编辑、新增或编辑患者
     *  1、选择“编辑患者”，若患者"patientName"和"mobilephone"都不变，则为编辑,"status"传入1
     *  2、选择“编辑患者”，若患者"patientName"和"mobilephone"都任意一个发生改变，则为编新增,"status"传入1
     *  3、选择“创建患者”，若检查患者"patientName"和"mobilephone"在数据库已存在，则为编辑，"status"传入0
     * @param map
     * @return
     */
    public Response updatePatient(HashMap<String,Object> map){
        map.put("_file", "/data/assessmentapp/patientManager/updatePatient.json");
        return getResponseFromYaml(
                "/api/assessmentapp/patientManager/updatePatient.yaml",
                map,
                tokenPattern
        );
    }

    public Response deletePatient(Integer uid){
        HashMap<String,Object> map = new HashMap<>();
        map.put("uid",uid);
        return getResponseFromYaml(
                "/api/assessmentapp/patientManager/deletePatient.yaml",
                map,
                tokenPattern
        );
    }

    public Response deleteAllPatients(){
            List<Integer> uidList = getAllPatientInfoByList().then().log().all().extract().path("body.patient.uid");
            uidList.forEach(uid->{
                deletePatient(uid);
            });
            return null;
    }

}
