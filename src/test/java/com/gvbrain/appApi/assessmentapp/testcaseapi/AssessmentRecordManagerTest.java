package com.gvbrain.appApi.assessmentapp.testcaseapi;

import com.gvbrain.appApi.Utils.RandomValueUtil;
import com.gvbrain.appApi.assessmentapp.interfance.AddRecord;
import com.gvbrain.appApi.assessmentapp.interfance.CreatePatient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class AssessmentRecordManagerTest {

    AssessmentRecordManager assessmentRecordManager;

    @BeforeEach
    void setup(){
        if (assessmentRecordManager == null){
            assessmentRecordManager = new AssessmentRecordManager();
        }
    }

    @Test
    void uploadFile() {
        String filePath = "/data/assessmentapp/assessmentRecordManager/CTD4.jpg";
        assessmentRecordManager.uploadFile(filePath).then().statusCode(200).body("status",equalTo("1"));
    }

    /**
     * 新增测评报告 + 查询测评报告
     * 创建患者-创建方案-测评-生成测评结果-上传测评报告图片-新增测评报告-查询测评报告
     */
    @Test
    void addRecord(){
        //生成当前时间oktime
        Long okTime = System.currentTimeMillis();
        //创建患者，获取患者信息和uid
        String patientName = RandomValueUtil.getRandomName();
        String phoneNumber = RandomValueUtil.getRandomPhoneNumber();
        String patientBirthDate = RandomValueUtil.getRandomBirthDate();
        Integer educationTime = RandomValueUtil.getNum(0,22);
        CreatePatient buildPatient = new CreatePatient.CreatePatientBuilder()
                .buildPatientName(patientName)
                .buildMobilePhone(phoneNumber)
                .buildPatientBirthdate(patientBirthDate)
                .buildEducationTime(educationTime)
                .buildAddress("上海市浦东新区张江镇")
                .buildPatient();
        HashMap<String, Object> map = buildPatient.getMap();
        //获取创建患者的基本信息uid
        String response = new PatientManager().createPatient(map).asString();
        Integer patientUid = from(response).get("body.patient.uid");
        //创建方案并获取方案id和方案名
        String assessmentPlanDescribe = RandomValueUtil.getRandomAssessmentPlanDescribe();
        String assessmentPlanName = RandomValueUtil.getRandomAssessmentPlanName();
        //使用CTD4画钟量表
        List<Integer> items = new ArrayList<>(Arrays.asList(3));
        Integer planUid = new AssessmentPlanManager().createAssessmentPlan(assessmentPlanDescribe,assessmentPlanName,items).then().statusCode(200)
                .body("status",equalTo("1"))
                .extract().path("body.uid");
        //上传图片获取报告图片url
        String filePath = "/data/assessmentapp/assessmentRecordManager/CTD4.jpg";
        String baogUrl = assessmentRecordManager.uploadFile(filePath).then().statusCode(200).body("status", equalTo("1"))
                .extract().path("body.url");
        //生成测评报告
        HashMap<String,Object> recordMap = new AddRecord()
                .buildOkTime(okTime)
                .buildPatientName(patientName)
                .buildMobilePhone(phoneNumber)
                .buildPatientBirthdate(patientBirthDate)
                .buildEducationTime(educationTime)
                .buildAddress("上海市浦东新区张江镇")
                .buildPersonUid(patientUid)
                .buildPlanId(planUid.toString())
                .buildPlanName(assessmentPlanName)
                .buildBaoGUrl(baogUrl).buildRecord();
        //新增测评报告
        assessmentRecordManager.addRecord(recordMap).then().statusCode(200).body("status",equalTo("1"));
        //查询测评报告,验证报告正确性
        assessmentRecordManager.searchRecord(patientUid).then().statusCode(200)
                .body("status",equalTo("1"))
                .body("body[0].planId",equalTo(planUid))
                .body("body[0].planName",equalTo(assessmentPlanName))
                .body("body[0].personBean.uid",equalTo(patientUid))
                .body("body[0].personBean.patientName",equalTo(patientName))
                .body("body[0].personBean.patientBirthdate",equalTo(patientBirthDate))
                .body("body[0].sonAnwserBeans[0].baogUrl",equalTo(baogUrl))
                .body(matchesJsonSchemaInClasspath("responseSchema/assessmentapp/recordManager/searchRecord.schema"));
    }
}