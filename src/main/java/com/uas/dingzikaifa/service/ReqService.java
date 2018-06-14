package com.uas.dingzikaifa.service;

import java.util.Map;

public interface ReqService {
    Map<String, Object> toProdOut(String jsons) throws IllegalAccessException;
}
