import java.awt.Font;

import javax.swing.*;
import java.awt.*;
// import java.awt.event.*;
// import javax.swing.border.LineBorder;
// import java.util.*;

class TitlePanel extends JPanel {
  JButton start,setting,finish;
  TitlePanel() {
    this.setLayout(null);
    ImageIcon icon1 = new ImageIcon("osero-illust7.png");
    ImageIcon icon2 = new ImageIcon("osero-illust8.png");
    start = new JButton("start", icon1);
    start.setRolloverIcon(icon2);
    start.setContentAreaFilled(false); //背景透明化
    start.setHorizontalTextPosition(JButton.CENTER);
    start.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 30));
    start.setForeground(Color.GREEN); //文字の色
    start.setBorderPainted(false); //ボタンの枠削除
    start.setBounds(414,400,240,135);
    
    setting = new JButton("setting", icon1);
    setting.setRolloverIcon(icon2);
    setting.setContentAreaFilled(false); //背景透明化
    setting.setHorizontalTextPosition(JButton.CENTER);
    setting.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 30));
    setting.setForeground(Color.GREEN); //文字の色
    setting.setBorderPainted(false); //ボタンの枠削除
    setting.setBounds(82,400,240,135);

    finish = new JButton("finish", icon1);
    finish.setRolloverIcon(icon2);
    finish.setContentAreaFilled(false); //背景透明化
    finish.setHorizontalTextPosition(JButton.CENTER);
    finish.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 30));
    finish.setForeground(Color.GREEN); //文字の色
    finish.setBorderPainted(false); //ボタンの枠削除
    finish.setBounds(746,400,240,135);

    //add(img);
    add(start);add(setting);add(finish);
    this.setVisible(true);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.setColor(Color.BLACK);
    this.paintLine(g);
  }
  
  public void paintLine(Graphics g){
    for(int i=0; i<10; i++){
      g.drawLine(373,0,368+i,600);
    }
		//g.fillRect(368,100,10,600);
  }
}