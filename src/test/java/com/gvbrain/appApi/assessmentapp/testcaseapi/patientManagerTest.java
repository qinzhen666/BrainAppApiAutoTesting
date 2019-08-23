package com.gvbrain.appApi.assessmentapp.testcaseapi;

import com.gvbrain.appApi.Utils.RandomValueUtil;
import com.gvbrain.appApi.assessmentapp.interfance.CreatePatient;
import com.gvbrain.appApi.assessmentapp.interfance.FindPatient;
import com.gvbrain.appApi.assessmentapp.interfance.UpdatePatient;
import com.sun.xml.fastinfoset.util.StringArray;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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
    }

    /**
     * 分别对以下字段缺少情况下进行患者创建，检查后端返回信息
     * @param patientName
     * @param phoneNumber
     * @param patientBirthDate
     * @param educationTime
     * @param expected
     */
    @ParameterizedTest
    @MethodSource("patientInfoProvider")
    void createPatientMissField(String patientName,String phoneNumber,String patientBirthDate,Integer educationTime,String expected) {
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
                .body("status", equalTo("0"))
                .body("message", equalTo(expected));
    }
    static Stream<Arguments> patientInfoProvider(){
        RandomValueUtil randomValueUtil = new RandomValueUtil();
        List<Arguments> list = new ArrayList<>();
        Arguments arguments = null;
        for (int i = 0 ; i < 4; i++){
            List<Object> objectList= new ArrayList<>();
            List<String> expectedList = Arrays.asList("姓名不允许为空!;","手机号不允许为空!;","出生日期不允许为空!;","教育时间不允许为空!;");
            String patientName = randomValueUtil.getRandomName();
            String phoneNumber = randomValueUtil.getRandomPhoneNumber();
            String patientBirthDate = randomValueUtil.getRandomBirthDate();
            Integer educationTime = randomValueUtil.getNum(0,22);
            objectList.add(patientName);
            objectList.add(phoneNumber);
            objectList.add(patientBirthDate);
            objectList.add(educationTime);
            objectList.add(expectedList.get(i));
            objectList.set(i,null);
            arguments = arguments(objectList.get(0),objectList.get(1),objectList.get(2),objectList.get(3),objectList.get(4));
            list.add(arguments);
        }
        return Stream.of(list.get(0),list.get(1),list.get(2),list.get(3));
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
        Integer patientSex = randomValueUtil.getNum(0,1);
        Integer educationTime = randomValueUtil.getNum(0,22);
        CreatePatient buildPatient = new CreatePatient.CreatePatientBuilder()
                .buildPatientName(patientName)
                .buildMobilePhone(phoneNumber)
                .buildPatientSex(patientSex)
                .buildPatientBirthdate(patientBirthDate)
                .buildEducationTime(educationTime)
                .buildAddress("上海市浦东新区张江镇")
                .buildPatient();
        HashMap<String, Object> map = buildPatient.getMap();
        System.out.println(map);
        patientManager.createPatient(map).then().statusCode(200).body("status",equalTo("1"));
        //查询查询患者信息，检查是否创建成功
        HashMap<String,Object> map2 = new FindPatient.FindPatientBuilder()
                .buildName(patientName)
                .buildSex(patientSex)
                .buildMedHistory(1)
                .buildPatient();
        patientManager.getPatientInfoByList(map2).then().statusCode(200)
                .body("status",equalTo("1"))
                .body("body.patient.patientName[0]",equalTo(patientName))
                .body("body.patient.mobilephone[0]",equalTo(phoneNumber));
    }


    /**
     * 分别传入不存在的以下参数，检查接口响应及查询结果
     * @param name
     * @param sex
     * @param age
     * @param medHistory
     */
    @ParameterizedTest
    @MethodSource("getInfoListProvider")
    //String name,Integer sex,String age,Integer medHistory,List expected2
    //ArgumentsAccessor arguments
    void getPatientInfoListNoxisting(String name,Integer sex,String age,Integer medHistory,String expected1,List expected2){
        HashMap<String,Object> map2 = new FindPatient.FindPatientBuilder()
                .buildName(name)
                .buildSex(sex)
                .buildAge(age)
                .buildMedHistory(medHistory)
                .buildPatient();
        patientManager.getPatientInfoByList(map2).then().statusCode(200)
                .body("status",equalTo(expected1))
                .body("body",equalTo(expected2));
    }
    static Stream<Arguments> getInfoListProvider(){
        return Stream.of(
                Arguments.of("不存在的姓名",null,null,null,"1",new ArrayList<>()),
                Arguments.of(null,-2,null,null,"1",new ArrayList<>()),
                Arguments.of(null,null,null,10,"1",new ArrayList<>()),
                Arguments.of("不存在的姓名",null,"0-60",null,"1",new ArrayList<>()),
                Arguments.of(null,null,"-1-0",null,"0",null)
        );
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
        CreatePatient buildPatient = new CreatePatient.CreatePatientBuilder()
                .buildPatientName(patientName)
                .buildMobilePhone(phoneNumber)
                .buildPatientBirthdate(patientBirthDate)
                .buildEducationTime(educationTime)
                .buildAddress("上海市浦东新区张江镇")
                .buildPatient();
        HashMap<String, Object> map = buildPatient.getMap();
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
        CreatePatient buildPatient = new CreatePatient.CreatePatientBuilder()
                .buildPatientName(patientName)
                .buildMobilePhone(phoneNumber)
                .buildPatientBirthdate(patientBirthDate)
                .buildEducationTime(educationTime)
                .buildAddress("上海市浦东新区张江镇")
                .buildPatient();
        HashMap<String, Object> map = buildPatient.getMap();
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
        CreatePatient buildPatient = new CreatePatient.CreatePatientBuilder()
                .buildPatientName(patientName)
                .buildMobilePhone(phoneNumber)
                .buildPatientBirthdate(patientBirthDate)
                .buildEducationTime(educationTime)
                .buildAddress("上海市浦东新区张江镇")
                .buildPatient();
        HashMap<String, Object> map = buildPatient.getMap();
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