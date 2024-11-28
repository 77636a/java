package com.student.view;

// 导入必要的Swing界面相关包
import javax.swing.*;
import javax.swing.border.EtchedBorder;  // 用于创建边框效果
import javax.swing.border.TitledBorder;  // 用于创建带标题的边框
import java.io.File;  // 用于文件操作
import com.student.util.Constant;  // 引入常量类

/**
 * 班级添加面板类
 * 用于实现新增班级的界面功能
 * 继承自JPanel，提供图形化界面
 */
public class ClassAddPanel extends JPanel {
    /**
     * 构造方法：初始化班级添加面板的界面组件
     * 包含班级名称输入框和确认按钮
     * 使用绝对布局方式进行组件排列
     */
    public ClassAddPanel() {
        // 设置面板布局为绝对布局
        this.setLayout(null);
        // 设置面板边框和标题
        this.setBorder(new TitledBorder(new EtchedBorder(), "新增班级"));
        
        // 创建界面组件
        JLabel lblName = new JLabel("班级名称：");  // 标签提示文本
        JTextField txtName = new JTextField();      // 班级名称输入框
        JButton btnName = new JButton("确认");      // 提交按钮
        
        // 添加组件到面板
        this.add(lblName);
        this.add(txtName);
        this.add(btnName);
        
        // 设置组件位置和大小
        lblName.setBounds(200, 80, 100, 30);
        txtName.setBounds(200, 130, 200, 30);
        btnName.setBounds(200, 180, 100, 30);

        // 添加确认按钮的点击事件监听器
        // 使用Lambda表达式简化事件处理
        btnName.addActionListener(e -> {
            String className = txtName.getText().trim();  // 获取输入内容并去除首尾空格
            // 验证输入的班级名称是否为空
            if (className == null || className.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请填写班级名称", "", 
                    JOptionPane.INFORMATION_MESSAGE);  // 显示提示对话框
            } else {
                saveClassToFile(className);  // 调用保存方法
            }
        });
    }

    /**
     * 将班级信息保存为文件夹
     * @param className 班级名称，用作文件夹名
     * 在指定路径下创建以班级名命名的文件夹
     * 包含文件夹已存在和创建失败的异常处理
     */
    private void saveClassToFile(String className) {
        // 在指定路径下创建文件夹对象
        File classDir = new File(Constant.FILE_PATH + className);
        
        try {
            if (!classDir.exists()) {  // 检查文件夹是否已存在
                boolean created = classDir.mkdirs();  // 创建多级目录
                if (created) {
                    // 创建成功时显示成功消息
                    JOptionPane.showMessageDialog(this, "新增班级成功", "", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // 创建失败时显示错误消息
                    JOptionPane.showMessageDialog(this, "创建班级文件夹失败", "", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 文件夹已存在时显示警告消息
                JOptionPane.showMessageDialog(this, "班级已存在", "", 
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            // 捕获并处理可能发生的异常
            e.printStackTrace();  // 打印异常堆栈信息
            JOptionPane.showMessageDialog(this, "保存班级信息失败", "", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
