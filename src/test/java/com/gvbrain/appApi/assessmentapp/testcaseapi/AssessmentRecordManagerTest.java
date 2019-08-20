package com.gvbrain.appApi.assessmentapp.testcaseapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        String filePath = "/Users/qinzhen/Documents/TestDev/APITest/BrainAppApiAutoTesting/src/main/resources/data/assessmentapp/assessmentRecordManager/CTD4.jpg";
        assessmentRecordManager.uploadFile(filePath).then().statusCode(200).body("status",equalTo("1"));
    }
}