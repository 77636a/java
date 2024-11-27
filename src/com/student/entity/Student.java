package com.student.entity;

public class Student {
    private String studentId; // 学号
    private String name;      // 姓名
    private double score;     // 成绩
    private Group group;      // 所属小组班级

    // 构造函数
    public Student(String studentId, String name, double score, Group group) {
        this.studentId = studentId;
        this.name = name;
        this.score = score;
        this.group = group;
    }

    // Getter 和 Setter 方法
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
