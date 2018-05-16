package com.odgather.service.impl;

import com.odgather.dao.StudentDao;
import com.odgather.entity.Student;
import com.odgather.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentDao studentDao;

    @Override
    public List<Student> getStudentList() {
        return studentDao.queryStudentList();
    }
}
