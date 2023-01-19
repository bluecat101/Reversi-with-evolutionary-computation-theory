import javax.swing.*;
import java.awt.*;
// import java.awt.event.*;
// import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
// import java.io.IOException;

public class SinglePanel extends JPanel{
  JButton returnButton,startButton;
  JButton level1,level2,level3;
  JComboBox<Object> cb;
  JComboBox<Object> first;
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
    // cb=new JComboBox<>();
    DefaultComboBoxModel<Object> md = new DefaultComboBoxModel<>();
    md.addElement(new ComboLabel("Level1", new ImageIcon("./osero-black.png")));
    md.addElement(new ComboLabel("Level2", new ImageIcon("./osero-black.png")));
    md.addElement(new ComboLabel("Level3", new ImageIcon("./osero-black.png")));

    cb = new JComboBox<>(md);

    ListCellRenderer<Object> renderer = new MyCellRenderer();
    cb.setRenderer(renderer);
    cb.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 40));
    cb.setBackground(Color.WHITE);
    // cb.addItem("level1");cb.addItem("level2");cb.addItem("level3");
    cb.setBounds(250,237,240,100);

    DefaultComboBoxModel<Object> md1 = new DefaultComboBoxModel<>();
    md1.addElement(new ComboLabel("先攻", new ImageIcon("./osero-black.png")));
    md1.addElement(new ComboLabel("後攻", new ImageIcon("./osero-black.png")));

    first = new JComboBox<>(md1);

    ListCellRenderer<Object> renderer1 = new MyCellRenderer();
    first.setRenderer(renderer1);
    first.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 40));
    first.setBackground(Color.WHITE);
    first.setBounds(550,237,240,100);

    // first=new JComboBox<>();
    // first.setFont(new Font("HGP創英角ﾎﾟｯﾌﾟ体", Font.BOLD, 60));
    // first.addItem("先攻");first.addItem("後攻");
    // first.setBounds(550,370,240,135);
    startButton = new JButton("Start",icon1);
    startButton.setRolloverIcon(icon2);
    startButton.setContentAreaFilled(false); //背景透明化
    startButton.setHorizontalTextPosition(JButton.CENTER);
    startButton.setFont(new Font("Arial Black", Font.BOLD, 30));
    startButton.setForeground(Color.GREEN); //文字の色
    startButton.setBorderPainted(false); //ボタンの枠削除
    startButton.setBounds(550,370,240,135);
    returnButton = new JButton("Back",icon1);
    returnButton.setRolloverIcon(icon2);
    returnButton.setContentAreaFilled(false); //背景透明化
    returnButton.setHorizontalTextPosition(JButton.CENTER);
    returnButton.setFont(new Font("Arial Black", Font.BOLD, 30));
    returnButton.setForeground(Color.GREEN); //文字の色
    returnButton.setBorderPainted(false); //ボタンの枠削除
    returnButton.setBounds(250, 370, 240, 135);
    add(returnButton);add(startButton);add(cb);add(first);
  }
  class ComboLabel{
    String text;
    Icon icon;

    ComboLabel(String text, Icon icon){
      this.text = text;
      this.icon = icon;
    }

    public String getText(){
      return text;
    }

    public Icon getIcon(){
      return icon;
    }
  }

  class MyCellRenderer extends JLabel implements ListCellRenderer<Object>{

    MyCellRenderer(){
      setOpaque(true);
    }

    public Component getListCellRendererComponent(
            JList<? extends Object> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus){

      ComboLabel data = (ComboLabel)value;
      setText(data.getText());
      setIcon(data.getIcon());

      if (isSelected){
        setForeground(Color.white);
        setBackground(Color.black);
      }else{
        setForeground(Color.black);
        setBackground(Color.white);
      }

      return this;
    }
  }
  
  public void paintComponent(Graphics g) {
    Font f2 = new Font("メイリオ", Font.BOLD, 80);
		Graphics2D g2 = (Graphics2D)g;
 
		//文字描画のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    FontMetrics fontMetrics = this.getFontMetrics(f2);

    g.drawImage(imgBack, 0, 0, 1080, 600, null);
    g.setFont(f2);
    int a=fontMetrics.stringWidth("Select AILevel");
    g.setColor(new Color(100,100,100));
    g.drawString("Select AILevel", 540-a/2+5, 195);
    g.setColor(new Color(30,200,30));
    g.drawString("Select AILevel", 540-a/2, 190);
  }
}