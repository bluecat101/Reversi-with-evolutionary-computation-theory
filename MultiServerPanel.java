import javax.swing.*;
import java.awt.*;

public class MultiServerPanel extends JPanel{
  JButton returnButton;
  JTextField password;
  String s="合言葉を決める";
  MultiServerPanel(){
    setLayout(null);
    password = new JTextField();
    password.setBounds(400,270,250,100);
    returnButton = new JButton("Back");
    returnButton.setBounds(400, 425, 250, 100);
    add(returnButton);add(password);
  }
  public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Font f2 = new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 25);
		Graphics2D g2 = (Graphics2D)g;
 
		//図形や線のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
		//文字描画のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
		//アンチエイリアシングありで描画
		g.setFont(f2);
		g.drawString(s, 400, 100);
	}
}