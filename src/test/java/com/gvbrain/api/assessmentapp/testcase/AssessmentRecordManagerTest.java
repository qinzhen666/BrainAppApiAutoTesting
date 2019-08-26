package com.gvbrain.api.assessmentapp.testcase;

import com.gvbrain.api.Utils.RandomValueUtil;
import com.gvbrain.api.assessmentapp.interfance.AddRecord;
import com.gvbrain.api.assessmentapp.interfance.CreatePatient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.*;

class AssessmentRecordManagerTest {

    AssessmentRecordManager assessmentRecordManager;
    RandomValueUtil randomValueUtil = new RandomValueUtil();

    @BeforeAll
    static void beforeAll(){
        PatientManager patientManager = new PatientManager();
        patientManager.deleteAllPatients();
    }

    @BeforeEach
    void setup(){
        if (assessmentRecordManager == null){
            assessmentRecordManager = new AssessmentRecordManager();
        }
    }

    /**
     * 上传报告图片
     */
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


    /**
     * 对新增报告必填项进行缺少校验
     * @param okTime
     * @param patientName
     * @param planUid
     * @param baogUrl
     * @param expecStatus
     * @param expecMessage
     */
    @ParameterizedTest
    @CsvSource({
            " , test患者,  252, testbaogUrl, 0, 测评时间不能为空;",
            "1566439505726, ,  252, testbaogUrl, 0, 患者姓名不能为空;",
            "1566439505726, test患者,  , testbaogUrl, 0, 测评方案编号不能为空;",
            "1566439505726, test患者,  252, , 0, 报告图片地址不能为空;"
    })
    void addRecordFail(Long okTime,String patientName,String planUid,String baogUrl,String expecStatus,String expecMessage){
        //生成测评报告
        HashMap<String,Object> recordMap = new AddRecord()
                .buildOkTime(okTime)
                .buildPatientName(patientName)
                .buildPlanId(planUid)
                .buildBaoGUrl(baogUrl).buildRecord();
        //新增测评报告
        assessmentRecordManager.addRecord(recordMap).then().statusCode(200)
                .body("status",equalTo(expecStatus))
                .body("message",equalTo(expecMessage));
    }
}