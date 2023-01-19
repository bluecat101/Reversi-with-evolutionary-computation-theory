import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
// import java.awt.*;
// import java.awt.event.*;
// import javax.swing.border.LineBorder;
// import java.util.*;

class SettingPanel extends JPanel {
  JButton returnButton;
  Image imgBack;
  JLabel ruleText;
  SettingPanel() {
    setLayout(null);
    try {
      imgBack = ImageIO.read(new File("haikei.jpg"));
    } catch (Exception e) {
        System.out.println(e);
        System.exit(0);
    }
    returnButton = new JButton("Return Title");
    ImageIcon icon1 = new ImageIcon("osero-illust7.png");
    ImageIcon icon2 = new ImageIcon("osero-illust8.png");
    returnButton = new JButton("back", icon1);
    returnButton.setRolloverIcon(icon2);
    returnButton.setContentAreaFilled(false); //背景透明化
    returnButton.setHorizontalTextPosition(JButton.CENTER);
    returnButton.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 30));
    returnButton.setForeground(Color.GREEN); //文字の色
    returnButton.setBorderPainted(false); //ボタンの枠削除
    returnButton.setBounds(414,350+20,240,135);
    add(returnButton);
  }
  public void paintComponent(Graphics g) {
    //Font f2 = new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 40);
    Font f2 = new Font("メイリオ", Font.BOLD, 40);
		Graphics2D g2 = (Graphics2D)g;

    FontMetrics fontMetrics = this.getFontMetrics(f2);
 
		//文字描画のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
    g.drawImage(imgBack, 0, 0, 1080, 600, null);
    g.setFont(f2);
    int a=fontMetrics.stringWidth("Rule");
    g.setColor(new Color(100,100,100));
    g.drawString("Rule", 540-a/2+5, 145);
    g.setColor(new Color(30,200,30));
    g.drawString("Rule", 540-a/2, 140);
    g.setColor(Color.BLACK);
    //ruleText = new JLabel("", JLabel.LEFT);
    g.drawString("・　移動　 : 矢印キー or マウス", 170,200);
    g.drawString("・駒を置く : Zキー or 左クリック",170,270);
    g.drawString("・チャット : Enterキー or Chatボタン",170,340);
    //this.add(ruleText);
  }
}