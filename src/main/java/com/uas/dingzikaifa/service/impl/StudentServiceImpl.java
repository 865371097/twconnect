/*
package com.uas.dingzikaifa.service.impl;

import com.uas.dingzikaifa.dao.StudentDao;
import com.uas.dingzikaifa.model.Student;
import com.uas.dingzikaifa.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Student getStuById(int id) {
        System.out.println(id);
        Student student = studentDao.selectById(id);
        return student;
    }

    @Override
    public int insertStu(String name, int id) {
        int i = studentDao.insert(name, id);
        return i;
    }

    @Override
    public List<Student> getAll() {
        List<Student> all = studentDao.getAll();
        return all;
    }

    @Override
    public int insertStu(List<Student> students) {
       return studentDao.insertStu(students);
    }

    @Override
    public void updateStu(String sql) {
        jdbcTemplate.batchUpdate(sql);
    }


}
*/
