/*
package com.uas.dingzikaifa.controller;


import com.uas.dingzikaifa.model.Student;
import com.uas.dingzikaifa.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {
   @Autowired
    private StudentService studentService;

   @RequestMapping("/get")
    public String get(int id) {
       Student stu = studentService.getStuById(id);
       return stu.getName();
   }

    @RequestMapping("/print")
    public String print() {
        return "started";
    }

    @RequestMapping("/insert")
    public int insert(int id, String name) {
        int i = studentService.insertStu(name, id);
        return i;
    }

    @RequestMapping("/getAll")
    public String getAll() {
        List<Student> all = studentService.getAll();
        System.out.println(all);
        return "success";
    }

    @RequestMapping("/insertStu")
    public void insertStu() {
       studentService.updateStu("update students set name='小熊' where id=1");
    }
}
*/
