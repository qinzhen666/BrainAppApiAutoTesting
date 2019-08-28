package com.gvbrain.api.backendweb.testcase;

import com.gvbrain.api.Utils.RSAEncryptUtil;
import com.gvbrain.api.Utils.RandomValueUtil;
import com.gvbrain.api.backendweb.interfance.UpdateUser;
import com.gvbrain.api.backendweb.testcaseapi.UserPatientManager;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static com.gvbrain.api.ApiToken.setLoginEnv;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class UserPatientManagerTest {

    //用户添加完成后无法删除，只可禁用
    private UserPatientManager userPatientManager;

    @BeforeEach
    void setup(){
        if (userPatientManager == null){
            userPatientManager = new UserPatientManager();
        }
    }

    /**
     * 新增系统管理员用户
     */
    @Test
    void insertUser() {
        String fullName = RandomValueUtil.getRandomName();
        String phoneNumber = RandomValueUtil.getRandomPhoneNumber();
        String email = RandomValueUtil.getRandomEmail(4);
        Integer orgID = 1;
        int[] roleIds = {1};
        userPatientManager.insertUser(orgID,fullName,email,phoneNumber,roleIds).then().statusCode(200)
                .body("status",equalTo("1"))
                .body("body.userRoles[0].roleName",equalTo("系统管理员"))
                .body("body.fullname",equalTo(fullName))
                .body("body.userName",equalTo(email))
                .body("body.organization.title",equalTo("总部"))
                .body(matchesJsonSchemaInClasspath("responseSchema/backendweb/userpatientManager/insertUser.schema"));
    }

    /**
     * 新增后台用户，必填项缺失检查
     * @param orgId
     * @param fullName
     * @param email
     * @param phoneNumber
     * @param roleIds
     * @param expecMessage
     */
    @ParameterizedTest
    @MethodSource("getMissUserInfo")
    void insertUserMissField(Integer orgId, String fullName, String email, String phoneNumber, int[] roleIds,String expecMessage){
        userPatientManager.insertUser(orgId,fullName,email,phoneNumber,roleIds).then().statusCode(200)
                .body("status",equalTo("0"))
                .body("message",equalTo(expecMessage));
    }
    static Stream<Arguments> getMissUserInfo(){
        String fullName = RandomValueUtil.getRandomName();
        String phoneNumber = RandomValueUtil.getRandomPhoneNumber();
        String email = RandomValueUtil.getRandomEmail(4);
        Integer orgID = 1;
        int[] roleIds = {1};
        Arguments arguments;
        List<Arguments> list = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            List<Object> userList = new ArrayList<>();
            List<String> expecMessList = Arrays.asList("部门唯一标识不能为空;","姓名不能为空;","邮箱不允许为空!;","手机号不允许为空!;","角色唯一标识集合不能NULL;");
            userList.add(orgID);
            userList.add(fullName);
            userList.add(email);
            userList.add(phoneNumber);
            userList.add(roleIds);
            userList.add(expecMessList.get(i));
            userList.set(i,null);
            arguments = arguments(userList.get(0),userList.get(1),userList.get(2),userList.get(3),userList.get(4),userList.get(5));
            list.add(arguments);
        }
        return Stream.of(list.get(0),list.get(1),list.get(2),list.get(3),list.get(4));
    }

    /**
     * 用户列表根据用户姓名查询
     */
    @Test
    void queryUser(){
        //先添加新的后台用户，总部-管理员，
        String fullName = RandomValueUtil.getRandomName();
        String phoneNumber = RandomValueUtil.getRandomPhoneNumber();
        String email = RandomValueUtil.getRandomEmail(4);
        Integer orgID = 1;
        int[] roleIds = {1};
        userPatientManager.insertUser(orgID,fullName,email,phoneNumber,roleIds).then().statusCode(200).body("status",equalTo("1"));
        //根据用户姓名在用户列表进行查询
        userPatientManager.queryUser(fullName,orgID).then().statusCode(200)
                .body("status",equalTo("1"))
                .body("body.pageList[0].userName",equalTo(email))
                .body("body.pageList[0].mobilenumber",equalTo(phoneNumber))
                .body("body.pageList[0].fullname",equalTo(fullName))
                .body(matchesJsonSchemaInClasspath("responseSchema/backendweb/userpatientManager/queryUser.schema"));
    }

    /**
     * 查询所有用户，最新添加用户应处于第一个
     */
    @Test
    void queryAllUser(){
        //先添加新的后台用户，总部-管理员
        String fullName = RandomValueUtil.getRandomName();
        String phoneNumber = RandomValueUtil.getRandomPhoneNumber();
        String email = RandomValueUtil.getRandomEmail(4);
        Integer orgID = 1;
        int[] roleIds = {1};
        //因后端接口记录create_time的逻辑有问题，会导致用户实际的排序错误，为保证测试结果不受影响，临时在这里加入延迟
        //todo:需要后端修改create_time的逻辑bug
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        userPatientManager.insertUser(orgID,fullName,email,phoneNumber,roleIds).then().statusCode(200).body("status",equalTo("1"));
        //用户列表进行所有查询，验证第一个用户
        userPatientManager.queryUser(null,orgID).then().statusCode(200)
                .body("status",equalTo("1"))
                .body("body.pageList[0].userName",equalTo(email))
                .body("body.pageList[0].mobilenumber",equalTo(phoneNumber))
                .body("body.pageList[0].fullname",equalTo(fullName))
                .body(matchesJsonSchemaInClasspath("responseSchema/backendweb/userpatientManager/queryAllUser.schema"));
    }

    /**
     * 登录用户鉴权（总部-管理员）
     */
    @Test
    void getAuthorization(){
        List srcPermissionsArr = Arrays.asList("1","11","111","112","113","2","21","211","212","22","221","222","223","224","3","31","311","312","313","314","315","316","317","4","41","411","412","413","414","42","421","422","423","424","425","426","5","51","511","512","513","514","52","521","522","523","524","53","531","532","533","54","541","542","543","544","545","546","547","548","549","550");
        //查看权限
        userPatientManager.authorization().then().statusCode(200)
                .body("status",equalTo("1"))
                .body("body.permissions",equalTo(srcPermissionsArr))
                .body(matchesJsonSchemaInClasspath("responseSchema/backendweb/userpatientManager/authrorization.schema"));
    }

    /**
     * 添加用户-更新用户-获取更新后用户位置-查询用户更新结果
     */
    @Test
    void updateUser(){
        //先添加新的后台用户，总部-管理员，获取user uid
        String fullName = RandomValueUtil.getRandomName();
        String phoneNumber = RandomValueUtil.getRandomPhoneNumber();
        String email = RandomValueUtil.getRandomEmail(4);
        Integer orgID = 1;
        int[] roleIds = {1};
        Integer uid = userPatientManager.insertUser(orgID,fullName,email,phoneNumber,roleIds).then().statusCode(200)
                .body("status",equalTo("1"))
                .extract().path("body.uid");
        //修改邮箱和电话
        String newPhone = RandomValueUtil.getRandomPhoneNumber();
        String newEmail = RandomValueUtil.getRandomEmail(5);
        HashMap<String,Object> updateMap = new UpdateUser()
                .buildFullName(fullName)
                .buildEmail(newEmail)//修改了邮箱
                .buildMobileNumber(newPhone)//修改了email
                .buildUid(uid)
                .buildUpdateUser();
        //编辑用户，获取用户uid
        userPatientManager.updateUser(updateMap).then().statusCode(200)
                .body("status",equalTo("1"))
                .body("body.mobilenumber",equalTo(newPhone))
                .body("body.userName",equalTo(newEmail))
                .body("body.email",equalTo(newEmail))
                .body(matchesJsonSchemaInClasspath("responseSchema/backendweb/userpatientManager/updateUser.schema"))
                .extract().path("body.uid");
        //通过查询接口，找到更新后user的返回位置,防止有重名用户
        List uidList = userPatientManager.queryUser(fullName,orgID).then().statusCode(200).body("status",equalTo("1"))
                .extract().path("body.pageList.uid");
        Integer userIndex = 0;
        for (int i = 0; i < uidList.size(); i++){
            userIndex = i;
            if (uidList.get(i) == uid){
                break;
            }
        }
        //再通过查询接口，检查update后的查询结果
        userPatientManager.queryUser(fullName,orgID).then().statusCode(200)
                .body("status",equalTo("1"))
                .body(String.format("body.pageList[%d].userName",userIndex),equalTo(newEmail))
                .body(String.format("body.pageList[%d].email",userIndex),equalTo(newEmail))
                .body(String.format("body.pageList[%d].mobilenumber",userIndex),equalTo(newPhone))
                .body(String.format("body.pageList[%d].fullname",userIndex),equalTo(fullName));
    }

    /**
     * 用户状态操作(status 0:禁用 1:启动)
     */
    @Test
    void userDisable() throws Exception {
        //先添加新的后台用户，总部-管理员，获取user uid
        String fullName = RandomValueUtil.getRandomName();
        String phoneNumber = RandomValueUtil.getRandomPhoneNumber();
        String email = RandomValueUtil.getRandomEmail(4);
        Integer orgID = 1;
        int[] roleIds = {1};
        Integer uid = userPatientManager.insertUser(orgID,fullName,email,phoneNumber,roleIds).then().statusCode(200)
                .body("status",equalTo("1"))
                .extract().path("body.uid");
        //将用户禁用，status：0；
        userPatientManager.userDisable(0,uid).then().statusCode(200)
                .body("status",equalTo("1"));
        //检查用户禁用后是否无法登录
        String url = setLoginEnv() + "/rest/user/login";
        String password = "suiren123";
        String passwordEncrypt = RSAEncryptUtil.encrypt(password);
        String body = "{\"userType\":0,\"userName\":\""+ email +"\",\"password\":\""+ passwordEncrypt +"\"}";
        RestAssured.given().log().all().body(body).when().post(url).then().log().all().statusCode(200)
                .body("status",equalTo("0"))
                .body("message",equalTo("用户已被禁用"));
        //再启用，status：1
        userPatientManager.userDisable(1,uid).then().statusCode(200)
                .body("status",equalTo("1"));
        RestAssured.given().log().all().body(body).when().post(url).then().log().all().statusCode(200)
                .body("status",equalTo("1"))
                .body("message",equalTo("登录成功"));
    }

}