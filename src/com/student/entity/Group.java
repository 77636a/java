package com.student.entity;

import java.util.List;

public class Group {
    private String groupName; // 小组名称
    private List<Student> students; // 学生列表

    // 构造函数
    public Group(String groupName, List<Student> students) {
        this.groupName = groupName;
        this.students = students;
    }

    // Getter 和 Setter 方法
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
