package com.gvbrain.api.backendweb.interfance;

import java.util.HashMap;

public class UpdateUser {

    HashMap<String,Object> map = new HashMap<>();

    public UpdateUser buildEmail(String email){
        map.put("email",email);
        return this;
    }

    public UpdateUser buildFullName(String fullName){
        map.put("fullname",fullName);
        return this;
    }

    public UpdateUser buildMobileNumber(String phoneNumber){
        map.put("mobilenumber",phoneNumber);
        return this;
    }

    public UpdateUser buildorgId(Integer orgID){
        map.put("orgId",orgID);
        return this;
    }


    public UpdateUser buildRoleIds(int[] roleIds){
        map.put("roleIds",roleIds);
        return this;
    }

    public UpdateUser buildUid(Integer uid){
        map.put("uid",uid);
        return this;
    }

    public HashMap<String,Object> buildUpdateUser(){
        return map;
    }
}
