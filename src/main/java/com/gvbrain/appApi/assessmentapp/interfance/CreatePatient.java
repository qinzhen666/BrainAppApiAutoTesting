package com.gvbrain.appApi.assessmentapp.interfance;

import lombok.Data;

import java.util.HashMap;

@Data
public class CreatePatient {

    private HashMap<String,Object> address;
    private HashMap<String,Object> education;
    private HashMap<String,Object> educationTime;
    private HashMap<String,Object> jobType;
    private HashMap<String,Object> marrige;
    private HashMap<String,Object> mobilephone;
    private HashMap<String,Object> patientAge;
    private HashMap<String,Object> patientBirthdate;
    private HashMap<String,Object> patientName;
    private HashMap<String,Object> patientSex;

    public static class UpdatePatientBuilder {
        HashMap<String,Object> map = new HashMap<>();

        /*private CreatePatient createPatient;
        public CreatePatientBuilder(){
            createPatient = new CreatePatient();
        }*/

        public UpdatePatientBuilder buildEducation(String education){
            map.put("$.patient.education",education);
            return this;
        }

        public UpdatePatientBuilder buildEducationTime(Integer educationTime){
            map.put("$.patient.educationTime",educationTime);
            return this;
        }

        public UpdatePatientBuilder buildJobType(String jobType){
            map.put("$.patient.jobType",jobType);
            return this;
        }

        public UpdatePatientBuilder buildMarrige(String marrige){
            map.put("$.patient.marrige",marrige);
            return this;
        }

        public UpdatePatientBuilder buildMobilePhone(String mobilePhone){
            map.put("$.patient.mobilephone",mobilePhone);
            return this;
        }

        public UpdatePatientBuilder buildPatientAge(Integer patientAge){
            map.put("$.patient.patientAge",patientAge);
            return this;
        }

        public UpdatePatientBuilder buildPatientBirthdate(String patientBirthdate){
            map.put("$.patient.patientBirthdate",patientBirthdate);
            return this;
        }

        public UpdatePatientBuilder buildPatientName(String patientName){
            map.put("$.patient.patientName",patientName);
            return this;
        }

        public UpdatePatientBuilder buildAddress(String address){
            map.put("$.patient.address",address);
            return this;
        }

        public UpdatePatientBuilder buildPatientSex(Integer patientSex){
            map.put("$.patient.patientSex",patientSex);
            return this;
        }

        public HashMap<String,Object> buildPatient(){
            return map;
        }
    }

}
