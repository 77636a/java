package com.student.view;

import com.student.entity.Group;
import com.student.util.Constant;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.io.*;
import java.util.Date;
import java.util.List;

public class StudentAddPanel extends JPanel {
    private JTextField txtId;
    private JTextField txtName;
    private JComboBox<String> cmbGroup;

    public StudentAddPanel() {
        this.setLayout(null);
        this.setBorder(new TitledBorder(new EtchedBorder(), "新增学生"));
        
        // 初始化组件
        JLabel lblId = new JLabel("学号：");
        txtId = new JTextField();
        JLabel lblName = new JLabel("姓名：");
        txtName = new JTextField();
        JLabel lblGroup = new JLabel("小组:");
        cmbGroup = new JComboBox<>();
        JButton btnConfirm = new JButton("确认");

        // 添加组件
        this.add(lblId);
        this.add(txtId);
        this.add(lblName);
        this.add(txtName);
        this.add(lblGroup);
        this.add(cmbGroup);
        this.add(btnConfirm);

        // 设置位置
        lblId.setBounds(200, 60, 100, 30);
        txtId.setBounds(200, 100, 100, 30);
        lblName.setBounds(200, 140, 100, 30);
        txtName.setBounds(200, 180, 200, 30);
        lblGroup.setBounds(200, 220, 100, 30);
        cmbGroup.setBounds(200, 260, 100, 30);
        btnConfirm.setBounds(200, 300, 100, 30);

        // 加载小组列表
        loadGroups();

        // 添加确认按钮事件
        btnConfirm.addActionListener(e -> addStudent());
    }

    private void loadGroups() {
        cmbGroup.removeAllItems();
        cmbGroup.addItem("请选择小组");

        if (Constant.CLASS_PATH == null || Constant.CLASS_PATH.isEmpty()) {
            return;
        }

        // 读取小组文件夹
        File groupsDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/groups");
        if (!groupsDir.exists() || !groupsDir.isDirectory()) {
            return;
        }

        File[] groupDirs = groupsDir.listFiles(File::isDirectory);
        if (groupDirs != null) {
            for (File groupDir : groupDirs) {
                cmbGroup.addItem(groupDir.getName());
            }
        }
    }

    private void addStudent() {
        // 验证输入
        String studentId = txtId.getText().trim();
        String studentName = txtName.getText().trim();
        String groupName = (String) cmbGroup.getSelectedItem();

        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写学号", "", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (studentName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写学生姓名", "", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (groupName == null || groupName.equals("请选择小组")) {
            JOptionPane.showMessageDialog(this, "请选择小组", "", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            // 1. 创建students文件夹（如果不存在）
            File studentsDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/students");
            if (!studentsDir.exists()) {
                studentsDir.mkdirs();
            }

            // 2. 在students文件夹中创建学生信息文件
            File studentFile = new File(studentsDir, studentId + ".txt");
            if (studentFile.exists()) {
                JOptionPane.showMessageDialog(this, "该学号已存在", "", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 写入学生信息到students文件夹
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(studentFile))) {
                writer.write("学号：" + studentId);
                writer.newLine();
                writer.write("姓名：" + studentName);
                writer.newLine();
                writer.write("小组：" + groupName);
                writer.newLine();
                writer.write("加入时间：" + new java.util.Date());
            }

            // 3. 更新小组文件中的学生信息
            File groupDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/groups/" + groupName);
            File groupStudentsFile = new File(groupDir, "students.txt");

            // 追加写入学生信息到小组文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(groupStudentsFile, true))) {
                writer.write(studentId + "," + studentName);
                writer.newLine();
            }

            // 清空输入框
            txtId.setText("");
            txtName.setText("");
            cmbGroup.setSelectedIndex(0);

            JOptionPane.showMessageDialog(this, "添加学生成功", "", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "添加学生失败：" + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
