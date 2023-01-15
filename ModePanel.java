import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

public class ModePanel extends JPanel{
  JButton returnButton;
  JButton single,multi;
  Image imgBack;
  ModePanel(){
    setLayout(null);
    try {
      imgBack = ImageIO.read(new File("haikei.jpg"));
    } catch (Exception e) {
        System.out.println(e);
        System.exit(0);
    }
    ImageIcon icon1 = new ImageIcon("osero-illust7.png");
    ImageIcon icon2 = new ImageIcon("osero-illust8.png");
    single = new JButton("Single",icon1);
    single.setRolloverIcon(icon2);
    single.setContentAreaFilled(false); //背景透明化
    single.setHorizontalTextPosition(JButton.CENTER);
    single.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 30));
    single.setForeground(Color.GREEN); //文字の色
    single.setBorderPainted(false); //ボタンの枠削除
    single.setBounds(250,220,240,135);
    multi = new JButton("Multi",icon1);
    multi.setRolloverIcon(icon2);
    multi.setContentAreaFilled(false); //背景透明化
    multi.setHorizontalTextPosition(JButton.CENTER);
    multi.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 30));
    multi.setForeground(Color.GREEN); //文字の色
    multi.setBorderPainted(false); //ボタンの枠削除
    multi.setBounds(550,220,240,135);
    returnButton = new JButton("<html>Return<br /> Title</html>",icon1);
    returnButton.setRolloverIcon(icon2);
    returnButton.setContentAreaFilled(false); //背景透明化
    returnButton.setHorizontalTextPosition(JButton.CENTER);
    returnButton.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 30));
    returnButton.setForeground(Color.GREEN); //文字の色
    returnButton.setBorderPainted(false); //ボタンの枠削除
    returnButton.setBounds(400, 370, 240, 135);
    add(returnButton);add(multi);add(single);
  }
  public void paintComponent(Graphics g) {
    Font f2 = new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 80);
		Graphics2D g2 = (Graphics2D)g;
 
		//文字描画のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
    g.drawImage(imgBack, 0, 0, 1080, 600, null);
    g.setFont(f2);
    g.setColor(new Color(0,100,255));
    g.drawString("Select Mode", 295, 195);
    g.setColor(new Color(255,0,60));
    g.drawString("Select Mode", 290, 190);
  }
}