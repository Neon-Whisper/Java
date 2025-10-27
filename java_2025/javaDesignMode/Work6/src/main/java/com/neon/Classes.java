package com.neon;

import java.util.ArrayList;
import java.util.Iterator;

public class Classes {
    private ArrayList<Student> studentList = new ArrayList<>();

    public void addStudent(Student student) {
        studentList.add(student);
    }

    public void display() {
        Iterator<Student> iterator = studentList.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    public ArrayList<Student> getStudentList() {
        return studentList;
    }
}