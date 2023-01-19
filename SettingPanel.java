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
    returnButton.setBounds(400, 425, 250, 100);
    add(returnButton);
  }
  public void paintComponent(Graphics g) {
    Font f2 = new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 80);
		Graphics2D g2 = (Graphics2D)g;

    FontMetrics fontMetrics = this.getFontMetrics(f2);
 
		//文字描画のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
    g.drawImage(imgBack, 0, 0, 1080, 600, null);
    g.setFont(f2);
    int a=fontMetrics.stringWidth("Rule");
    g.setColor(new Color(0,100,255));
    g.drawString("Rule", 540-a/2+5, 195);
    g.setColor(new Color(255,0,60));
    g.drawString("Rule", 540-a/2, 190);
    g.setColor(Color.BLACK);
    ruleText = new JLabel("", JLabel.LEFT);
    ruleText.setText("<html>"+"・移動 : 矢印キー or マウス<br>"+"</html>");
    this.add(ruleText);
  }
}