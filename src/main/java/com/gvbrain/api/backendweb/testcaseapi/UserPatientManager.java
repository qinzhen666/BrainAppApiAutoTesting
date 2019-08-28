package com.gvbrain.api.backendweb.testcaseapi;

import com.gvbrain.api.ApiToken;
import io.restassured.response.Response;

import java.util.HashMap;

public class UserPatientManager extends ApiToken {
    private String tokenPattern = "brainBackend";

    public Response selectPatient(){
        return null;
    }

    /**
     * 患者列表根据姓名查询患者
     * @param patientName
     * @return
     */
    public Response searchPatientByName(String patientName){
        HashMap<String,Object> map = new HashMap<>();
        map.put("patientName",patientName);
        return getResponseFromHar(
                "/data/backendweb/userpatientManager/restPatient.har.json",
                ".*rest/backend/patients.*",
                map,
                tokenPattern
        );
    }

    /**
     * 新增后台用户
     * @param orgId
     * @param fullname
     * @param email
     * @param mobilenumber
     * @param roleIds
     * @return
     */
    public Response insertUser(Integer orgId, String fullname, String email, String mobilenumber, int[] roleIds){
        HashMap<String,Object> map = new HashMap<>();
        map.put("orgId",orgId);
        map.put("fullname",fullname);
        map.put("email",email);
        map.put("mobilenumber",mobilenumber);
        map.put("roleIds",roleIds);
        return getResponseFromHar(
                "/data/backendweb/userpatientManager/userInsert.har.json",
                ".*/rest/backend/user/insert.*",
                map,
                tokenPattern
        );
    }

    /**
     * 用户列表查询后台用户
     * @param fullName
     * @param orgId
     * @return
     */
    public Response queryUser(String fullName, Integer orgId){
        HashMap<String,Object> map = new HashMap<>();
        map.put("fullname",fullName);
        map.put("orgId",orgId);
        return getResponseFromHar(
                "/data/backendweb/userpatientManager/queryUser.har.json",
                ".*/rest/backend/user/query.*",
                map,
                tokenPattern
        );
    }

    /**
     * 编辑系统用户
     * @param map
     * @return
     */
    public Response updateUser(HashMap<String ,Object> map){
        return getResponseFromHar(
                "/data/backendweb/userpatientManager/updateUser.har.json",
                ".*/rest/backend/user/update.*",
                map,
                tokenPattern
        );
    }

    /**
     * 用户状态操作(status 0:禁用 1:启动)
     * @param status
     * @param uid
     * @return
     */
    public Response userDisable(Integer status,Integer uid){
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",status);
        map.put("uid",uid);
        return getResponseFromHar(
                "/data/backendweb/userpatientManager/userDisable.har.json",
                ".*/rest/backend/user/disable.*",
                map,
                tokenPattern
        );
    }

    /**
     * 获取用户授权信息
     * @return
     */
    public Response authorization(){
        return getResponseFromHar(
                "/data/backendweb/userpatientManager/authorization.har.json",
                ".*/rest/backend/authorization.*",
                null,
                tokenPattern
        );
    }
}
