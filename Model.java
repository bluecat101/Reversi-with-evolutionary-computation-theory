import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.*;

@SuppressWarnings("deprecation")

class Model {
  
  private ReversiModel reversiModel=new ReversiModel();
  public ReversiModel getReversiModel(){
    return reversiModel;
  }
class ReversiModel extends Observable{
  final int board_size=8;
  final int sizeOfOne=70;
  private int[][] board_array= new int[board_size][board_size];
  private int[][] judge_array= new int[board_size][board_size];
  private int pikapika_x=2;//阪上
  private int pikapika_y=3;//阪上
  private int player;
  private int pass_flag,finish_flag;
  public ReversiModel(){
    initBoard();

  }

  //コントローラーから呼び出される。playerは1,2で渡してほしい。
  public void setStone(int mouse_x, int mouse_y){
    // int x=mouse_x;int y=mouse_y;
    int x = transformMousePoint(mouse_x);//mouseの座標の変換
    int y = transformMousePoint(mouse_y);//mouseの座標の変換
    System.out.println(x+" "+y);
    if(x==-1||y==-1||board_array[x][y]!=0){//範囲外
      return ;
    }
    //searchする座標を探す
    judge_array = getJudgeBoardArray(player);
    if(judge_array[x][y]==3){//自分のところに置ける
      search(x,y);
      board_array[x][y]=player;//自分の位置に置く
      judge_array = getJudgeBoardArray(getOpponentStone(player));//相手プレイヤーが置けるかどうか
      if(canPut()==true){//置けるなら
        player=getOpponentStone(player);//プレイヤーの交代
      }else{
        judge_array = getJudgeBoardArray(player);//自分プレイヤーの置ける位置を格納。
        if(canPut()==true){
          pass_flag=1;
        }else{
          finish_flag=1;
        }
      }
      setChanged();
      notifyObservers();
    }
  }
  private void search(int x,int y){
    int search_x=x;int search_y=y;
    for(int i=-1;i<=1;i++){
      for(int j=-1;j<=1;j++){
        search_x=x+i;
        search_y=y+j;
        if(search_x==-1||search_y==-1||search_x==8||search_y==8){//範囲外なら抜ける。
          continue;
        }else if(board_array[search_x][search_y]!=getOpponentStone(player)){//相手の石じゃないなら抜ける
          continue;
        }
        while(search_x!=-1&&search_y!=-1&&search_x!=8&&search_y!=8&&board_array[search_x][search_y]==getOpponentStone(player)){//範囲内かつ相手の石ならループする
          search_x+=i;search_y+=j;
        }
        if(search_x!=-1&&search_y!=-1&&search_x!=8&&search_y!=8&&board_array[search_x][search_y]==player){//範囲内であるかつ自分の石である
          search_x-=i;search_y-=j;
          while(search_x!=x||search_y!=y){
            board_array[search_x][search_y]=player;//ひっくり返す。
            search_x-=i;search_y-=j;
          }
        }
      }
    }
  }
  private boolean pre_search(int x,int y,int player){
    int search_x=x;int search_y=y;
    for(int i=-1;i<2;i++){
      for(int j=-1;j<2;j++){
        search_x=x+i;search_y=y+j;

        if(search_x==-1||search_y==-1||search_x==8||search_y==8){//範囲外なら抜ける。
          continue;
        }else if(board_array[search_x][search_y]!=getOpponentStone(player)){//相手の石じゃないなら抜ける
          continue;
        }
        while(search_x!=-1&&search_y!=-1&&search_x!=8&&search_y!=8&&board_array[search_x][search_y]==getOpponentStone(player)){//範囲内かつ相手の石ならループする
          search_x+=i;search_y+=j;
        }
        //置いた石の真上が自分の石の時にも実行されるが、while文には入らない。
        if(search_x!=-1&&search_y!=-1&&search_x!=8&&search_y!=8&&board_array[search_x][search_y]==player){//範囲内である
          return true;//置けること返す。
        }
      }
    }
    return false;
  }
  //相手のプレイヤーの番号を返す関数。
  private int getOpponentStone(int player){
    if(player == 1){
      return 2;
    }
    return 1;
  }
  //マウスの座標を配列に変換する関数。
  private int transformMousePoint(int mouse){
    if(mouse<20||mouse>=sizeOfOne*8+20){
      return -1;
    }
    mouse-=20;
    int index=mouse/sizeOfOne;
    return index;
  }
  //modelが呼び出す。---------
  public int[][] getBoardArray(){
    return board_array;
  }
  //置けるかどうかの判定結果を格納して配列を返す関数。
  public int[][] getJudgeBoardArray(int player){
    int[][] judge_array= new int[board_size][board_size];//boardの用意
    //board_arrayの内容をコピー
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        judge_array[i][j]=board_array[i][j];
      }
    }
    //置ける位置を判定
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        if(board_array[i][j]==0&&pre_search(i,j,player)==true){
          judge_array[i][j]=3;//置けることが判明したら3を入れる。
        }      
      }
    }
    return judge_array;
  }
  public int getPlayer(){
    return player;
  }
  public int getPassFlag(){
    if(pass_flag==0){//passでない。
      return pass_flag;
    }else{
      pass_flag=0;//初期化
      return 1;//元の値
    }
   
  }
  public int getFinishFlag(){
    if(finish_flag==0){//finishでない。
      return finish_flag;
    }else{
      return 1;//元の値
    }
   
  }
  //置けるかどうかを判定する関数
  public boolean canPut(){
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        if(judge_array[i][j]==3){
          setPikaPika_x(i);
          setPikaPika_y(j);
          return true;//playerは置ける
        }
      }
    }
    return false;
  }
 
  public void initBoard(){
    pass_flag=0;
    finish_flag=0;
    player=1;
    pikapika_x=2;
    pikapika_y=3;
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        board_array[i][j]=0;
      }
    }
    //white
    board_array[3][3]=2;board_array[4][4]=2;
    //black
    board_array[3][4]=1;board_array[4][3]=1;
    setPikaPika_x(2);
    setPikaPika_y(3);
    judge_array = getJudgeBoardArray(player);
    setChanged();
    notifyObservers();
  }
  public int countStorn(int colornum){
    int count=0;
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        if(board_array[i][j]==colornum){
          count++;
        }
      }
    }
    return count;
  }
  //光る座標
  public int getPikaPika_x(){ //ココから阪上↓
    return pikapika_x;
  }
  public int getPikaPika_y(){
    return pikapika_y;
  }
  public void setPikaPika_x(int x){
    pikapika_x = x;
  }
  public void setPikaPika_y(int y){
    pikapika_y = y;
  }

  public int distance(int x1, int y1, int x2, int y2){
    int d = (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
    return d;
  }

  public void next_position(int x, int y,int direction){//0:右,1:左,2:下,3:上
    int[] array={0,8,0,8};
    switch(direction){
      case 0:
      array[direction]=getPikaPika_x()+1;
      break;
      case 1:
      array[direction]=getPikaPika_x();
      break;
      case 2:
      array[direction]=getPikaPika_y()+1;
      break;
      case 3:
      array[direction]=getPikaPika_y();
      break;
    }
    int next_x = 30, next_y = 30;//十分に離れているとする。
    int[][] arr = getJudgeBoardArray(player);
    for(int i=array[0]; i<array[1]; i++){
      for(int j=array[2]; j<array[3]; j++){
        if(arr[i][j] == 3){
          if(distance(x,y,next_x,next_y)>distance(x,y,i,j)){next_x = i; next_y = j;}
        }
      }
    }
    if(next_x == 30 || next_y == 30){next_x = x; next_y = y;}
    System.out.println(next_x+"    "+next_y);
    setPikaPika_x(next_x); setPikaPika_y(next_y);
    setChanged();
    notifyObservers();
  }
  public void xySetStone(int x,int y){//配列値でsetStoneを呼び出す。
    setStone(20+70*x, 20+70*y);
  }
}

}