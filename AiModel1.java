//鈴村用

class Ai_1 extends Model{
  private int player;//Aiの番号
  private int opponent;//相手の番号
  private int board_size;//盤面のサイズ
  private Model.ReversiModel reversiModel;//ReversiModelを保持
  //back_judge_arrayの中身は変えないでね(変えるなら一度コピーしてからしてください。)
  private int[][] back_judge_array;//石を置くことができる場所を保持
  private Model.ChatModel chatModel;// chatmodel
  private int[][] cell_weight = {
    {30, -12, 1, 0, 0, 1, -12, 30},
    {-12, -15, -3, -3, -3, -3, -15, -12},
    {1, -3, 0, -1, -1, 0, -3, 1},
    {0, -3, -1, -1, -1, -1, -3, 0},
    {0, -3, -1, -1, -1, -1, -3, 0},
    {1, -3, 0, -1, -1, 0, -3, 1},
    {-12, -15, -3, -3, -3, -3, -15, -12},
    {30, -12, 1, 0, 0, 1, -12, 30}//盤面を評価するための値をマスごとに定義
};
  
  //初期設定を行っている
  public Ai_1(Model m ,int aiPlayer){
    reversiModel = m.getReversiModel();
    chatModel = m.getChatModel();
    this.player=aiPlayer;//Aiの番号をセット
    opponent=reversiModel.getOpponentStone(player);//相手の番号をセット
    this.board_size=reversiModel.board_size;//盤面のサイズを指定。
  }
  
  //呼び出される関数
  public void run(){
    back_judge_array=reversiModel.getJudgeBoardArray(player);//置ける位置が分かる配列を入手
    int[][] back_board_array= new int[board_size][board_size];//新しく配列を用意
    int max=0,maxi=0,maxj=0;//最終的に一番高かった評価値とその座標を保存
    int flag=1;
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        if(back_judge_array[i][j]==3){
          getBackBoardArray(back_board_array);//現在の盤面を取得
          back_board_array = nextarray(i, j, back_board_array);//i,jに置いた時の次の盤面を入手
          if(flag==1){
            max=evaluate(back_board_array);maxi=i;maxj=j;flag=0;//比較対象として初めに得られた値をセット
          }else{
            if(max < evaluate(back_board_array)){
              max=evaluate(back_board_array);maxi=i;maxj=j;//もし、次の盤面の評価値がmaxより高かったらその値と座標を保存
            }
          }
        }
      }
    }
    chatModel.writeHistroy(maxi,maxj,reversiModel.getIsYourTurn());// 履歴に書く。
    reversiModel.xySetStone(maxi,maxj);//評価値が一番高かった座標に石を置く
    return;
  }

  //与えられた盤面に点数を付けるメソッド。点数が多いほど良い盤面
  public int evaluate(int[][] array){
    int result=0;//評価結果を格納
    for(int i=0;i<8;i++){
      for(int j=0;j<8;j++){
        if(array[i][j]==player){
          result += cell_weight[i][j];//自分の駒なら評価値を足す
        }
        if(array[i][j]==opponent){
          result -= cell_weight[i][j];//相手の駒なら評価値を引く
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

  //引数に渡した配列でx,yの位置に石をおいてひっくり返して、ひっくり返した後の配列を返す
  private int[][] nextarray(int x,int y,int[][] back_board_array){
    // int count=0;//ひっくり返した数。
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
            back_board_array[search_x][search_y]=player;//ひっくり返す
            search_x-=i;search_y-=j;
          }
        }
      }
    }
    back_board_array[x][y]=player;//石を置く
    return back_board_array;
  }
}