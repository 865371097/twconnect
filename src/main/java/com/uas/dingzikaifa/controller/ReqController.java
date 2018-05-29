package com.uas.dingzikaifa.controller;

import com.uas.dingzikaifa.service.ReqService;
import com.uas.dingzikaifa.util.SecurityUtil;
import com.uas.dingzikaifa.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReqController {

    @Autowired
    private ReqService reqService;

    @RequestMapping("/toProdOut.action")
    public boolean toProdOut(String jsons, String token) throws IllegalAccessException {
        if (SecurityUtil.checkToken(token)) {
            if (!StringUtils.isEmpty(jsons)) {
                return reqService.toProdOut(jsons) ;
            }else {
                throw new IllegalAccessException("请求参数为空");
            }
        } else {
            throw new IllegalAccessException("请求Token有误");
        }
    }

    @RequestMapping("/getVersion")
    public String getVersion() {
        return "version: uasToTopwise-2018052901";
    }
}
