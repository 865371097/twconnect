package com.uas.dingzikaifa.controller;

import com.uas.dingzikaifa.service.ReqService;
import com.uas.dingzikaifa.util.SecurityUtil;
import com.uas.dingzikaifa.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dingzikaifa")
public class ReqController {

    @Autowired
    private ReqService reqService;

    @RequestMapping("/toProdOut.action")
    public Map<String, Object> toProdOut(String jsons, String token) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<String, Object>();
        boolean suc = false;
        String message = "";
        if (SecurityUtil.checkToken(token)) {
            if (!StringUtils.isEmpty(jsons)) {
                return reqService.toProdOut(jsons);
            }else {
                message = "请求参数为空";
            }
        } else {
            message = "请求Token有误";
        }
        map.put("success", suc);
        map.put("result", message);
        return map;
    }

    @RequestMapping("/getProductWH.action")
    public Map<String, Object> getProductWH (String info, String token) {
        Map<String, Object> map = new HashMap<String, Object>();
        String message;
        if (SecurityUtil.checkToken(token)) {
            if (!StringUtils.isEmpty(info)) {
                return reqService.getWarehouse(info);
            }else {
                message = "请求参数为空";
            }
        } else {
            message = "请求Token有误";
        }
        map.put("success", "fail");
        map.put("result", message);
        return map;
    }

    @RequestMapping("/getVersion")
    public String getVersion() {
        return "version: uasToTopwise-2018070401";
    }
}
