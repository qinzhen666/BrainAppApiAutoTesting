package com.gvbrain.appApi.assessmentapp.testcaseapi;

import com.gvbrain.appApi.assessmentapp.interfance.FindPatient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class patientManagerTest {

    PatientManager patientManager;
    Random random = new Random();
    @BeforeEach
    void setup(){
        if (patientManager == null){
            patientManager = new PatientManager();
        }
    }

    @Test
    void getPatientInfoByList() {
        //根据查询条件的不同，任意传入查询条件进行列表患者的查询
        //(String name,Integer sex,String age,String eduTime, Integer medHistory)
        HashMap<String, Object> map = new FindPatient.FindPatientBuilder()
                .buildSex(0)
                .buildPatient();
        System.out.println(map);
        patientManager.getPatientInfoByList(map);
    }
}