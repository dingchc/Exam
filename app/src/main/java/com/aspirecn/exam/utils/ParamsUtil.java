package com.aspirecn.exam.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 类名称: ParamsUtil
 * 类描述: 微服务参数工具类
 * 创建人: 陈书东
 * 创建时间: 2016/10/27 19:57
 * 修改人: 无
 * 修改时间: 无
 * 修改备注: 无
 */
public class ParamsUtil {
    public static String getParams(Map<String, String> map) {
        String json = "";
//        json = json + "{";
        Iterator entryIterator = map.entrySet().iterator();
        JSONObject jsonObject = new JSONObject();
        while (entryIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIterator.next();
            try {
                jsonObject.put(entry.getKey().toString(),entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            json = json + "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\",";
        }
//        json = json.substring(0, json.length() - 1);
//        json = json + "}";
//        LogUtils.i("CSD:json =" + json);

        return jsonObject.toString();

    }
}
