package org.example.oss.util;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    public static Map<String, Object> createResponse(int code, Object data, String msg) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("data", data);
        response.put("msg", msg);
        return response;
    }
}
