package com.gvbrain.appApi.assessmentapp.testcaseapi;

import com.gvbrain.appApi.Utils.RandomValueUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

import static org.junit.jupiter.api.Assertions.*;

class DoctorManagerTest {

    DoctorManager doctorManager;

    @BeforeEach
    void setup(){
        if (doctorManager == null){
            doctorManager = new DoctorManager();
        }
    }

    /**
     * 重置密码错误
     */
    @Test
    void changePwdFalse() {
        String currentPassword = "suiren123";
        String newPassword = RandomValueUtil.getRandomAlphabet(6)+RandomValueUtil.getNum(0,9);
        String renewPassword = newPassword + 1;
        doctorManager.changePwd(currentPassword,newPassword,renewPassword).then().statusCode(200)
                .body("status",equalTo("0"))
                .body("message",equalTo("两次输入的密码不一致，请重新输入"));
    }

    /**
     * 问题反馈
     */
    @Test
    void feedBack() {
        String feedBackInfo = "问题反馈" + RandomValueUtil.getRandomAlphabet(3) +RandomValueUtil.getRandomPhoneNumber();
        doctorManager.feedBack(feedBackInfo).then().statusCode(200).body("status",equalTo("1"));
    }
}