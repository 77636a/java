package com.student.view;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.io.File;
import com.student.util.Constant; 

public class ClassAddPanel extends JPanel {
    public ClassAddPanel() {
        this.setLayout(null);
        this.setBorder(new TitledBorder(new EtchedBorder(), "新增班级"));
        JLabel lblName = new JLabel("班级名称：");
        JTextField txtName = new JTextField();
        JButton btnName = new JButton("确认");
        this.add(lblName);
        this.add(txtName);
        this.add(btnName);
        lblName.setBounds(200, 80, 100, 30);
        txtName.setBounds(200, 130, 200, 30);
        btnName.setBounds(200, 180, 100, 30);

        btnName.addActionListener(e -> {
            String className = txtName.getText();
            if (className == null || className.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请填写班级名称", "", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // 保存班级信息到文件
                saveClassToFile(className);
            }
        });
    }

    private void saveClassToFile(String className) {
        // 修改为创建文件夹而不是.txt文件
        File classDir = new File(Constant.FILE_PATH + className);
        
        try {
            if (!classDir.exists()) {
                boolean created = classDir.mkdirs();  // 创建文件夹
                if (created) {
                    JOptionPane.showMessageDialog(this, "新增班级成功", "", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "创建班级文件夹失败", "", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "班级已存在", "", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "保存班级信息失败", "", JOptionPane.ERROR_MESSAGE);
        }
    }
}
