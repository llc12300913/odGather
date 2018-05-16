package dao;

import com.odgather.dao.StudentDao;
import com.odgather.entity.Student;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StudentDaoTest extends BaseTest {
    @Autowired
    private StudentDao studentDao;

    @Test
    public void demo1(){
        List<Student> studentList = studentDao.queryStudentList();
        System.out.println(studentList.size());
    }
}
