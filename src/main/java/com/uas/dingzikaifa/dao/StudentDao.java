/*package com.uas.dingzikaifa.dao;

import com.uas.dingzikaifa.model.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface StudentDao {
    @Select("select * from students where id= #{id}")
    Student selectById(@Param("id") int id);

    @Insert("INSERT INTO students(id, name, cid) VALUES(#{id}, #{name}, 9)")
    int insert(@Param("name") String name, @Param("id") int id);

    @Select("select * from students")
    List<Student> getAll();

    @Select("select count(1) from students where id =#{id}")
    int getCount(@Param("id") int id);

  *//*  @Insert("insert into students (id, name ) values(#{id}, #{name})")
    int insertStu(Student student);*//*

    @Insert("insert into students (id, name ) values(#{id}, #{name})")
    int insertStu(List<Student> students);

}*/
