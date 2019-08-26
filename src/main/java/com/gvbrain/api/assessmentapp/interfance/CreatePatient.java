package com.gvbrain.api.assessmentapp.interfance;

import lombok.Data;

import java.util.HashMap;

@Data
public class CreatePatient {

    /*private HashMap<String,Object> address;
    private HashMap<String,Object> education;
    private HashMap<String,Object> educationTime;
    private HashMap<String,Object> jobType;
    private HashMap<String,Object> marrige;
    private HashMap<String,Object> mobilephone;
    private HashMap<String,Object> patientAge;
    private HashMap<String,Object> patientBirthdate;
    private HashMap<String,Object> patientName;
    private HashMap<String,Object> patientSex;*/
    private HashMap<String,Object> map;


    public static class CreatePatientBuilder {
        HashMap<String,Object> map1 = new HashMap<>();

        private CreatePatient createPatient;
        public CreatePatientBuilder(){
            createPatient = new CreatePatient();
        }

        public CreatePatientBuilder buildEducation(String education){
            map1.put("$.patient.education",education);
            createPatient.setMap(map1);
            return this;
        }

        public CreatePatientBuilder buildEducationTime(Integer educationTime){
            map1.put("$.patient.educationTime",educationTime);
            createPatient.setMap(map1);
            return this;
        }

        public CreatePatientBuilder buildJobType(String jobType){
            map1.put("$.patient.jobType",jobType);
            createPatient.setMap(map1);
            return this;
        }

        public CreatePatientBuilder buildMarrige(String marrige){
            map1.put("$.patient.marrige",marrige);
            createPatient.setMap(map1);
            return this;
        }

        public CreatePatientBuilder buildMobilePhone(String mobilePhone){
            map1.put("$.patient.mobilephone",mobilePhone);
            createPatient.setMap(map1);
            return this;
        }

        public CreatePatientBuilder buildPatientAge(Integer patientAge){
            map1.put("$.patient.patientAge",patientAge);
            createPatient.setMap(map1);
            return this;

        }

        public CreatePatientBuilder buildPatientBirthdate(String patientBirthdate){
            map1.put("$.patient.patientBirthdate",patientBirthdate);
            createPatient.setMap(map1);
            return this;
        }

        public CreatePatientBuilder buildPatientName(String patientName){
            map1.put("$.patient.patientName",patientName);
            createPatient.setMap(map1);
            return this;
        }

        public CreatePatientBuilder buildAddress(String address){
            map1.put("$.patient.address",address);
            createPatient.setMap(map1);
            return this;
        }

        public CreatePatientBuilder buildPatientSex(Integer patientSex){
            map1.put("$.patient.patientSex",patientSex);
            createPatient.setMap(map1);
            return this;
        }

        public CreatePatient buildPatient(){
            return createPatient;
        }
    }

}
