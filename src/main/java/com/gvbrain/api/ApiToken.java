package com.gvbrain.api;

import com.gvbrain.api.Utils.RSAEncryptUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ApiToken extends Api{

    private static String appToken;
    private static String backendToken;
    private static String BrainPlatformTestUrl = "http://192.168.1.103/brain";
    private static String BrainPlatformDevUrl = "http://ijixin.com/brain";

    @Override
    public RequestSpecification getDefaultRequestSpecification(String tokenPattern){
        RequestSpecification requestSpecification = super.getDefaultRequestSpecification(tokenPattern);
        try {
            if (tokenPattern.equals("brainPlatform")) {
                return requestSpecification.header("Token", ApiToken.getAppToken(tokenPattern)).contentType(ContentType.JSON);
            }else if (tokenPattern.equals("brainBackend")){
                return requestSpecification.header("Token", ApiToken.getBackendToken(tokenPattern)).contentType(ContentType.JSON);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getBrainPlatformAppToken() {
        String url = setLoginEnv() + "/rest/user/login";
        String body = "{\"userName\":\"18616210504\",\"password\":\"suiren123\",\"userType\":1}";
        return RestAssured.given().log().all()
                .body(body)
                .when().post(url)
                .then().log().all().statusCode(200)
                .extract().response().getHeader("Token");
    }

    public static String getBrainPlatformBackendToken() throws Exception {
        String url = setLoginEnv() + "/rest/user/login";
        String password = "Suiren123";
        String passwordEncrypt = RSAEncryptUtil.encrypt(password);
        String body = "{\"userType\":0,\"userName\":\"qinzhen@green-valley.com\",\"password\":\""+ passwordEncrypt +"\"}";
        return RestAssured.given().log().all()
                .body(body)
                .when().post(url)
                .then().log().all().statusCode(200)
                .extract().response().getHeader("Token");
    }

    //todo:适应不同登录方式或权限的不同类型token
    public static String getBrainAppSpecialToken(){
        return null;
    }

    public static String setLoginEnv(){
        String url = "";
        String currentEnv = ApiConfig.getInstance().currentEnv;
        if (currentEnv.equals("brainPlatformTest")){
            url = BrainPlatformTestUrl;
            return url;
        }
        if (currentEnv.equals("brainPlatformDev")){
            url = BrainPlatformDevUrl;
            return url;
        }
        return url;
    }


    public static String getAppToken(String tokenPattern) throws Exception {
        //todo:支持两种类型的token
        if (appToken == null) {
            if (tokenPattern.equals("brainPlatform")) {
                appToken = getBrainPlatformAppToken();
                return appToken;
            }

            if (tokenPattern.equals("specialToken")) {
                appToken = getBrainAppSpecialToken();
                return appToken;
            }
        }
        return appToken;
    }


    public static String getBackendToken(String tokenPattern) throws Exception {
        if (backendToken == null){
            if (tokenPattern.equals("brainBackend")) {
                backendToken = getBrainPlatformBackendToken();
                return backendToken;
            }
        }
        return backendToken;
    }



}
