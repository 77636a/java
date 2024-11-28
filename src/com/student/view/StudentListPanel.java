package com.student.view;

import com.student.entity.Group;
import com.student.entity.Student;
import com.student.util.Constant;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * 学生列表面板类
 * 用于显示、修改和删除学生信息
 * 继承自JPanel，提供图形化界面
 */
public class StudentListPanel extends JPanel {
    // 表格列头定义
    String[] headers = {"学号", "姓名", "小组"};
    // 界面组件声明
    JTable studentTable;                     // 学生信息表格
    JTextField txtId = new JTextField();     // 学号输入框
    JTextField txtName = new JTextField();   // 姓名输入框
    JComboBox<String> cmbGroup = new JComboBox<>();  // 小组选择下拉框
    JButton btnEdit = new JButton("修改");   // 修改按钮
    JButton btnDelete = new JButton("删除"); // 删除按钮

    /**
     * 构造方法：初始化学生列表面板的界面组件
     */
    public StudentListPanel() {
        // 设置面板边框和布局
        this.setBorder(new TitledBorder(new EtchedBorder(), "学生列表"));
        this.setLayout(new BorderLayout());

        // 初始化表格，设置为不可编辑
        DefaultTableModel tableModel = new DefaultTableModel(new String[0][0], headers);
        studentTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // 设置表格不可直接编辑
            }
        };
        studentTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        this.add(scrollPane, BorderLayout.CENTER);

        // 初始化底部面板
        JPanel btnPanel = new JPanel();
        btnPanel.add(txtId);
        txtId.setPreferredSize(new Dimension(100, 30));
        btnPanel.add(txtName);
        txtName.setPreferredSize(new Dimension(200, 30));
        btnPanel.add(cmbGroup);
        cmbGroup.setPreferredSize(new Dimension(100, 30));
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        this.add(btnPanel, BorderLayout.SOUTH);

        // 加载小组下拉列表
        loadGroups();
        
        // 更新学生列表
        updateStudentList();

        // 表格选择监听器
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                txtId.setText((String) studentTable.getValueAt(selectedRow, 0));
                txtName.setText((String) studentTable.getValueAt(selectedRow, 1));
                cmbGroup.setSelectedItem(studentTable.getValueAt(selectedRow, 2));
            }
        });

        // 修改按钮监听器
        btnEdit.addActionListener(e -> {
            if (validateAndEdit()) {
                updateStudentList();
            }
        });

        // 删除按钮监听器
        btnDelete.addActionListener(e -> {
            if (deleteStudent()) {
                updateStudentList();
            }
        });
    }

    /**
     * 加载小组列表
     * 从文件系统读取所有小组信息并添加到下拉框中
     */
    private void loadGroups() {
        cmbGroup.removeAllItems();
        cmbGroup.addItem("请选择小组");

        if (Constant.CLASS_PATH == null || Constant.CLASS_PATH.isEmpty()) {
            return;
        }

        File groupsDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/groups");
        if (groupsDir.exists() && groupsDir.isDirectory()) {
            File[] groupDirs = groupsDir.listFiles(File::isDirectory);
            if (groupDirs != null) {
                for (File groupDir : groupDirs) {
                    cmbGroup.addItem(groupDir.getName());
                }
            }
        }
    }

    /**
     * 更新学生列表显示
     * 从文件系统读取所有学生信息并显示在表格中
     */
    private void updateStudentList() {
        if (Constant.CLASS_PATH == null || Constant.CLASS_PATH.isEmpty()) {
            return;
        }

        File studentsDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/students");
        if (!studentsDir.exists() || !studentsDir.isDirectory()) {
            return;
        }

        File[] studentFiles = studentsDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (studentFiles == null) {
            return;
        }

        List<String[]> studentList = new ArrayList<>();
        for (File file : studentFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String[] studentInfo = new String[3];
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("学号：")) {
                        studentInfo[0] = line.substring(3).trim();
                    } else if (line.startsWith("姓名：")) {
                        studentInfo[1] = line.substring(3).trim();
                    } else if (line.startsWith("小组：")) {
                        studentInfo[2] = line.substring(3).trim();
                    }
                }
                studentList.add(studentInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 更新表格
        String[][] data = studentList.toArray(new String[0][]);
        DefaultTableModel model = new DefaultTableModel(data, headers);
        studentTable.setModel(model);
    }

    /**
     * 验证并修改学生信息
     * @return 修改是否成功
     */
    private boolean validateAndEdit() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择学生", "", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        String studentId = txtId.getText().trim();
        String studentName = txtName.getText().trim();
        String groupName = (String) cmbGroup.getSelectedItem();

        if (studentId.isEmpty() || studentName.isEmpty() || 
            groupName == null || groupName.equals("请选择小组")) {
            JOptionPane.showMessageDialog(this, "请填写完整信息", "", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        try {
            // 1. 更新学生文件
            String oldId = (String) studentTable.getValueAt(selectedRow, 0);
            File oldFile = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/students/" + oldId + ".txt");
            File newFile = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/students/" + studentId + ".txt");

            if (!oldId.equals(studentId) && newFile.exists()) {
                JOptionPane.showMessageDialog(this, "该学号已存在", "", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // 2. 从旧小组文件中删除学生
            String oldGroup = (String) studentTable.getValueAt(selectedRow, 2);
            updateGroupStudentsList(oldGroup, oldId, null, true);

            // 3. 添加到新小组文件
            updateGroupStudentsList(groupName, studentId, studentName, false);

            // 4. 更新学生信息文件
            if (!oldId.equals(studentId)) {
                oldFile.delete();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(newFile))) {
                writer.write("学号：" + studentId);
                writer.newLine();
                writer.write("姓名：" + studentName);
                writer.newLine();
                writer.write("小组：" + groupName);
                writer.newLine();
                writer.write("修改时间：" + new Date());
            }

            JOptionPane.showMessageDialog(this, "修改成功", "", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "修改失败：" + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private boolean deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择学生", "", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        if (JOptionPane.showConfirmDialog(this, "确认删除该学生？", "", 
            JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return false;
        }

        try {
            String studentId = (String) studentTable.getValueAt(selectedRow, 0);
            String groupName = (String) studentTable.getValueAt(selectedRow, 2);

            // 1. 删除学生文件
            File studentFile = new File(Constant.FILE_PATH + Constant.CLASS_PATH + 
                "/students/" + studentId + ".txt");
            studentFile.delete();

            // 2. 从小组文件中删除学生
            updateGroupStudentsList(groupName, studentId, null, true);

            JOptionPane.showMessageDialog(this, "删除成功", "", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "删除失败：" + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void updateGroupStudentsList(String groupName, String studentId, 
        String studentName, boolean isRemove) throws IOException {
        
        File groupDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/groups/" + groupName);
        File studentsFile = new File(groupDir, "students.txt");

        if (isRemove) {
            if (!studentsFile.exists()) {
                return;
            }

            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(studentsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith(studentId + ",")) {
                        lines.add(line);
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(studentsFile))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(studentsFile, true))) {
                writer.write(studentId + "," + studentName);
                writer.newLine();
            }
        }
    }
}
