package com.student.view;

// 导入必要的包
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import com.student.util.Constant;

/**
 * 班级列表面板类
 * 用于显示、修改和删除班级信息
 * 继承自JPanel，提供图形化界面
 */
public class ClassListPanel extends JPanel {
    // 表格列头定义
    String[] headers = {"序号", "班级名称"};
    // 界面组件声明
    JTable classTable;         // 班级列表表格
    JTextField txtName;        // 班级名称输入框
    JButton btnEdit;          // 修改按钮
    JButton btnDelete;        // 删除按钮

    /**
     * 构造方法：初始化班级列表面板的界面组件
     */
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

        /**
         * 表格选择监听器
         * 当选择表格行时，将班级名称显示在输入框中
         */
        classTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = classTable.getSelectedRow();
            if (selectedRow >= 0) {
                txtName.setText((String) classTable.getValueAt(selectedRow, 1));
            }
        });

        /**
         * 修改按钮点击事件处理
         * 实现班级名称的修改功能
         */
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

        /**
         * 删除按钮点击事件处理
         * 实现班级的删除功能
         */
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

    /**
     * 更新班级列表显示
     * 读取指定目录下的所有文件夹，将其作为班级信息显示在表格中
     */
    private void updateClassList() {
        File classDir = new File(Constant.FILE_PATH);
        // 确保目录存在
        if (!classDir.exists() || !classDir.isDirectory()) {
            classDir.mkdirs();
            return;
        }

        // 获取所有文件夹（班级）
        File[] classes = classDir.listFiles(File::isDirectory);

        if (classes != null && classes.length > 0) {
            // 构建表格数据
            String[][] data = new String[classes.length][2];
            for (int i = 0; i < classes.length; i++) {
                data[i][0] = String.valueOf(i + 1);          // 序号
                data[i][1] = classes[i].getName();           // 班级名称
            }
            DefaultTableModel tableModel = new DefaultTableModel(data, headers);
            classTable.setModel(tableModel);
        } else {
            // 没有班级时显示空表格
            DefaultTableModel tableModel = new DefaultTableModel(new String[0][0], headers);
            classTable.setModel(tableModel);
        }
    }

    /**
     * 递归删除目录及其内容
     * @param directory 要删除的目录
     * @return 删除是否成功
     */
    private boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);    // 递归删除子目录
                    } else {
                        file.delete();           // 删除文件
                    }
                }
            }
        }
        return directory.delete();               // 删除空目录
    }
}
