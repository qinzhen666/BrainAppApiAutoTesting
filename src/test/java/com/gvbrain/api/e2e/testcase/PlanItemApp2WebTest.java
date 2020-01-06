package com.gvbrain.api.e2e.testcase;

import com.gvbrain.api.Utils.RandomValueUtil;
import com.gvbrain.api.backendweb.testcaseapi.PlanItemManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

class PlanItemApp2WebTest {


    private PlanItemManager planItemManager;

    @BeforeEach
    void setup(){
        if (planItemManager == null){
            planItemManager = new PlanItemManager();
        }
    }




}
