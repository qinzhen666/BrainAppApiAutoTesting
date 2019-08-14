package com.gvbrain.appApi.assessmentapp.testcaseapi;

import com.gvbrain.appApi.Utils.RandomValueUtil;
import com.gvbrain.appApi.assessmentapp.interfance.CreatePatient;
import com.gvbrain.appApi.assessmentapp.interfance.FindPatient;
import com.gvbrain.appApi.assessmentapp.interfance.UpdatePatient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;

class patientManagerTest {

    PatientManager patientManager;
    RandomValueUtil randomValueUtil = new RandomValueUtil();

    @BeforeAll
    static void beforeAll(){
        PatientManager patientManager1 = new PatientManager();
        patientManager1.deleteAllPatients();
    }

    @BeforeEach
    void setup(){
        if (patientManager == null){
            patientManager = new PatientManager();
        }
    }

    /**
     * 创建患者必填项
     *     address	string
     *     education*	string
     *     educationTime*	integer($int32)
     *     jobType*	string
     *     marrige*	string
     *     mobilephone*	string
     *     patientBirthdate*	string
     *     patientName*	string
     *     patientSex*	integer($int32)
     */
    @Test
    void createPatient(){
        String patientName = randomValueUtil.getRandomName();
        String phoneNumber = randomValueUtil.getRandomPhoneNumber();
        String patientBirthDate = randomValueUtil.getRandomBirthDate();
        Integer educationTime = randomValueUtil.getNum(0,22);
        HashMap<String,Object> map = new CreatePatient.UpdatePatientBuilder()
                .buildPatientName(patientName)
                .buildMobilePhone(phoneNumber)
                .buildPatientBirthdate(patientBirthDate)
                .buildEducationTime(educationTime)
                .buildAddress("上海市浦东新区张江镇")
                .buildPatient();
        System.out.println(map);
        patientManager.createPatient(map).then().statusCode(200).body("status",equalTo("1"));
    }

    /**
     * 根据查询条件的不同，任意传入查询条件进行列表患者的查询
     * (String name,Integer sex,String age,String eduTime, Integer medHistory)
     */
    @Test
    void getPatientInfoByList() {
        String patientName = randomValueUtil.getRandomName();
        String phoneNumber = randomValueUtil.getRandomPhoneNumber();
        String patientBirthDate = randomValueUtil.getRandomBirthDate();
        Integer educationTime = randomValueUtil.getNum(0,22);
        HashMap<String,Object> map = new CreatePatient.UpdatePatientBuilder()
                .buildPatientName(patientName)
                .buildMobilePhone(phoneNumber)
                .buildPatientBirthdate(patientBirthDate)
                .buildEducationTime(educationTime)
                .buildAddress("上海市浦东新区张江镇")
                .buildPatient();
        System.out.println(map);
        patientManager.createPatient(map).then().statusCode(200).body("status",equalTo("1"));
        //查询查询患者信息，检查是否创建成功
        HashMap<String,Object> map2 = new FindPatient.FindPatientBuilder()
                .buildName(patientName)
                .buildMedHistory(1)
                .buildPatient();
        patientManager.getPatientInfoByList(map2).then().statusCode(200)
                .body("status",equalTo("1"))
                .body("body.patient.patientName[0]",equalTo(patientName))
                .body("body.patient.mobilephone[0]",equalTo(phoneNumber));
    }

    /**
     * 1、选择“编辑患者”，若患者"patientName"和"mobilephone"都不变，则为编辑,"status"传入0
     * 3、选择“创建患者”，若检查患者"patientName"和"mobilephone"在数据库已存在，则为编辑，"status"传入0
     */
    @Test
    void updatePatientNoChange(){
        String patientName = randomValueUtil.getRandomName();
        String phoneNumber = randomValueUtil.getRandomPhoneNumber();
        String patientBirthDate = randomValueUtil.getRandomBirthDate();
        Integer educationTime = randomValueUtil.getNum(0,22);
        HashMap<String,Object> map = new CreatePatient.UpdatePatientBuilder()
                .buildPatientName(patientName)
                .buildMobilePhone(phoneNumber)
                .buildPatientBirthdate(patientBirthDate)
                .buildEducationTime(educationTime)
                .buildAddress("上海市浦东新区张江镇")
                .buildPatient();
        //获取创建患者的病例uid和基本信息uid
        String response = patientManager.createPatient(map).asString();
        Integer uidOne = from(response).getInt("body.medicalHistoryType.uid[0]");
        Integer uidTwo = from(response).getInt("body.medicalHistoryType.uid[1]");
        Integer uidThree = from(response).getInt("body.medicalHistoryType.uid[2]");
        Integer patientUid = from(response).get("body.patient.uid");
        String patientBirthDate2 = randomValueUtil.getRandomBirthDate();
        //姓名和手机号不变编辑患者
        HashMap<String,Object> map2 = new UpdatePatient.UpdatePatientBuilder()
                .buildUidOne(uidOne)
                .buildUidTwo(uidTwo)
                .buildUidThree(uidThree)
                .buildUid(patientUid)
                .updateStatus(0)
                .updatePatientName(patientName)//患者姓名不变
                .updateMobilePhone(phoneNumber)//患者手机号不变
                .updateAddress("上海市徐汇区上海南站")
                .updatePatientBirthdate(patientBirthDate2)
                .updatePatient();
        patientManager.updatePatient(map2).then().statusCode(200).body("status",equalTo("1"));
        //检查患者是否编辑成功
        patientManager.getPatientByNamePhone(patientName,phoneNumber).then().statusCode(200)
                .body("status",equalTo("1"))
                .body("body.patient.address",equalTo("上海市徐汇区上海南站"))
                .body("body.patient.patientBirthdate",equalTo(patientBirthDate2));
    }

    /**
     * 2、选择“编辑患者”，若患者"patientName"和"mobilephone"都任意一个发生改变，则为新增,"status"传入1
     */
    @Test
    void updatePatientChangeNamePhone(){
        //先创建一个新患者
        String patientName = randomValueUtil.getRandomName();
        String phoneNumber = randomValueUtil.getRandomPhoneNumber();
        String patientBirthDate = randomValueUtil.getRandomBirthDate();
        Integer educationTime = randomValueUtil.getNum(0,22);
        HashMap<String,Object> map = new CreatePatient.UpdatePatientBuilder()
                .buildPatientName(patientName)
                .buildMobilePhone(phoneNumber)
                .buildPatientBirthdate(patientBirthDate)
                .buildEducationTime(educationTime)
                .buildAddress("上海市浦东新区张江镇")
                .buildPatient();
        //获取创建患者的病例uid和基本信息uid
        String response = patientManager.createPatient(map).asString();
        Integer uidOne = from(response).getInt("body.medicalHistoryType.uid[0]");
        Integer uidTwo = from(response).getInt("body.medicalHistoryType.uid[1]");
        Integer uidThree = from(response).getInt("body.medicalHistoryType.uid[2]");
        Integer patientUid = from(response).get("body.patient.uid");
        //修改患者的手机号码
        String phoneNumberNew = randomValueUtil.getRandomPhoneNumber();
        //手机号改变编辑患者=新增
        HashMap<String,Object> map2 = new UpdatePatient.UpdatePatientBuilder()
                .buildUidOne(uidOne)
                .buildUidTwo(uidTwo)
                .buildUidThree(uidThree)
                .buildUid(patientUid)
                .updateStatus(1)
                .updatePatientName(patientName)//患者姓名不变
                .updateMobilePhone(phoneNumberNew)//患者手机号改变
                .updatePatient();
        patientManager.updatePatient(map2).then().statusCode(200).body("status",equalTo("1"));
        //检查是否新增成功
        patientManager.getPatientByNamePhone(patientName,phoneNumberNew).then().statusCode(200).body("status",equalTo("1"));
        //检查原患者是否还存在
        patientManager.getPatientByNamePhone(patientName,phoneNumber).then().statusCode(200).body("status",equalTo("1"));
    }

    @Test
    void deletePatient(){
        String patientName = randomValueUtil.getRandomName();
        String phoneNumber = randomValueUtil.getRandomPhoneNumber();
        String patientBirthDate = randomValueUtil.getRandomBirthDate();
        Integer educationTime = randomValueUtil.getNum(0,22);
        HashMap<String,Object> map = new CreatePatient.UpdatePatientBuilder()
                .buildPatientName(patientName)
                .buildMobilePhone(phoneNumber)
                .buildPatientBirthdate(patientBirthDate)
                .buildEducationTime(educationTime)
                .buildAddress("上海市浦东新区张江镇")
                .buildPatient();
        //获取创建患者的uid
        Integer uid = patientManager.createPatient(map).then().statusCode(200).extract().path("body.patient.uid");
        //删除患者
        patientManager.deletePatient(uid).then().statusCode(200).body("status",equalTo("1"));
        //调用查询接口检查患者是否成功删除
        HashMap<String,Object> map2 = new FindPatient.FindPatientBuilder()
                .buildName(patientName)
                .buildMedHistory(1)
                .buildPatient();
        patientManager.getPatientInfoByList(map2)
                .then().statusCode(200)
                .body("status",equalTo("1"))
                .body("body",equalTo(new ArrayList<>()));
    }

    @Test
    void deleteAllPatients(){
        patientManager.deleteAllPatients();
    }
}