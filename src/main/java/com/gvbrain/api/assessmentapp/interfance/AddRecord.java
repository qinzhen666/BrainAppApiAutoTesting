package com.gvbrain.api.assessmentapp.interfance;

import java.util.HashMap;

public class AddRecord {

    HashMap<String,Object> map = new HashMap<>();

    public AddRecord buildOkTime(Long okTime){
        map.put("$.okTime",okTime);
        return this;
    }

    public AddRecord buildEducation(String education){
        map.put("$.personBean.education",education);
        return this;
    }

    public AddRecord buildEducationTime(Integer educationTime){
        map.put("$.personBean.educationTime",educationTime);
        return this;
    }

    public AddRecord buildJobType(String jobType){
        map.put("$.personBean.jobType",jobType);
        return this;
    }

    public AddRecord buildMarrige(String marrige){
        map.put("$.personBean.marrige",marrige);
        return this;
    }

    public AddRecord buildMobilePhone(String mobilePhone){
        map.put("$.personBean.mobilephone",mobilePhone);
        return this;
    }

    public AddRecord buildPatientAge(Integer patientAge){
        map.put("$.personBean.patientAge",patientAge);
        return this;

    }

    public AddRecord buildPatientBirthdate(String patientBirthdate){
        map.put("$.personBean.patientBirthdate",patientBirthdate);
        return this;
    }

    public AddRecord buildPatientName(String patientName){
        map.put("$.personBean.patientName",patientName);
        return this;
    }

    public AddRecord buildAddress(String address){
        map.put("$.personBean.address",address);
        return this;
    }

    public AddRecord buildPatientSex(Integer patientSex){
        map.put("$.personBean.patientSex",patientSex);
        return this;
    }

    public AddRecord buildPersonUid(Integer personUid){
        map.put("$.personBean.uid",personUid);
        return this;
    }

    public AddRecord buildPlanId(String planId){
        map.put("$.planId",planId);
        return this;
    }

    public AddRecord buildPlanName(String planName){
        map.put("$.planName",planName);
        return this;
    }

    public AddRecord buildBaoGUrl(String baogUrl){
        map.put("$.sonAnwserBeans[0].baogUrl",baogUrl);
        return this;
    }


    public HashMap<String,Object> buildRecord(){
        return map;
    }
}
