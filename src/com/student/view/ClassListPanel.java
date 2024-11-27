package com.student.view;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import com.student.util.Constant;

public class ClassListPanel extends JPanel {
    String[] headers = {"序号", "班级名称"};
    JTable classTable;
    JTextField txtName = new JTextField();
    JButton btnEdit = new JButton("修改");
    JButton btnDelete = new JButton("删除");

    public ClassListPanel() {
        this.setBorder(new TitledBorder(new EtchedBorder(), "班级列表"));
        this.setLayout(new BorderLayout());
        
        // 首先初始化 JTable
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

        // 然后再调用更新方法
        updateClassList(); // 更新班级列表

        JPanel btnPanel = new JPanel();
        btnPanel.add(txtName);
        txtName.setPreferredSize(new Dimension(200, 30));
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        this.add(btnPanel, BorderLayout.SOUTH);

        classTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = classTable.getSelectedRow();
            if (selectedRow >= 0) {
                txtName.setText((String) classTable.getValueAt(selectedRow, 1));
            }
        });

        btnEdit.addActionListener(e -> {
            int selectedRow = classTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "请先选择班级", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String newClassName = txtName.getText();
            if (newClassName == null || newClassName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请填写班级名称", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // 修改班级文件夹名称
            String oldClassName = (String) classTable.getValueAt(selectedRow, 1);
            File oldDir = new File(Constant.FILE_PATH + oldClassName);
            File newDir = new File(Constant.FILE_PATH + newClassName);
            if (oldDir.renameTo(newDir)) {
                updateClassList();
                JOptionPane.showMessageDialog(this, "修改成功", "", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "修改失败", "", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = classTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "请先选择班级", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String className = (String) classTable.getValueAt(selectedRow, 1);
            File dir = new File(Constant.FILE_PATH + className);
            if (deleteDirectory(dir)) {
                updateClassList();
                JOptionPane.showMessageDialog(this, "删除成功", "", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void updateClassList() {
        File classDir = new File(Constant.FILE_PATH);
        if (!classDir.exists() || !classDir.isDirectory()) {
            classDir.mkdirs();
            return;
        }

        // 只列出文件夹
        File[] classes = classDir.listFiles(File::isDirectory);

        if (classes != null && classes.length > 0) {
            String[][] data = new String[classes.length][2];
            for (int i = 0; i < classes.length; i++) {
                data[i][0] = String.valueOf(i + 1);
                data[i][1] = classes[i].getName(); // 直接使用文件夹名称
            }
            DefaultTableModel tableModel = new DefaultTableModel(data, headers);
            classTable.setModel(tableModel);
        } else {
            // 如果没有班级，显示空表格
            DefaultTableModel tableModel = new DefaultTableModel(new String[0][0], headers);
            classTable.setModel(tableModel);
        }
    }

    // 添加递归删除目录的方法
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
