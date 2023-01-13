//斉藤

import java.util.ArrayList;
class Ai_3 extends Model{
  final boolean is_maxcount=false;//置く最大数で判定するのか、相手が置く際小数で判定するのか
  final int future_hand_num=3;//何手先まで読むのか
  private int result_init_num=0;//is_maxcountがtrue or falseで判定
  private int recursive_end_num=1;//何回目の再帰で終了するか
  private int player;
  private int board_size;
  private Model.ReversiModel reversiModel;//実際に動かすmodel
  private Model.ReversiModel virtualModel;//仮想的に動かして様子を見るmodel
  //back_judge_arrayの中身は変えないでね(変えるなら一度コピーしてからしてください。)
  private int[][] back_judge_array;
  
  //コンストラクタ
  public Ai_3(Model m){
    reversiModel = m.getReversiModel();
    virtualModel=(new Model()).getReversiModel();
    this.player=reversiModel.getOpponentStone(reversiModel.getPlayer());//playerの指定
    this.board_size=reversiModel.board_size;//盤面のサイズを指定。
    if(!is_maxcount){//Aiの考え方によって変更する必要のある変数
      result_init_num=100;
      recursive_end_num=2;
    }
  }
  //呼び出される関数
  public void run(){//みんなのプログラムを追加する。
    //再帰のことも考え、今の盤面を入れる配列を関数内で作成する。
    int[][] back_board_array= new int[board_size][board_size];
    //今の盤面をコピーする
    getBackBoardArray(back_board_array);
    //virtualの盤面を合わせる。
    virtualModel.setBoard_onlyAI(back_board_array);
    virtualModel.setPlayer(this.player);
    int[] storn_position=recursive_run(back_board_array,2*(future_hand_num-1)+1);
    reversiModel.xySetStone(storn_position[0],storn_position[1]);
  }
  

  public int[] recursive_run(int[][] parent_board,int n){//第一返り値:x,第二返り値:y,第二返り値:ひっくり返す数,
    int[][] board= new int[board_size][board_size];
    copyBoardArray(board,parent_board);//親のboardをコピー
      //can_put_positionで必要な変数
      back_judge_array=virtualModel.getJudgeBoardArray(virtualModel.getPlayer());
      ArrayList<int[]> can_put_position=getCanPutArray(back_judge_array);//置ける位置を配列に格納する
      //
      int length = can_put_position.size();//置ける位置の個数を数える
      int[] result={0,0,result_init_num};
      if(n==recursive_end_num){//結果を出す
        int max_or_min_over_storn=result_init_num;//最大(最小)のひっくり返す個数の数を記憶
        int good_position=0;//良い手の座標
        for(int i=0; i<length;i++){
          if(is_maxcount){//Aiの考え方による分岐
            int pre_max_over_storn=over_search(can_put_position.get(i)[0],can_put_position.get(i)[1],board,player);//ひっくり返す数をカウント
            if(max_or_min_over_storn<pre_max_over_storn){//もし現状での最善手なら、良い手として記憶する。
              max_or_min_over_storn=pre_max_over_storn;
              good_position=i;
            }
          }else{
            int pre_min_over_storn=over_search(can_put_position.get(i)[0],can_put_position.get(i)[1],board,virtualModel.getOpponentStone(player));
            if(max_or_min_over_storn>pre_min_over_storn){//現状の最善手なら良い手として記憶する
              max_or_min_over_storn=pre_min_over_storn;
              good_position=i;
            }
          }
          
        }
        //returnように記憶する
        result[0]=can_put_position.get(good_position)[0];
        result[1]=can_put_position.get(good_position)[1];
        result[2]=max_or_min_over_storn;//最大(最小)のひっくり返す数の記憶
      }else{
        int[] pre_result={0,0,result_init_num};//仮の座標の記憶
        for(int i=0; i<length;i++){
          if(n%2==1){//仮想空間上の盤面の置くplayerの指定
            virtualModel.setPlayer(this.player);
          }else{
            virtualModel.setPlayer(reversiModel.getOpponentStone(this.player) );
          }
          virtualModel.setBoard_onlyAI(board);//仮想空間の盤面を現在の再帰の盤面に合わせる。(再帰内での初期化)
          virtualModel.xySetStone(can_put_position.get(i)[0], can_put_position.get(i)[1]);//仮想空間の盤面に置く
          if((virtualModel.getPassFlag("Ai")==1&&n%2==0)||(virtualModel.getFinishFlag("Ai")==1&&n%2==0)){//もし相手が置くときにpassまたはfinishかの判定
            virtualModel.getPassFlag();//passflagを初期化
            virtualModel.getFinishFlag();//finishflagを初期化
            continue;//最悪手と認定
          }else if(virtualModel.getPassFlag("Ai")==1&&n%2==1){//もし自分が置いてpassとなったなら
            virtualModel.getPassFlag();
            result[0]=can_put_position.get(i)[0];
            result[1]=can_put_position.get(i)[1];
            result[2]=65;//最大の優先度
          }else if(virtualModel.getFinishFlag("Ai")==1&&n%2==1){//もし自分が置いてfinishとなったなら
            virtualModel.getFinishFlag();
            result[0]=can_put_position.get(i)[0];
            result[1]=can_put_position.get(i)[1];
            result[2]=66;//passよりも高い優先度
          }else{//pass,finishではない
            pre_result=recursive_run(virtualModel.getBoardArray(),n-1);//再帰する
          }
          if(is_maxcount){//Aiの考え型による分岐
            if(result[2]<pre_result[2]){
                result[0]=can_put_position.get(i)[0];
                result[1]=can_put_position.get(i)[1];
                result[2]=pre_result[2];
              }
          }else{
            if(result[2]>pre_result[2]){
                result[0]=can_put_position.get(i)[0];
                result[1]=can_put_position.get(i)[1];
                result[2]=pre_result[2];
              }
          }
          
        }
      }
      return result;
  }

  //現在の盤面のコピー
  public void getBackBoardArray(int[][] back_board_array){
    copyBoardArray(back_board_array,reversiModel.getBoardArray());
  }
  
  //8*8配列をコピーする(第一引数:コピー先,第二引数:コピー元)
  public void copyBoardArray(int[][] target_copy,int[][] source_copy){
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        target_copy[i][j]=source_copy[i][j];
      } 
    }
  }
  //置ける位置を配列に格納する。
  public ArrayList<int[]> getCanPutArray(int[][] targetBoard){//board_jadge_arryaに直すと思う
    ArrayList<int[]> can_put_position = new ArrayList<>();
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        if(targetBoard[i][j]==3){
          int[] k={i,j};
          can_put_position.add(k);
        }
      }
    }
    return can_put_position;
  }

  //引数に渡した配列でx,yの位置に石をおいてひっくり返して、ひっくり返した石の数を返す。
  private int over_search(int x,int y,int[][] back_board_array,int player){
    int count=0;//ひっくり返した数。
    int search_x=x;int search_y=y;
    for(int i=-1;i<=1;i++){
      for(int j=-1;j<=1;j++){
        search_x=x+i;
        search_y=y+j;
        if(search_x==-1||search_y==-1||search_x==8||search_y==8){//範囲外なら抜ける。
          continue;
        }else if(back_board_array[search_x][search_y]!=reversiModel.getOpponentStone(player)){//相手の石じゃないなら抜ける
          continue;
        }
        while(search_x!=-1&&search_y!=-1&&search_x!=8&&search_y!=8&&back_board_array[search_x][search_y]==reversiModel.getOpponentStone(player)){//範囲内かつ相手の石ならループする
          search_x+=i;search_y+=j;
        }
        if(search_x!=-1&&search_y!=-1&&search_x!=8&&search_y!=8&&back_board_array[search_x][search_y]==player){//範囲内であるかつ自分の石である
          search_x-=i;search_y-=j;
          while(search_x!=x||search_y!=y){
            back_board_array[search_x][search_y]=player;//ひっくり返す。
            count++;//ひっくり返した数を数える。
            search_x-=i;search_y-=j;
          }
        }
      }
    }
    return count;
  }
}
