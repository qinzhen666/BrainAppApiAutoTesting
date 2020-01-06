package com.gvbrain.api.e2e.testcase;

import com.gvbrain.api.Utils.RandomValueUtil;
import com.gvbrain.api.assessmentapp.interfance.AddRecord;
import com.gvbrain.api.assessmentapp.interfance.CreatePatient;
import com.gvbrain.api.assessmentapp.testcaseapi.AssessmentPlanManager;
import com.gvbrain.api.assessmentapp.testcaseapi.AssessmentRecordManager;
import com.gvbrain.api.assessmentapp.testcaseapi.DoctorManager;
import com.gvbrain.api.assessmentapp.testcaseapi.PatientManager;
import com.gvbrain.api.backendweb.testcaseapi.RecordManager;
import com.gvbrain.api.backendweb.testcaseapi.UserPatientManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

class PatientRecordApp2WebTest {

    private PatientManager patientManager ;
    private UserPatientManager userPatientManager;
    private RecordManager recordManager;
    private AssessmentRecordManager assessmentRecordManager;
    private AssessmentPlanManager assessmentPlanManager;

    @BeforeAll
    static void beforeTest(){
        new PatientManager().deleteAllPatients();
        new AssessmentPlanManager().deleteAllAssessmentPlan();
    }

    @AfterAll
    static void afterTest(){
        new PatientManager().deleteAllPatients();
        new AssessmentPlanManager().deleteAllAssessmentPlan();
    }

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
        if (assessmentPlanManager == null){
            assessmentPlanManager = new AssessmentPlanManager();
        }
    }

    /**
     * 获取所有测评过程的信息
     * 创建患者-创建方案-测评-生成测评结果-上传测评报告图片-新增测评报告
     * @return
     */
    public HashMap<String,Object> getPatientPlanRecord(){
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
        HashMap<String,Object> allInfoMap = new HashMap<>();
        allInfoMap.put("okTime",okTime);
        allInfoMap.put("patientName",patientName);
        allInfoMap.put("phoneNumber",phoneNumber);
        allInfoMap.put("patientBirthDate",patientBirthDate);
        allInfoMap.put("educationTime",educationTime);
        allInfoMap.put("patientUid",patientUid);
        allInfoMap.put("assessmentPlanDescribe",assessmentPlanDescribe);
        allInfoMap.put("assessmentPlanName",assessmentPlanName);
        allInfoMap.put("planUid",planUid);
        allInfoMap.put("baogUrl",baogUrl);
        return allInfoMap;
    }

    /**
     * App创建患者-后台患者管理-查询患者
     */
    @Test
    void searchBackendPatientByName(){
        //先在App端创建患者
        HashMap<String, Object> infoMap = getPatientPlanRecord();
        String patientName = (String) infoMap.get("patientName");
        String phoneNumber = (String) infoMap.get("phoneNumber");
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
        //先创建患者-方案-报告
        HashMap map = getPatientPlanRecord();
        Integer patientUid = (Integer) map.get("patientUid");
        Integer planUid = (Integer) map.get("planUid");
        //在后台根据患者ID查询测评报告
        recordManager.findRecordById(patientUid).then().statusCode(200)
                .body("status",equalTo("1"))
                .body("body[0].patientUid",equalTo(patientUid))
                .body("body[0].assessmentPlanUid",equalTo(planUid))
                .body(matchesJsonSchemaInClasspath("responseSchema/backendweb/recordManager/findRecordByPId.schema"));
    }


    /**
     * 根据医生id查询测评人次
     * 后台检查创建人数->App创建患者->创建方案->生成测评报告->上传测评报告->后台检查测评人数
     */
    @Test
    void getRecordsCount(){
        //获取当前登录的doctorId
        Integer docId = new DoctorManager().getDoctorInfo().then().statusCode(200)
                .body("status",equalTo("1"))
                .extract().path("body.doctor.uid");
        //检查当前测评人数
        Integer countOld = recordManager.recordsCountByDocId(docId).then().statusCode(200)
                .body("status",equalTo("1"))
                .extract().path("body");
        //App端创建患者-方案-报告-测评
        getPatientPlanRecord();
        //再次检查当前测评人数
        recordManager.recordsCountByDocId(docId).then().statusCode(200)
                .body("status",equalTo("1"))
                .body("body",equalTo(countOld+1));
    }


}
