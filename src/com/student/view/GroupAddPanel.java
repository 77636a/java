package com.student.view;

import com.student.util.Constant;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.io.*;

public class GroupAddPanel extends JPanel {
    public GroupAddPanel() {
        this.setLayout(null);
        this.setBorder(new TitledBorder(new EtchedBorder(), "新增小组"));
        JLabel lblName = new JLabel("小组名称：");
        JTextField txtName = new JTextField();
        JButton btnName = new JButton("确认");
        this.add(lblName);
        this.add(txtName);
        this.add(btnName);
        lblName.setBounds(200, 80, 100, 30);
        txtName.setBounds(200, 130, 200, 30);
        btnName.setBounds(200, 180, 100, 30);

        btnName.addActionListener(e -> {
            String groupName = txtName.getText();
            if (groupName == null || groupName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请填写小组名称", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // 检查当前是否选择了班级
            if (Constant.CLASS_PATH == null || Constant.CLASS_PATH.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请先选择班级", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // 在当前班级文件夹中创建小组文件夹
            String groupDirPath = Constant.FILE_PATH + Constant.CLASS_PATH + "/groups";
            File groupsDir = new File(groupDirPath);
            if (!groupsDir.exists()) {
                groupsDir.mkdirs();
            }
            
            // 创建小组文件夹
            File groupDir = new File(groupsDir, groupName);
            if (groupDir.exists()) {
                JOptionPane.showMessageDialog(this, "小组已存在", "", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                // 创建小组文件夹
                if (groupDir.mkdir()) {
                    // 在小组文件夹中创建小组信息文件
                    File groupInfoFile = new File(groupDir, groupName + ".txt");
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(groupInfoFile))) {
                        writer.write("小组名称：" + groupName);
                        writer.newLine();
                        writer.write("创建时间：" + new java.util.Date());
                        writer.newLine();
                        writer.write("所属班级：" + Constant.CLASS_PATH);
                    }
                    
                    JOptionPane.showMessageDialog(this, "新增小组成功", "", JOptionPane.INFORMATION_MESSAGE);
                    txtName.setText(""); // 清空输入框
                } else {
                    JOptionPane.showMessageDialog(this, "创建小组失败", "", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "创建小组失败：" + ex.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
