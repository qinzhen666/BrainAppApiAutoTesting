package com.gvbrain.api.e2e;

import com.gvbrain.api.Utils.RandomValueUtil;
import com.gvbrain.api.assessmentapp.interfance.CreatePatient;
import com.gvbrain.api.assessmentapp.testcase.PatientManager;
import com.gvbrain.api.backendweb.testcaseapi.PatientBackendManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class App2WebTest {

    PatientManager patientManager ;
    PatientBackendManager patientBackendManager;
    RandomValueUtil randomValueUtil = new RandomValueUtil();

    @BeforeEach
    void setup(){
        if (patientManager == null){
            patientManager = new PatientManager();
        }
        if (patientBackendManager == null){
            patientBackendManager = new PatientBackendManager();
        }
    }

    @Test
    void searchBackendPatientByName(){
        //先在App端创建患者
        String patientName = randomValueUtil.getRandomName();
        String phoneNumber = randomValueUtil.getRandomPhoneNumber();
        String patientBirthDate = randomValueUtil.getRandomBirthDate();
        Integer educationTime = randomValueUtil.getNum(0,22);
        CreatePatient buildPatient = new CreatePatient.CreatePatientBuilder()
                .buildPatientName(patientName)
                .buildMobilePhone(phoneNumber)
                .buildPatientBirthdate(patientBirthDate)
                .buildEducationTime(educationTime)
                .buildAddress("上海市浦东新区张江镇")
                .buildPatient();
        HashMap<String, Object> map = buildPatient.getMap();
        System.out.println(map);
        patientManager.createPatient(map).then().statusCode(200)
                .body("status",equalTo("1"))
                .body(matchesJsonSchemaInClasspath("responseSchema/assessmentapp/patientManager/createPatient.schema"));
        //再在后台根据姓名查询患者
        patientBackendManager.searchPatientByName(patientName).then().statusCode(200)
        .body("status",equalTo("1"));
    }
}
