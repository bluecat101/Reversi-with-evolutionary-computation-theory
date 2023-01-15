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
    g.setColor(new Color(0,180,0));
    this.paintLine(g);
  }
  
  public void paintLine(Graphics g){
    g.setColor(new Color(0,180,0));
    g.setColor(new Color(40,40,40));
    g.fillRect(0,0,1050,600);
    int xpoint[] = {493,513,353,313};
    int ypoint[] = {200,200,600,600};
    g.fillRect(0,560,1050,30);
    g.fillRect(0,345,1050,30);
    g.fillRect(0,200,1050,30);
    //g.fillPolygon(xpoint,ypoint,4);
    for(int j=0; j<2; j++){
      g.fillPolygon(xpoint,ypoint,4);
      for(int i=0; i<4; i++){xpoint[i] = (1065 - xpoint[i]);}//線対称
		//g.fillRect(368,0,60,600);
  }
}