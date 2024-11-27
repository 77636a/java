package com.student.view;

import com.student.util.Constant;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.Timer;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class RandomGroupPanel extends JPanel {
    private JLabel groupLabel;
    private JTextField groupField;
    private JLabel studentLabel;
    private JTextField studentField;
    private JLabel scoreLabel;
    private JTextField scoreField;
    private JLabel photoLabel;
    private JButton randomGroupBtn;
    private JButton randomStudentBtn;
    private JButton scoreBtn;
    private JButton absenceButton;
    private JButton leaveButton;
    private javax.swing.Timer timer;
    private Random random = new Random();
    private List<String[]> groupList = new ArrayList<>();
    private List<String[]> groupStudentList = new ArrayList<>();
    private boolean isRandomizing = false;

    public RandomGroupPanel() {
        this.setLayout(null);
        this.setBorder(new TitledBorder(new EtchedBorder(), "随机小组点名"));

        // 初始化组件
        groupLabel = new JLabel("小组名：");
        groupField = new JTextField();
        studentLabel = new JLabel("学生姓名：");
        studentField = new JTextField();
        scoreLabel = new JLabel("小组评分：");
        scoreField = new JTextField();
        photoLabel = new JLabel("照片");
        randomGroupBtn = new JButton("随机小组");
        randomStudentBtn = new JButton("随机学生");
        scoreBtn = new JButton("小组评分");
        absenceButton = new JButton("缺勤");
        leaveButton = new JButton("请假");

        // 设置组件位置和大小
        groupLabel.setBounds(50, 50, 80, 30);
        groupField.setBounds(130, 50, 150, 30);
        studentLabel.setBounds(300, 50, 80, 30);
        studentField.setBounds(380, 50, 150, 30);
        scoreLabel.setBounds(50, 100, 80, 30);
        scoreField.setBounds(130, 100, 150, 30);
        photoLabel.setBounds(300, 100, 200, 200);
        randomGroupBtn.setBounds(50, 320, 100, 30);
        randomStudentBtn.setBounds(350, 320, 100, 30);
        scoreBtn.setBounds(50, 370, 100, 30);
        absenceButton.setBounds(350, 370, 80, 30);
        leaveButton.setBounds(450, 370, 80, 30);

        // 设置照片标签的边框和对齐方式
        photoLabel.setBorder(BorderFactory.createEtchedBorder());
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 设置文本框为只读
        groupField.setEditable(false);
        studentField.setEditable(false);

        // 添加组件到面板
        this.add(groupLabel);
        this.add(groupField);
        this.add(studentLabel);
        this.add(studentField);
        this.add(scoreLabel);
        this.add(scoreField);
        this.add(photoLabel);
        this.add(randomGroupBtn);
        this.add(randomStudentBtn);
        this.add(scoreBtn);
        this.add(absenceButton);
        this.add(leaveButton);

        // 初始化定时器
        timer = new Timer(50, e -> {
            if (groupList.isEmpty()) {
                stopRandomizing();
                return;
            }
            String[] randomGroup = groupList.get(random.nextInt(groupList.size()));
            groupField.setText(randomGroup[0]); // 显示小组名
            loadGroupStudents(randomGroup[0]); // 加载该小组的学生
        });

        // 随机小组按钮事件
        randomGroupBtn.addActionListener(e -> {
            if (!isRandomizing) {
                startRandomizing();
            } else {
                stopRandomizing();
            }
        });

        // 随机学生按钮事件
        randomStudentBtn.addActionListener(e -> {
            String currentGroup = groupField.getText();
            if (currentGroup.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请先选择小组", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 检查小组目录和学生文件是否存在
            File groupDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/groups/" + currentGroup);
            File studentsFile = new File(groupDir, "students.txt");
            
            if (!studentsFile.exists() || !loadGroupStudents(currentGroup)) {
                JOptionPane.showMessageDialog(this, "小组 " + currentGroup + " 没有学生", "", JOptionPane.INFORMATION_MESSAGE);
                studentField.setText("");
                photoLabel.setIcon(null);
                photoLabel.setText("照片");
                return;
            }

            if (groupStudentList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "小组 " + currentGroup + " 没有学生", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] randomStudent = groupStudentList.get(random.nextInt(groupStudentList.size()));
            studentField.setText(randomStudent[1]);
            displayPhoto(randomStudent[2]);
        });

        // 小组评分按钮事件
        scoreBtn.addActionListener(e -> {
            String groupName = groupField.getText();
            String score = scoreField.getText();
            if (groupName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请先随机选择小组", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (score.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入评分", "", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            updateGroupScore(groupName, score);
        });

        // 缺勤和请假按钮事件
        absenceButton.addActionListener(e -> recordStatus("缺勤"));
        leaveButton.addActionListener(e -> recordStatus("请假"));

        // 添加这行：加载小组列表
        loadGroupList();
    }

    private void startRandomizing() {
        if (groupList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可用的小组", "", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        isRandomizing = true;
        timer.start();
        randomGroupBtn.setText("停止随机小组");
    }

    private void stopRandomizing() {
        isRandomizing = false;
        timer.stop();
        randomGroupBtn.setText("随机小组");
    }

    private boolean loadGroupStudents(String groupName) {
        groupStudentList.clear();
        File groupDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/groups/" + groupName);
        File studentsFile = new File(groupDir, "students.txt");

        if (!studentsFile.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(studentsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String[] studentInfo = new String[3];
                    studentInfo[0] = parts[0]; // 学号
                    studentInfo[1] = parts[1]; // 姓名
                    studentInfo[2] = Constant.FILE_PATH + Constant.CLASS_PATH + "/photos/" + parts[0] + ".jpg"; // 照片路径
                    groupStudentList.add(studentInfo);
                }
            }
            return !groupStudentList.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void displayPhoto(String photoPath) {
        // 从文件中加载学生照片并显示在photoLabel上
        // 这里需要实现从文件中加载照片的逻辑
    }

    private void updateGroupScore(String groupName, String score) {
        // 更新小组评分
        // 这里需要实现更新小组评分的逻辑
    }

    private void recordStatus(String status) {
        // 记录学生状态
        // 这里需要实现记录学生状态的逻辑
    }

    // 添加加载小组列表的方法
    private void loadGroupList() {
        groupList.clear();
        if (Constant.CLASS_PATH == null || Constant.CLASS_PATH.isEmpty()) {
            return;
        }

        File groupsDir = new File(Constant.FILE_PATH + Constant.CLASS_PATH + "/groups");
        if (!groupsDir.exists() || !groupsDir.isDirectory()) {
            return;
        }

        // 添加调试信息
        System.out.println("正在加载小组，路径：" + groupsDir.getAbsolutePath());

        File[] groupDirs = groupsDir.listFiles(File::isDirectory);
        if (groupDirs != null) {
            for (File dir : groupDirs) {
                String[] groupInfo = new String[]{dir.getName()};
                groupList.add(groupInfo);
                System.out.println("找到小组：" + dir.getName());
            }
        }

        System.out.println("共加载 " + groupList.size() + " 个小组");
    }
}
