package com.gvbrain.api.backendweb.testcase;

import com.gvbrain.api.Utils.RandomValueUtil;
import com.gvbrain.api.backendweb.testcaseapi.PlanItemManager;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.*;

class PatientItemManagerTest {

    private PlanItemManager planItemManager;

    @BeforeEach
    void setup(){
        if (planItemManager == null){
            planItemManager = new PlanItemManager();
        }
    }

    /**
     * 查询所有测评方案信息
     */
    @Test
    void queryAllPlanInBackend(){
        planItemManager.queryPlan(null).then().statusCode(200).body("status",equalTo("1"));
    }

    /**
     * 新建固定测评方案->后台检查返回医院和量表->查询此方案->检查返回医院和量表
     */
    @Test
    void insertGDPlan(){
        //新增固定测评方案
        String planDescribe = RandomValueUtil.getRandomAssessmentPlanDescribe();
        String PlanName = RandomValueUtil.getRandomAssessmentPlanName();
        Integer classify = 1;
        List<Integer> hospitalUids = Arrays.asList(RandomValueUtil.getNum(1,20),RandomValueUtil.getNum(21,40));
        List<Integer> items = Arrays.asList(RandomValueUtil.getNum(1,8),RandomValueUtil.getNum(1,8));
        //对List hospitalUids去重排序，作为服务端处理结果校验备用
        LinkedHashSet<Integer> set1 = new LinkedHashSet<>(hospitalUids.size());
        List<Integer> uniqueHosUid = new ArrayList<>(hospitalUids.size());
        set1.addAll(hospitalUids);
        uniqueHosUid.addAll(set1);
        Collections.sort(uniqueHosUid);
        //对List items去重排序，作为服务端处理结果校验备用
        LinkedHashSet<Integer> set = new LinkedHashSet<>(items.size());
        List<Integer> uniqueItems = new ArrayList<>(items.size());
        set.addAll(items);
        uniqueItems.addAll(set);
        Collections.sort(uniqueItems);
        String response = planItemManager.insertPlan(planDescribe,PlanName,classify,hospitalUids,items).then().statusCode(200)
                .body("status",equalTo("1"))
                .body(matchesJsonSchemaInClasspath("responseSchema/backendweb/planitemManager/insertGDPlan.schema"))
                .extract().asString();
        List respHosUids = from(response).get("body.hospitals.uid");
        Collections.sort(respHosUids);
        List respItemUid = from(response).get("body.assessmentItems.uid");
        Collections.sort(respItemUid);
        //校验创建后返回的医院和量表是否正确
        MatcherAssert.assertThat(uniqueHosUid,equalTo(respHosUids));
        MatcherAssert.assertThat(uniqueItems,equalTo(respItemUid));
        //查询固定测评方案,校验查询结果
        String queryResponse = planItemManager.queryPlan(PlanName).then().statusCode(200)
                .body("status",equalTo("1"))
                /*.body("body.assessmentPlans.items.uid",equalTo(uniqueItems))
                .body("body.assessmentPlans.hospitals",equalTo(uniqueHosUid))*/
                .body(matchesJsonSchemaInClasspath("responseSchema/backendweb/planitemManager/queryplan.schema"))
                .extract().asString();
        List queryHosUids = from(queryResponse).get("body.assessmentPlans[0].hospitals.uid");
        Collections.sort(queryHosUids);
        List queryItems = from(queryResponse).get("body.assessmentPlans[0].items.uid");
        Collections.sort(queryItems);
        MatcherAssert.assertThat(uniqueHosUid,equalTo(queryHosUids));
        MatcherAssert.assertThat(uniqueItems,equalTo(queryItems));
    }
}
