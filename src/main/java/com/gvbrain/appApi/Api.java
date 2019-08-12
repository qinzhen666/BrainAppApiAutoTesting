package com.gvbrain.appApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.useRelaxedHTTPSValidation;

public class Api {

    //信任HTTPS证书
    public  Api(){
        useRelaxedHTTPSValidation();
    }

    //对RequestSpecification进行初步封装
    public RequestSpecification getDefaultRequestSpecification(String tokenPattern){
        RequestSpecification requestSpecification = given().log().all();
        return requestSpecification;
    }

    //加载json文件，对body值进行修改
    public String loadJsonBody(String jsonPath, HashMap<String,Object> map){
        DocumentContext context = JsonPath.parse(Api.class.getResourceAsStream(jsonPath));
        if (map == null){
            return context.jsonString();
        }
        map.entrySet().forEach(entry->{
            context.set(entry.getKey(),entry.getValue());
        });
        return context.jsonString();
    }

    public Restful getApiFromYaml(String yamlPath){
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(Api.class.getResource(yamlPath),Restful.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Restful updateApiFromMap(Restful restful, HashMap<String,Object> map){
        if (map == null){
            return restful;
        }
        if (restful.method.toLowerCase().contains("get")){
            map.entrySet().forEach(entry->{
                restful.query.put(entry.getKey(),entry.getValue());
            });
        }
        if (restful.method.toLowerCase().contains("post")){
            if (map.containsKey("_body")){
                restful.body = map.get("_body").toString();
            }
            if (map.containsKey("_file")){
                String filePath = map.get("_file").toString();
                map.remove("_file");
                restful.body = loadJsonBody(filePath,map);
            }
        }
        return restful;
    }

    public Response getResponseFromApi(Restful restful,String tokenPattern){
        RequestSpecification requestSpecification = getDefaultRequestSpecification(tokenPattern);
        //设置请求参数
        if (restful.query != null){
            restful.query.entrySet().forEach(entry->{
                requestSpecification.queryParam(entry.getKey(),entry.getValue());
            });
        }
        if (restful.body != null){
            requestSpecification.body(restful.body);
        }
        //设置请求环境地址
        String[] upUrl = updateUrl(restful.url);
        return requestSpecification.log().all()
                .header("Host",upUrl[0])
                .request(restful.method,upUrl[1])
                .then().log().all().extract().response();
    }

    /**
     * 替换URL中的请求地址，适配不同测试环境
     * @param url
     * @return
     */
    public String[] updateUrl(String url){
        //fixed:多环境支持，替换url中ip地址，更新header的Host
        HashMap<String,String> Hosts = ApiConfig.getInstance().env.get(ApiConfig.getInstance().currentEnv);
        final String[] Host = new String[1];
        final String[] newUrl = new String[1];
        Hosts.entrySet().forEach(entry->{
            if (url.contains(entry.getKey())){
                Host[0] = entry.getKey();
                newUrl[0] = url.replace(entry.getKey(),entry.getValue());
            }
        });
        return new String[]{Host[0], newUrl[0]};
    }

    /**
     * 根据yaml生成接口定义并发送
     * @param yamlPath
     * @param map
     * @param tokenPattern
     * @return
     */
    public Response getResponseFromYaml(String yamlPath, HashMap<String,Object> map,String tokenPattern){
        //fixed:根据yaml生成接口定义并发送
        Restful restful = getApiFromYaml(yamlPath);
        restful = updateApiFromMap(restful, map);
        return getResponseFromApi(restful,tokenPattern);
    }

    /**
     * 判断字符串中是否都是数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        //如果是数字，创建new BigDecimal()时肯定不会报错，那就可以直接返回true
        //BigDecimal value = new BigDecimal(oldValue);可以把科学记数法转换为普通数字表示
        try {
            new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }
        return true;
    }


}
