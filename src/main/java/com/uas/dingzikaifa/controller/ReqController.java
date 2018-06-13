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
        if (SecurityUtil.checkToken(token)) {
            if (!StringUtils.isEmpty(jsons)) {
                map.put("success", true);
                map.put("code", reqService.toProdOut(jsons));
                return map;
            }else {
                throw new IllegalAccessException("请求参数为空");
            }
        } else {
            throw new IllegalAccessException("请求Token有误");
        }
    }

        @RequestMapping("/getVersion")
    public String getVersion() {
        return "version: uasToTopwise-2018061301";
    }
}
