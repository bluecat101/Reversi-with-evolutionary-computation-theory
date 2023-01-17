import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class SinglePanel extends JPanel{
  JButton returnButton,startButton;
  JButton level1,level2,level3;
  JComboBox<String> cb;
  JComboBox<String> first;
  Image imgBack;
  SinglePanel(){
    setLayout(null);
    try {
      imgBack = ImageIO.read(new File("haikei.jpg"));
    } catch (Exception e) {
        System.out.println(e);
        System.exit(0);
    }
    ImageIcon icon1 = new ImageIcon("osero-illust7.png");
    ImageIcon icon2 = new ImageIcon("osero-illust8.png");
    cb=new JComboBox<>();
    cb.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 60));
    cb.addItem("level1");cb.addItem("level2");cb.addItem("level3");
    cb.setBounds(250,220,240,135);
    first=new JComboBox<>();
    first.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 60));
    first.addItem("先攻");first.addItem("後攻");
    first.setBounds(550,370,240,135);
    startButton = new JButton("Start",icon1);
    startButton.setRolloverIcon(icon2);
    startButton.setContentAreaFilled(false); //背景透明化
    startButton.setHorizontalTextPosition(JButton.CENTER);
    startButton.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 30));
    startButton.setForeground(Color.GREEN); //文字の色
    startButton.setBorderPainted(false); //ボタンの枠削除
    startButton.setBounds(550,220,240,135);
    returnButton = new JButton("Back",icon1);
    returnButton.setRolloverIcon(icon2);
    returnButton.setContentAreaFilled(false); //背景透明化
    returnButton.setHorizontalTextPosition(JButton.CENTER);
    returnButton.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 30));
    returnButton.setForeground(Color.GREEN); //文字の色
    returnButton.setBorderPainted(false); //ボタンの枠削除
    returnButton.setBounds(400, 370, 240, 135);
    add(returnButton);add(startButton);add(cb);add(first);
  }
  public void paintComponent(Graphics g) {
    Font f2 = new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 80);
		Graphics2D g2 = (Graphics2D)g;
 
		//文字描画のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
    g.drawImage(imgBack, 0, 0, 1080, 600, null);
    g.setFont(f2);
    g.setColor(new Color(0,100,255));
    g.drawString("Select AILevel", 275, 195);
    g.setColor(new Color(255,0,60));
    g.drawString("Select AILevel", 270, 190);
  }
}