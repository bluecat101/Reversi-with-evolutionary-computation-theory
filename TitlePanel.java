import javax.swing.*;
// import java.awt.*;
// import java.awt.event.*;
// import javax.swing.border.LineBorder;
// import java.util.*;

class TitlePanel extends JPanel {
  JButton start,setting,finish;
  TitlePanel() {
    this.setLayout(null);
    // JLabel img = new JLabel("<html><img src='file:title.jpg'></html>");
    // img.setBounds(0,0,1050,600);
    start = new JButton("start");
    start.setBounds(400,75,250,100);
    
    setting = new JButton("setting");
    setting.setBounds(400,250,250,100);

    finish = new JButton("finish");
    finish.setBounds(400,425,250,100);

    //add(img);
    add(start);add(setting);add(finish);
  }
}