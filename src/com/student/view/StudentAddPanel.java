package com.student.view;

import com.student.entity.Group;
import com.student.util.Constant;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * 学生添加面板类
 * 用于实现新增学生的界面功能
 * 包含学号、姓名输入和小组选择等功能
 */
public class StudentAddPanel extends JPanel {
    // 界面组件声明
    private JTextField txtId;        // 学号输入框
    private JTextField txtName;      // 姓名输入框
    private JComboBox<String> cmbGroup;  // 小组选择下拉框

    /**
     * 构造方法：初始化学生添加面板的界面组件
     */
    public StudentAddPanel() {
        this.setLayout(null);
        this.setBorder(new TitledBorder(new EtchedBorder(), "新增学生"));
        
        // 初始化界面组件
        JLabel lblId = new JLabel("学号：");
        txtId = new JTextField();
        JLabel lblName = new JLabel("姓名：");
        txtName = new JTextField();
        JLabel lblGroup = new JLabel("小组:");
        cmbGroup = new JComboBox<>();
        JButton btnConfirm = new JButton("确认");

        // 设置组件位置和大小
        lblId.setBounds(200, 60, 100, 30);
        txtId.setBounds(200, 100, 100, 30);
        lblName.setBounds(200, 140, 100, 30);
        txtName.setBounds(200, 180, 200, 30);
        lblGroup.setBounds(200, 220, 100, 30);
        cmbGroup.setBounds(200, 260, 100, 30);
        btnConfirm.setBounds(200, 300, 100, 30);

        // 加载小组列表到下拉框
        loadGroups();

        // 添加确认按钮的点击事件监听器
        btnConfirm.addActionListener(e -> addStudent());
    }

    /**
     * 加载小组列表
     * 从文件系统读取所有小组信息并添加到下拉框中
     */
    private void loadGroups() {
        cmbGroup.removeAllItems();
        cmbGroup.addItem("请选择小组");  // 添加默认选项

        // 检查班级路径是否存在
        if (Constant.CLASS_PATH == null || Constant.CLASS_PATH.isEmpty()) {
            return;
        }

        // 读取小组文件夹
        File groupsDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/groups");
        if (!groupsDir.exists() || !groupsDir.isDirectory()) {
            return;
        }

        // 获取所有小组文件夹并添加到下拉框
        File[] groupDirs = groupsDir.listFiles(File::isDirectory);
        if (groupDirs != null) {
            for (File groupDir : groupDirs) {
                cmbGroup.addItem(groupDir.getName());
            }
        }
    }

    /**
     * 添加学生信息
     * 将学生信息保存到文件系统中，包括个人信息文件和小组成员文件
     */
    private void addStudent() {
        // 获取并验证输入信息
        String studentId = txtId.getText().trim();
        String studentName = txtName.getText().trim();
        String groupName = (String) cmbGroup.getSelectedItem();

        // 输入验证
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
            // 创建students文件夹（如果不存在）
            File studentsDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/students");
            if (!studentsDir.exists()) {
                studentsDir.mkdirs();
            }

            // 检查学号是否已存在
            File studentFile = new File(studentsDir, studentId + ".txt");
            if (studentFile.exists()) {
                JOptionPane.showMessageDialog(this, "该学号已存在", "", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 写入学生个人信息文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(studentFile))) {
                writer.write("学号：" + studentId);
                writer.newLine();
                writer.write("姓名：" + studentName);
                writer.newLine();
                writer.write("小组：" + groupName);
                writer.newLine();
                writer.write("加入时间：" + new java.util.Date());
            }

            // 更新小组成员文件
            File groupDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/groups/" + groupName);
            File groupStudentsFile = new File(groupDir, "students.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(groupStudentsFile, true))) {
                writer.write(studentId + "," + studentName);
                writer.newLine();
            }

            // 清空输入框并显示成功消息
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
