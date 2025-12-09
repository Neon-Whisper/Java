package com.neon;

import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        Classes classes = new Classes();
        classes.addStudent(new Student("张三", 20, "男"));
        classes.addStudent(new Student("李四", 19, "男"));
        classes.addStudent(new Student("王五", 21, "女"));

        System.out.println("排序前的学生信息：");
        classes.display();

        Collections.sort(classes.getStudentList(), new MyComparator());

        System.out.println("\n按年龄升序排序后的学生信息：");
        classes.display();
    }
}