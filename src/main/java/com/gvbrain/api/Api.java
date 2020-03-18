package com.gvbrain.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.sstoehr.harreader.HarReader;
import de.sstoehr.harreader.model.Har;
import de.sstoehr.harreader.model.HarEntry;
import de.sstoehr.harreader.model.HarRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.HashMap;

import static io.restassured.RestAssured.*;

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

    public Restful getApiFromHar(String harJsonPath ,String urlPattern){
        HarReader harReader = new HarReader();
        Restful restful = new Restful();
        try {
            //读取har文件，获取文件路径，对路径进行编码设置
            Har har = harReader.readFromFile(new File(URLDecoder.decode(getClass().getResource(harJsonPath).getPath(), "utf-8")));
            HarRequest harRequest = new HarRequest();
            //遍历entries,正则匹配到包含API信息的object
            Boolean match = false;
            for (HarEntry entry : har.getLog().getEntries()){
                harRequest= entry.getRequest();
                if (harRequest.getUrl().matches(urlPattern)){
                    match = true;
                    break;
                }
            }
            if (match == false){
                throw new Exception("【ERROR】未找到正确的URL");
            }
            if (harRequest.getUrl().contains("?")){
                restful.url = harRequest.getUrl().split("\\?")[0];
            }else {
                restful.url = harRequest.getUrl();
            }
            harRequest.getQueryString().forEach(q->{
                restful.query.put(q.getName(),q.getValue());
            });
            restful.method = harRequest.getMethod().toString();
            restful.body = harRequest.getPostData().getText();
            return restful;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
            if (map.containsKey("_postPara")){
                map.remove("_postPara");
                map.entrySet().forEach(entry->{
                    restful.query.put(entry.getKey(),entry.getValue());
                });
            }else {
                DocumentContext context = JsonPath.parse(restful.body);
                map.entrySet().forEach(key->{
                    context.set(key.getKey(),key.getValue());
                });
                restful.body = context.jsonString();
            }
        }
        return restful;
    }

    public Response getResponseFromApi(Restful restful,String tokenPattern){
        RequestSpecification requestSpecification = getDefaultRequestSpecification(tokenPattern);
        //设置请求参数
        if (restful.query != null){
            if (restful.query.get("_multiPath") != null){//判断是否需要上传文件
                String multiPath = (String) restful.query.get("_multiPath");
                try {
                    final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream(multiPath));
                    restful.query.remove("_multiPath");
                    restful.query.entrySet().forEach(entry->{
                        requestSpecification.queryParam(entry.getKey(),entry.getValue());
                    });
                    requestSpecification.contentType("multipart/form-data").multiPart("file", "myFile", bytes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                restful.query.entrySet().forEach(entry->{
                    requestSpecification.queryParam(entry.getKey(),entry.getValue());
                });
            }
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
     * 根据har生成接口定义并发送
     * @param harJsonPath
     * @param urlParttern
     * @param map
     * @param tokenPattern
     * @return
     */
    public Response getResponseFromHar(String harJsonPath,String urlPattern,HashMap<String,Object> map,String tokenPattern){
        Restful restful = getApiFromHar(harJsonPath, urlPattern);
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
