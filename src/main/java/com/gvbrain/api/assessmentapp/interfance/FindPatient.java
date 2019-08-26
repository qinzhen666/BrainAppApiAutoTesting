package com.gvbrain.api.assessmentapp.interfance;

import lombok.Data;

import java.util.HashMap;

@Data
public class FindPatient {
    private HashMap<String,Object> patientName;
    private HashMap<String,Object> patientSex;
    private HashMap<String,Object> patientAge;
    private HashMap<String,Object> educationTime;
    private HashMap<String,Object> medicalHistory;

    public static class FindPatientBuilder{
        HashMap<String,Object> map = new HashMap<>();
        private FindPatient findPatient;

        public FindPatientBuilder(){
            findPatient = new FindPatient();
        }

        public FindPatientBuilder buildName(String patientName){
            findPatient.patientName = (HashMap<String, Object>) map.put("patientName",patientName);
            return this;
        }

        public FindPatientBuilder buildSex(Integer patientSex){
            findPatient.patientSex = (HashMap<String, Object>) map.put("patientSex",patientSex);
            return this;
        }

        public FindPatientBuilder buildAge(String patientAge){
            findPatient.patientAge = (HashMap<String, Object>) map.put("patientAge",patientAge);
            return this;
        }

        public FindPatientBuilder buildEduTime(Integer educationTime){
            findPatient.educationTime = (HashMap<String, Object>) map.put("educationTime",educationTime);
            return this;
        }

        public FindPatientBuilder buildMedHistory(Integer medicalHistory){
            findPatient.medicalHistory = (HashMap<String, Object>) map.put("medicalHistory",medicalHistory);
            return this;
        }


        public HashMap<String,Object> buildPatient(){
            return map;
        }
    }
}
