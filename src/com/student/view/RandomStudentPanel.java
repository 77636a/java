package com.student.view;

import com.student.util.Constant;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.Timer;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class RandomStudentPanel extends JPanel {
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel photoLabel;
    private JButton randomButton;
    private JButton absenceButton;
    private JButton leaveButton;
    private JButton answerButton;
    private javax.swing.Timer timer;
    private Random random = new Random();
    private List<String[]> studentList = new ArrayList<>();
    private boolean isRandomizing = false;

    public RandomStudentPanel() {
        this.setLayout(null);
        this.setBorder(new TitledBorder(new EtchedBorder(), "随机学生点名"));

        // 初始化组件
        nameLabel = new JLabel("学生姓名：");
        nameField = new JTextField();
        photoLabel = new JLabel("照片");
        randomButton = new JButton("随机学生");
        absenceButton = new JButton("缺勤");
        leaveButton = new JButton("请假");
        answerButton = new JButton("答题");

        // 设置组件位置和大小
        nameLabel.setBounds(150, 50, 80, 30);
        nameField.setBounds(150, 90, 200, 30);
        photoLabel.setBounds(150, 140, 200, 200);
        randomButton.setBounds(200, 360, 100, 30);
        absenceButton.setBounds(120, 410, 80, 30);
        leaveButton.setBounds(210, 410, 80, 30);
        answerButton.setBounds(300, 410, 80, 30);

        // 设置照片标签的边框和对齐方式
        photoLabel.setBorder(BorderFactory.createEtchedBorder());
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 添加组件到面板
        this.add(nameLabel);
        this.add(nameField);
        this.add(photoLabel);
        this.add(randomButton);
        this.add(absenceButton);
        this.add(leaveButton);
        this.add(answerButton);

        // 初始化定时器
        timer = new javax.swing.Timer(50, e -> {
            if (studentList.isEmpty()) {
                stopRandomizing();
                return;
            }
            String[] randomStudent = studentList.get(random.nextInt(studentList.size()));
            nameField.setText(randomStudent[1]); // 显示学生姓名
            
            // 显示照片
            displayPhoto(randomStudent[2]);
        });

        // 随机按钮事件
        randomButton.addActionListener(e -> {
            if (!isRandomizing) {
                startRandomizing();
            } else {
                stopRandomizing();
            }
        });

        // 缺勤按钮事件
        absenceButton.addActionListener(e -> {
            recordStatus("缺勤");
        });

        // 请假按钮事件
        leaveButton.addActionListener(e -> {
            recordStatus("请假");
        });

        // 答题按钮事件
        answerButton.addActionListener(e -> {
            recordStatus("答题");
        });

        // 加载学生列表
        loadStudentList();
    }

    private void loadStudentList() {
        studentList.clear();
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

        for (File file : studentFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String[] studentInfo = new String[3];  // 增加一个元素存储照片路径
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("学号：")) {
                        studentInfo[0] = line.substring(3).trim();
                    } else if (line.startsWith("姓名：")) {
                        studentInfo[1] = line.substring(3).trim();
                    } else if (line.startsWith("照片：")) {
                        studentInfo[2] = line.substring(3).trim();
                    }
                }
                // 如果没有照片路径，设置默认路径
                if (studentInfo[2] == null) {
                    studentInfo[2] = Constant.FILE_PATH + Constant.CLASS_PATH + "/photos/" + studentInfo[0] + ".jpg";
                }
                studentList.add(studentInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startRandomizing() {
        if (studentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可用的学生信息", "", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        isRandomizing = true;
        randomButton.setText("停止");
        timer.start();
    }

    private void stopRandomizing() {
        isRandomizing = false;
        randomButton.setText("随机学生");
        timer.stop();
    }

    private void recordStatus(String status) {
        String currentStudent = nameField.getText();
        if (currentStudent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先随机选择学生", "", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 记录学生状态到文件
        try {
            File statusDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/status");
            if (!statusDir.exists()) {
                statusDir.mkdirs();
            }

            File statusFile = new File(statusDir, new Date().getTime() + ".txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(statusFile))) {
                writer.write("学生：" + currentStudent);
                writer.newLine();
                writer.write("状态：" + status);
                writer.newLine();
                writer.write("时间：" + new Date());
            }

            JOptionPane.showMessageDialog(this, "记录成功", "", JOptionPane.INFORMATION_MESSAGE);
            nameField.setText("");
            photoLabel.setIcon(null);
            photoLabel.setText("照片");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "记录失败", "", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 添加显示照片的方法
    private void displayPhoto(String photoPath) {
        if (photoPath != null && !photoPath.isEmpty()) {
            try {
                ImageIcon imageIcon = new ImageIcon(photoPath);
                // 调整图片大小以适应标签
                Image image = imageIcon.getImage();
                Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaledImage));
                photoLabel.setText(""); // 清除默认文本
            } catch (Exception e) {
                photoLabel.setIcon(null);
                photoLabel.setText("无法加载照片");
                e.printStackTrace();
            }
        } else {
            photoLabel.setIcon(null);
            photoLabel.setText("无照片");
        }
    }
}
