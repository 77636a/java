package com.student.view;

import com.student.util.Constant;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import java.io.File;
import java.util.Enumeration;

public class ChangeClassPanel extends JScrollPane {
    JLabel infoLbl = new JLabel();

    public ChangeClassPanel(MainFrame mainFrame) {
        this.setBorder(new TitledBorder(new EtchedBorder(), "新选择班级"));
        int x = 160, y = 100;
        
        // 修改为使用 JPanel 作为内容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        this.setViewportView(contentPanel);
        
        File classDir = new File(Constant.FILE_PATH);
        System.out.println("读取目录：" + classDir.getAbsolutePath());
        
        if (!classDir.exists()) {
            classDir.mkdirs();
        }
        
        // 添加调试信息
        File[] files = classDir.listFiles(File::isDirectory);
        System.out.println("找到的班级文件夹数量：" + (files != null ? files.length : 0));
        if (files != null) {
            for (File file : files) {
                System.out.println("发现班级文件夹：" + file.getName());
            }
        }
        
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(this, "请先创建班级", "", JOptionPane.INFORMATION_MESSAGE);
        } else {
            ButtonGroup btnGroup = new ButtonGroup();
            int maxWidth = 0;
            int count = 0;
            
            // 创建所有班级的单选按钮
            for (File file : files) {
                String className = file.getName();
                JRadioButton classRadio = new JRadioButton(className);
                btnGroup.add(classRadio);
                contentPanel.add(classRadio);
                classRadio.setBounds(x, y + (count * 40), 200, 30);
                count++;
                
                // 更新最大宽度
                maxWidth = Math.max(maxWidth, 200);
                
                System.out.println("创建单选按钮：" + className + " 位置：" + y + (count * 40));
                
                // 如果是当前班级，设置为选中
                if (className.equals(Constant.CLASS_PATH)) {
                    classRadio.setSelected(true);
                }
            }
            
            // 添加确认按钮
            JButton btnChooseClass = new JButton("确认选择班级");
            contentPanel.add(btnChooseClass);
            btnChooseClass.setBounds(x, y + (count * 40), 120, 30);
            
            // 设置内容面板的首选大小
            contentPanel.setPreferredSize(new Dimension(maxWidth + 50, y + (count * 40) + 50));
            
            btnChooseClass.addActionListener(e -> {
                Enumeration<AbstractButton> elements = btnGroup.getElements();
                boolean isSelected = false;
                while (elements.hasMoreElements()) {
                    JRadioButton btn = (JRadioButton) elements.nextElement();
                    if (btn.isSelected()) {
                        isSelected = true;
                        String selectedClass = btn.getText();
                        mainFrame.setTitle(selectedClass);
                        Constant.CLASS_PATH = selectedClass;
                        
                        // 读取学生总数
                        int studentCount = getStudentCount(selectedClass);
                        infoLbl.setText("班级：" + selectedClass + "，班级学生总数：" + studentCount);
                        break;
                    }
                }
                if (isSelected) {
                    // TODO 初始化小组和学生
                    try {
                        // 可以在这里添加其他初始化代码
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(this, "初始化小组和学生信息失败，请检查相关文件", "", JOptionPane.INFORMATION_MESSAGE);
                    }
                    this.removeAll();
                    infoLbl.setBounds(160, 100, 200, 30);
                    this.add(infoLbl);
                    this.repaint();
                    this.validate();
                } else {
                    JOptionPane.showMessageDialog(this, "请先选择班级", "", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            this.repaint();
            this.validate();
        }
    }

    // 修改获取学生总数的方法
    private int getStudentCount(String className) {
        File studentsDir = new File(Constant.FILE_PATH + className + "/students");
        if (!studentsDir.exists() || !studentsDir.isDirectory()) {
            return 0;
        }
        
        // 只统计学生文件（不包含目录）
        File[] studentFiles = studentsDir.listFiles((dir, name) -> 
            name.endsWith(".txt") && new File(dir, name).isFile()
        );
        
        // 添加调试信息
        System.out.println("学生文件夹路径：" + studentsDir.getAbsolutePath());
        if (studentFiles != null) {
            System.out.println("找到的学生文件数量：" + studentFiles.length);
            for (File file : studentFiles) {
                System.out.println("学生文件：" + file.getName());
            }
        }
        
        return studentFiles != null ? studentFiles.length : 0;
    }
}
