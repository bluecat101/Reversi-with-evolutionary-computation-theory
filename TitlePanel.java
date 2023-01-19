import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

class TitlePanel extends JPanel {
  JButton start,setting,finish;
  Image imgBack;
  TitlePanel() {
    this.setLayout(null);
    ImageIcon icon1 = new ImageIcon("osero-illust7.png");
    ImageIcon icon2 = new ImageIcon("osero-illust8.png");
    start = new JButton("start", icon1);
    start.setRolloverIcon(icon2);
    start.setContentAreaFilled(false); //背景透明化
    start.setHorizontalTextPosition(JButton.CENTER);
    start.setFont(new Font("Arial Black", Font.BOLD, 33));
    start.setForeground(Color.GREEN); //文字の色
    start.setBorderPainted(false); //ボタンの枠削除
    start.setBounds(414,350+20,240,135);
    
    setting = new JButton("setting", icon1);
    setting.setRolloverIcon(icon2);
    setting.setContentAreaFilled(false); //背景透明化
    setting.setHorizontalTextPosition(JButton.CENTER);
    setting.setFont(new Font("Arial Black", Font.BOLD, 33));
    setting.setForeground(Color.GREEN); //文字の色
    setting.setBorderPainted(false); //ボタンの枠削除
    setting.setBounds(82,350+20,240,135);

    finish = new JButton("finish", icon1);
    finish.setRolloverIcon(icon2);
    finish.setContentAreaFilled(false); //背景透明化
    finish.setHorizontalTextPosition(JButton.CENTER);
    finish.setFont(new Font("Arial Black", Font.BOLD, 33));
    finish.setForeground(Color.GREEN); //文字の色
    finish.setBorderPainted(false); //ボタンの枠削除
    finish.setBounds(746,350+20,240,135);

    try {
      imgBack = ImageIO.read(new File("vs.jpg"));
    } catch (Exception e) {
        System.out.println(e);
        System.exit(0);
    }
    add(start);add(setting);add(finish);
    this.setVisible(true);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage(imgBack, 0, -80, 1080, 680, null);
    this.paintLine(g);
  }
  
  public void paintLine(Graphics g){
    Font f2 = new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 100);
		Graphics2D g2 = (Graphics2D)g;
 
		//図形や線のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
		//文字描画のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 
		//アンチエイリアシングありで描画
    for(int i=0;i<141;i++){
      g.setColor(new Color(0,0,0,210-(int)(i*1.5)));
      g.fillRect(400+i,300+20,1,40);
      g.fillRect(680-i,300+20,1,40);
      if(i<88||i>140){
        g.fillRect(400+i,180+20,1,30);
        g.fillRect(680-i,180+20,1,30);
      }
    }
    g.setColor(new Color(0,0,0,210));
    g.fillRect(0,500+20,1080,60);
    g.fillRect(0,300+20,400,40);
    g.fillRect(680,300+20,1080,40);
    g.fillRect(0,180+20,400,30);
    g.fillRect(680,180+20,1080,30);
    g.fillRect(0,100+20,1080,20);
    g.fillRect(0,50+20,1040,10);
    g.fillRect(5,17+20,940,5);
    int xpoint[] = {505,520,350,270};
    int ypoint[] = {0,0,600,600};
    //g.fillPolygon(xpoint,ypoint,4);
    for(int j=0; j<2; j++){
      g.fillPolygon(xpoint,ypoint,4);
      for(int i=0; i<4; i++){xpoint[i] = (1080 - xpoint[i]);}//線対称
    }
    int xpoint1[] = {425,440,0,-80};
    int ypoint1[] = {0,0,460,460};
    for(int j=0; j<2; j++){
      g.fillPolygon(xpoint1,ypoint1,4);
      for(int i=0; i<4; i++){xpoint1[i] = (1080 - xpoint1[i]);}//線対称
    }
    int xpoint2[] = {345,360,0,-80};
    int ypoint2[] = {0,0,200,200};
    for(int j=0; j<2; j++){
      g.fillPolygon(xpoint2,ypoint2,4);
      for(int i=0; i<4; i++){xpoint2[i] = (1080 - xpoint2[i]);}//線対称
    }
    int xpoint4[] = {265,280,0,-80};
    int ypoint4[] = {0,0,100,100};
    for(int j=0; j<2; j++){
      g.fillPolygon(xpoint4,ypoint4,4);
      for(int i=0; i<4; i++){xpoint4[i] = (1080 - xpoint4[i]);}//線対称
    }
    int xpoint3[] = {205,220,0,-150};
    int ypoint3[] = {0,0,40,40};
    g.fillPolygon(xpoint3,ypoint3,4);
    g.setFont(f2);
    g.setColor(new Color(0,0,255));
    g.drawString("REVERSI", 355, 155);
    g.setColor(new Color(255,0,0));
    g.drawString("REVERSI", 350, 150);
  }
}