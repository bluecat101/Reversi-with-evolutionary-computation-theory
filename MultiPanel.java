import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

public class MultiPanel extends JPanel{
  JButton returnButton,serverButton,clientButton;
  Image imgBack;
  MultiPanel(){
    setLayout(null);
    try {
      imgBack = ImageIO.read(new File("haikei.jpg"));
    } catch (Exception e) {
        System.out.println(e);
        System.exit(0);
    }
    ImageIcon icon1 = new ImageIcon("osero-illust7.png");
    ImageIcon icon2 = new ImageIcon("osero-illust8.png");
    serverButton = new JButton("<html>Make a <br /><center> room</center></html>",icon1);
    serverButton.setRolloverIcon(icon2);
    serverButton.setContentAreaFilled(false); //背景透明化
    serverButton.setHorizontalTextPosition(JButton.CENTER);
    serverButton.setFont(new Font("Arial Black", Font.BOLD, 30));
    serverButton.setForeground(Color.GREEN); //文字の色
    serverButton.setBorderPainted(false); //ボタンの枠削除
    serverButton.setBounds(270,220,240,135);
    clientButton = new JButton("Join a room",icon1);
    clientButton.setRolloverIcon(icon2);
    clientButton.setContentAreaFilled(false); //背景透明化
    clientButton.setHorizontalTextPosition(JButton.CENTER);
    clientButton.setFont(new Font("Arial Black", Font.BOLD, 30));
    clientButton.setForeground(Color.GREEN); //文字の色
    clientButton.setBorderPainted(false); //ボタンの枠削除
    clientButton.setBounds(570,220,240,135);
    returnButton = new JButton("Back",icon1);
    returnButton.setRolloverIcon(icon2);
    returnButton.setContentAreaFilled(false); //背景透明化
    returnButton.setHorizontalTextPosition(JButton.CENTER);
    returnButton.setFont(new Font("Arial Black", Font.BOLD, 30));
    returnButton.setForeground(Color.GREEN); //文字の色
    returnButton.setBorderPainted(false); //ボタンの枠削除
    returnButton.setBounds(420, 370, 240, 135);
    add(returnButton);add(clientButton);add(serverButton);
  }
  public void paintComponent(Graphics g) {
    Font f2 = new Font("メイリオ", Font.BOLD, 80);
		Graphics2D g2 = (Graphics2D)g;
 
		//文字描画のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
    g.drawImage(imgBack, 0, 0, 1080, 600, null);
    g.setFont(f2);
    FontMetrics fontMetrics = this.getFontMetrics(f2);
    int a=fontMetrics.stringWidth("Multi Mode");
    g.setColor(new Color(100,100,100));
    g.drawString("Multi Mode", 540-a/2+5, 195);
    g.setColor(new Color(30,200,30));
    g.drawString("Multi Mode", 540-a/2, 190);
  }
}
