package com.student.view;

import com.student.entity.Group;
import com.student.util.Constant;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GroupListPanel extends JPanel {
    String[] headers = {"序号", "小组名称", "分数"};
    JTable classTable;
    JTextField txtName = new JTextField();
    JTextField txtScore = new JTextField();
    JButton btnEdit = new JButton("修改");
    JButton btnDelete = new JButton("删除");

    public GroupListPanel() {
        this.setBorder(new TitledBorder(new EtchedBorder(), "小组列表"));
        this.setLayout(new BorderLayout());

        // 初始化表格
        DefaultTableModel tableModel = new DefaultTableModel(new String[0][0], headers);
        classTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        classTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(classTable);
        this.add(scrollPane, BorderLayout.CENTER);

        // 初始化按钮面板
        JPanel btnPanel = new JPanel();
        btnPanel.add(txtName);
        txtName.setPreferredSize(new Dimension(200, 30));
        btnPanel.add(txtScore);
        txtScore.setPreferredSize(new Dimension(100, 30));
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        this.add(btnPanel, BorderLayout.SOUTH);

        // 更新小组列表
        updateGroupList();

        // 选择监听器
        classTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = classTable.getSelectedRow();
            if (selectedRow >= 0) {
                txtName.setText((String) classTable.getValueAt(selectedRow, 1));
                txtScore.setText((String) classTable.getValueAt(selectedRow, 2));
            }
        });

        // 修改按钮监听器
        btnEdit.addActionListener(e -> {
            int selectedRow = classTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "请先选择小组", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            updateGroupInfo(selectedRow);
        });

        // 删除按钮监听器
        btnDelete.addActionListener(e -> {
            int selectedRow = classTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "请先选择小组", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            deleteGroup(selectedRow);
        });
    }

    private void updateGroupList() {
        if (Constant.CLASS_PATH == null || Constant.CLASS_PATH.isEmpty()) {
            return;
        }

        // 获取当前班级的小组文件夹
        File groupsDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/groups");
        if (!groupsDir.exists() || !groupsDir.isDirectory()) {
            return;
        }

        // 获取所有小组文件夹
        File[] groupDirs = groupsDir.listFiles(File::isDirectory);
        if (groupDirs == null) {
            return;
        }

        // 准备表格数据
        String[][] data = new String[groupDirs.length][3];
        for (int i = 0; i < groupDirs.length; i++) {
            data[i][0] = String.valueOf(i + 1);  // 序号
            data[i][1] = groupDirs[i].getName(); // 小组名称
            
            // 读取小组分数
            File scoreFile = new File(groupDirs[i], groupDirs[i].getName() + ".txt");
            data[i][2] = readGroupScore(scoreFile);
        }

        // 更新表格
        DefaultTableModel model = new DefaultTableModel(data, headers);
        classTable.setModel(model);
    }

    private String readGroupScore(File file) {
        if (!file.exists()) {
            return "0";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("分数：")) {
                    return line.substring(3).trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0";
    }

    private void updateGroupInfo(int selectedRow) {
        String groupName = (String) classTable.getValueAt(selectedRow, 1);
        String newScore = txtScore.getText();
        
        File groupDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/groups/" + groupName);
        File groupFile = new File(groupDir, groupName + ".txt");

        try {
            // 读取现有内容
            List<String> lines = new ArrayList<>();
            if (groupFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(groupFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("分数：")) {
                            lines.add(line);
                        }
                    }
                }
            }
            
            // 写入更新后的内容
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(groupFile))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.write("分数：" + newScore);
            }
            
            updateGroupList();
            JOptionPane.showMessageDialog(this, "修改成功", "", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "修改失败", "", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteGroup(int selectedRow) {
        String groupName = (String) classTable.getValueAt(selectedRow, 1);
        File groupDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/groups/" + groupName);
        
        if (deleteDirectory(groupDir)) {
            updateGroupList();
            JOptionPane.showMessageDialog(this, "删除成功", "", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "删除失败", "", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return directory.delete();
    }
}
