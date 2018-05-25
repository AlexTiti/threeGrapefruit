package com.findtech.threePomelos.utils.weather;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/02
 */

public class UrlUtils {

    public static  String getWeatherUrl(String location ){
        long second = System.currentTimeMillis() / 1000;
        String builder = "https://api.seniverse.com/v3/weather/now.json?" + "ts=" + second + "&uid=U7054AA301" + "&sig=" +
                sign(second) + "&location=" + location +
                "&language = zh-Hans&unit = c";
        return builder;
    }

    public static  String getAirUrl(String location ){
        long second = System.currentTimeMillis() / 1000;
        String builder = "https://api.seniverse.com/v3/air/now.json?" + "ts=" + second + "&uid=U7054AA301" + "&sig=" +
                sign(second) + "&language = zh-Hans" +
                "&location=" + location +
                "&scope=city";
        return builder;
    }

    public static  String getLifeUrl(String location ){
        long second = System.currentTimeMillis() / 1000;
        String builder = "https://api.seniverse.com/v3/life/suggestion.json?" + "ts=" + second + "&uid=U7054AA301" + "&sig=" +
                sign(second) + "&language = zh-Hans" +
                "&location=" + location;

        return builder;
    }

    public static String sign(long second){
        String string = "ts=" + second + "&uid=U7054AA301";
        byte[] b = new byte[0];
        try {
            b = HAMCSHA.HmacSHA1Encrypt(string, "1limijcgcsj4vjqd");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64Utils.encode(b);
    }
}
