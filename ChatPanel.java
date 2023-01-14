import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
// import javax.swing.border.LineBorder;
// import java.util.*;

@SuppressWarnings("deprecation")
class ChatPanel extends JPanel implements Observer ,AdjustmentListener{
  protected Model model;
  protected Model.ChatModel chatmodel;
  private ArrayList<StringBuffer> message;
  private ArrayList<Integer> player;
  protected Font font;
  protected FontMetrics fontMetrics;
  protected JScrollBar scrollbar;
  protected int stheight;
  protected int panelheight;
  protected int playernum;
  protected int opponent=2;
  
  public ChatPanel(Model m,JScrollBar scrollbar){
    model = m;
    this.scrollbar = scrollbar;
    this.scrollbar.addAdjustmentListener(this);
    scrollbar.setMaximum(0);
    chatmodel = model.getChatModel();
    message = chatmodel.getChatMessage();
    player  = chatmodel.getChatPlayer();

    //Chatmodelからplayerの番号を取得する。
    // playernum=chatmodel.getPlayerNum();
    
    //下の状態だとplayerの番号が2だから偶数回目のメッセージが自分側に表示される。
    playernum=2;
    if(playernum==2){opponent=1;}

    font = new Font("Serif",Font.PLAIN,20);
    fontMetrics = this.getFontMetrics(font);
    chatmodel.addObserver(this);
  }
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;
 
		//図形や線のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
		//文字描画のアンチエイリアシングの有効化
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    panelheight = this.getHeight();
    int newextray = panelheight -stheight -20;
    int extray = 0;
    if(newextray < extray){extray = newextray;}
    else{
      if(newextray - extray + 60 > panelheight){
        extray = newextray + 60 -panelheight;
      }
    }
    if(extray > 0){extray = 0;}
    drawString(g, extray);
  }

  public String getPostfix(int start,int line) {//line番目のStringBufferのstartからの文字列を返す
    if (start < 0) start = 0;
    if (start > message.get(line).length()) start = message.get(line).length();
    return message.get(line).substring(start);
  }

  public String getPrefix(int end,int line) {//line番目のStringBufferのendまでの文字列を返す
    if (end < 0) end = 0;
    if (end > message.get(line).length()) end = message.get(line).length();
    return message.get(line).substring(0,end);
  }

  public int getLength(int line) {//line番目のStringBufferの長さを返す
    return message.get(line).length();
  }

  public int buffersize(StringBuffer s){
    return fontMetrics.stringWidth(s.substring(0));
  }

  private void remake(){
    int panelwidth = this.getWidth();
    int i=message.size()-1;
    while(fontMetrics.stringWidth(getPostfix(0, i)) > panelwidth-60){
      // System.out.println(fontMetrics.stringWidth(getPostfix(0, i))+"  "+(panelwidth-60));
      message.add(i+1,new StringBuffer());//ArrayListにStringBufferを追加
      // System.out.print("fasd");
      int j=0;
      while(fontMetrics.stringWidth(getPrefix(j, i)) <= panelwidth-60){
        j++;
      }
      // System.out.println(""+fontMetrics.stringWidth(getPostfix(j, i)));
      if(fontMetrics.stringWidth(getPostfix(j, i))!=0){
        message.get(i+1).insert(0,getPostfix(j-1, i));
        message.get(i).delete(j-1, getLength(i));
      }
      i++;
    }
    if(fontMetrics.stringWidth(getPostfix(0, i))!=0){
      message.add(i+1,new StringBuffer());//ArrayListにStringBufferを追加
    }
  }

  private void round(Graphics g,int x,int y,int width,int height,int player){
    int dir=20;
    g.fillOval(x-dir/2, y, dir, dir);
    g.fillOval(x-dir/2+width, y, dir, dir);

    g.fillRect(x-dir/2, y+dir/2, dir, height-dir/2);
    g.fillRect(x-dir/2+width, y+dir/2, dir, height-dir/2);

    g.fillOval(x-dir/2, y+height-dir/2, dir, dir);
    g.fillOval(x-dir/2+width, y+height-dir/2, dir, dir);

    g.fillRect(x, y+height-dir/2, width, dir);

    if(player==playernum){
      g.fillPolygon(new int[] {x-dir/2+width,x+dir/2+width,x+dir/2+width+10}, new int[] {y+2,y+15,y+5}, 3);
    }else{
      g.fillPolygon(new int[] {x-dir/2+10,x-dir/2,x-dir}, new int[] {y+2,y+15,y+5}, 3);    }
  }

  private void drawString(Graphics g,int extray) {
    int h = fontMetrics.getAscent();
    int h1 = h;
    int chatflag=1;
    int pn=0;

    for(int i=0;i<message.size();i++){
      StringBuffer f = message.get(i);
      String s = new String(f);
      int j=i;
      int h1sub = h1;
      if(chatflag==1){
        if(player.get(pn)==playernum){
          g.setColor(Color.orange);
        }else{
          g.setColor(Color.green);
        }
        if(buffersize(message.get(j+1))==0){
          if(player.get(pn)==playernum){
            int x=20+145-buffersize(f)+5;
            int y=h1sub+extray+20-fontMetrics.getAscent();
            int width=buffersize(f)-10;
            int height=fontMetrics.getAscent();
            g.fillRect(x, y, width, height);

            round(g, x, y, width, height,playernum);
          }else{
            int x=25;
            int y=h1sub+extray+20-fontMetrics.getAscent();
            int width=buffersize(f)-10;
            int height=fontMetrics.getAscent();
            g.fillRect(x, y, width, height);

            round(g, x, y, width, height,opponent);
          }
        }else{
          if(player.get(pn)==playernum){
            int x=20+20+5;
            int width=115;
            int height=fontMetrics.getAscent();
            int ysub = h1sub+extray+20-fontMetrics.getAscent();
            while(buffersize(f)!=0){
              int y=h1sub+extray+20-fontMetrics.getAscent();
              g.fillRect(x,y,width,height);
              f=message.get(++j);
              h1sub +=h;
            }
            round(g, x, ysub, width, height*(j-i),playernum);
          }else{
            int x=25;
            int width=115;
            int height=fontMetrics.getAscent();
            int ysub = h1sub+extray+20-fontMetrics.getAscent();
            while(buffersize(f)!=0){
              int y=h1sub+extray+20-fontMetrics.getAscent();
              g.fillRect(x,y,width,height);
              f=message.get(++j);
              h1sub +=h;
            }
            round(g, x, ysub, width, height*(j-i),opponent);
          }
        }
        chatflag=0;
        pn++;
      }
      g.setColor(Color.black);
      g.setFont(font);
      if(player.get(pn-1)==playernum && (i==0 || buffersize(message.get(i-1))==0) && buffersize(message.get(i+1))==0){
        g.drawString(s,20+145-buffersize(f),h1+extray+20);
      }else{
        if(player.get(pn-1)==playernum){
          g.drawString(s,20+20,h1+extray+20);
        }else{
          g.drawString(s,20,h1+extray+20);
        }
      }
      h1 =h1+h;//それぞれのStringBufferをずらして置くために値を変更していく
      if(fontMetrics.stringWidth(s)==0){chatflag=1;}
    }
  }
  public void update(Observable o,Object arg){
    remake();
    stheight = fontMetrics.getAscent()*(message.size());
    if(panelheight -stheight -80<0){
      scrollbar.setMinimum(panelheight-20);
      // System.out.println("a+"+stheight);
      scrollbar.setMaximum(stheight);
      stheight = fontMetrics.getAscent()*(message.size());
      scrollbar.setValue(stheight);
    }
    this.repaint();
    // System.out.println(""+player.get(0));
    // System.out.println(""+message.size());
  }
  public void adjustmentValueChanged(AdjustmentEvent e) {
          JScrollBar sb = (JScrollBar)e.getSource();
      // スクロールバーの値を取得
      if(sb.getValue()!=0){
        stheight = sb.getValue();
        // System.out.println(sb.getValue());
        this.repaint();
      }
  }
}