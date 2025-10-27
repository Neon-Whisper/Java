package com.neon;

public class Student {
    private String sName;
    private int sAge;
    private String sSex;

    public Student(String sName, int sAge, String sSex) {
        this.sName = sName;
        this.sAge = sAge;
        this.sSex = sSex;
    }

    public String getSName() {
        return sName;
    }

    public int getSAge() {
        return sAge;
    }

    public String getSSex() {
        return sSex;
    }

    @Override
    public String toString() {
        return "Student{name='" + sName + "', age=" + sAge + ", sex='" + sSex + "'}";
    }
}
