package com.gvbrain.api.e2e.testcase;

import com.gvbrain.api.Utils.RandomValueUtil;
import com.gvbrain.api.assessmentapp.interfance.AddRecord;
import com.gvbrain.api.assessmentapp.interfance.CreatePatient;
import com.gvbrain.api.assessmentapp.testcaseapi.AssessmentPlanManager;
import com.gvbrain.api.assessmentapp.testcaseapi.AssessmentRecordManager;
import com.gvbrain.api.assessmentapp.testcaseapi.PatientManager;
import com.gvbrain.api.backendweb.testcaseapi.RecordManager;
import com.gvbrain.api.backendweb.testcaseapi.UserPatientManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.*;

public class App2WebTest {

    private PatientManager patientManager ;
    private UserPatientManager userPatientManager;
    private RecordManager recordManager;
    private AssessmentRecordManager assessmentRecordManager;


    @BeforeEach
    void setup(){
        if (patientManager == null){
            patientManager = new PatientManager();
        }
        if (userPatientManager == null){
            userPatientManager = new UserPatientManager();
        }
        if (recordManager == null){
            recordManager = new RecordManager();
        }
        if (assessmentRecordManager == null){
            assessmentRecordManager = new AssessmentRecordManager();
        }
    }

    /**
     * App创建患者-后台患者管理-查询患者
     */
    @Test
    void searchBackendPatientByName(){
        //先在App端创建患者
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
        System.out.println(map);
        patientManager.createPatient(map).then().statusCode(200)
                .body("status",equalTo("1"))
                .body(matchesJsonSchemaInClasspath("responseSchema/assessmentapp/patientManager/createPatient.schema"));
        //再在后台根据姓名查询患者
        userPatientManager.searchPatientByName(patientName).then().statusCode(200)
        .body("status",equalTo("1"))
        .body("body.patients.patient.patientName",hasItem(patientName))
        .body("body.patients.patient.mobilephone",hasItem(phoneNumber))
        .body(matchesJsonSchemaInClasspath("responseSchema/backendweb/userpatientManager/searchPatientByName.schema"));
    }

    /**
     * App创建患者->创建方案->生成测评报告->上传测评报告->后台根据患者ID查询患者测评报告
     */
    @Test
    void findRecordByPatientId(){
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
        //在后台根据患者ID查询测评报告
        recordManager.findRecordById(patientUid).then().statusCode(200)
                .body("status",equalTo("1"))
                .body("body[0].patientUid",equalTo(patientUid))
                .body("body[0].assessmentPlanUid",equalTo(planUid))
                .body(matchesJsonSchemaInClasspath("responseSchema/backendweb/recordManager/findRecordByPId.schema"));
    }


}
