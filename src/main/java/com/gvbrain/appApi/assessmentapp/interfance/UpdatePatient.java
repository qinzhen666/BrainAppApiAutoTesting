package com.gvbrain.appApi.assessmentapp.interfance;

import java.util.HashMap;

public class UpdatePatient {

    private HashMap<String,Object> address;
    private HashMap<String,Object> education;
    private HashMap<String,Object> educationTime;
    private HashMap<String,Object> jobType;
    private HashMap<String,Object> marrige;
    private HashMap<String,Object> mobilephone;
//    private HashMap<String,Object> patientAge;
    private HashMap<String,Object> patientBirthdate;
    private HashMap<String,Object> patientName;
    private HashMap<String,Object> patientSex;
    private HashMap<String,Object> status;
    private HashMap<String,Object> patientUid;
    private HashMap<String,Object> uidOne;
    private HashMap<String,Object> uidTwo;
    private HashMap<String,Object> uidThree;

    public static class UpdatePatientBuilder{
        HashMap<String,Object> map = new HashMap<>();

        /*private CreatePatient createPatient;
        public CreatePatientBuilder(){
            createPatient = new CreatePatient();
        }*/
        public UpdatePatientBuilder buildUidOne(Integer uidOne){
            map.put("$.medicalHistoryType[0].uid",uidOne);
            return this;
        }

        public UpdatePatientBuilder buildUidTwo(Integer uidTwo){
            map.put("$.medicalHistoryType[1].uid",uidTwo);
            return this;
        }

        public UpdatePatientBuilder buildUidThree(Integer uidThree){
            map.put("$.medicalHistoryType[2].uid",uidThree);
            return this;
        }

        public UpdatePatientBuilder buildUid(Integer patientUid){
            map.put("$.patient.uid",patientUid);
            return this;
        }

        public UpdatePatientBuilder updateStatus(Integer status){
            map.put("$.status",status);
            return this;
        }

        public UpdatePatientBuilder updateEducation(String education){
            map.put("$.patient.education",education);
            return this;
        }

        public UpdatePatientBuilder updateEducationTime(Integer educationTime){
            map.put("$.patient.educationTime",educationTime);
            return this;
        }

        public UpdatePatientBuilder updateJobType(String jobType){
            map.put("$.patient.jobType",jobType);
            return this;
        }

        public UpdatePatientBuilder updateMarrige(String marrige){
            map.put("$.patient.marrige",marrige);
            return this;
        }

        public UpdatePatientBuilder updateMobilePhone(String mobilePhone){
            map.put("$.patient.mobilephone",mobilePhone);
            return this;
        }

        /*public UpdatePatientBuilder updatePatientAge(Integer patientAge){
            map.put("$.patient.patientAge",patientAge);
            return this;
        }*/

        public UpdatePatientBuilder updatePatientBirthdate(String patientBirthdate){
            map.put("$.patient.patientBirthdate",patientBirthdate);
            return this;
        }

        public UpdatePatientBuilder updatePatientName(String patientName){
            map.put("$.patient.patientName",patientName);
            return this;
        }

        public UpdatePatientBuilder updateAddress(String address){
            map.put("$.patient.address",address);
            return this;
        }

        public UpdatePatientBuilder updatePatientSex(Integer patientSex){
            map.put("$.patient.patientSex",patientSex);
            return this;
        }

        public HashMap<String,Object> updatePatient(){
            return map;
        }
    }
}
