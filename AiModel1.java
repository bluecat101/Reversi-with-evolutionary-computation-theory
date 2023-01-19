//鈴村用

import java.util.ArrayList;
class Ai_1 extends Model{

  private int player;
  private int board_size;
  private Model.ReversiModel reversiModel;
  //back_judge_arrayの中身は変えないでね(変えるなら一度コピーしてからしてください。)
  private int[][] back_judge_array;
  private Model.ChatModel chatModel;// chatmodel
  
  //コンストラクタ
  public Ai_1(Model m ,int aiPlayer){//引数の削除
    reversiModel = m.getReversiModel();
    chatModel = m.getChatModel();
    // this.player=reversiModel.getOpponentStone(reversiModel.getPlayer());//playerの指定
    this.player=aiPlayer;
    this.board_size=reversiModel.board_size;//盤面のサイズを指定。
  }
  
  //呼び出される関数
  public void run(){//みんなのプログラムを追加する。
    System.out.println("1da-");
    back_judge_array=reversiModel.getJudgeBoardArray(player);
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        if(back_judge_array[i][j]==3){
          chatModel.writeHistroy(i,j,reversiModel.getIsYourTurn());// 履歴に書く。
          reversiModel.xySetStone(i,j);
          return;
        }
      }
    }
    //再帰のことも考え、今の盤面を入れる配列を関数内で作成する。
    // int[][] back_board_array= new int[board_size][board_size];
    //今の盤面をコピーする
    // getBackBoardArray(back_board_array);
    //再帰のことも考え、置ける位置の配列を関数内で作成する。
    // ArrayList<int[]> can_put_position=getCanPutArray();

    //ここに盤面から選んだ値をresultに格納する。
    //
    //
    //例:can_put_positionの中の値を一つ一つback_board_arrayに代入して、
    //ひっくり返った数を数えて一番多い位置をresultに入れる。
    //
    //

    // return result;//置く座標の値をresultに格納する。
  }
  
  //現在の盤面のコピー
  // public void getBackBoardArray(int[][] back_board_array){
  //   copyBoardArray(back_board_array,reversiModel.getBoardArray());
  // }
  
  // //8*8配列をコピーする(第一引数:コピー先,第二引数:コピー元)
  // public void copyBoardArray(int[][] target_copy,int[][] source_copy){
  //   for(int i=0;i<board_size;i++){
  //     for(int j=0;j<board_size;j++){
  //       target_copy[i][j]=source_copy[i][j];
  //     } 
  //   }
  // }
  // //置ける位置を配列に格納する。
  // public ArrayList<int[]> getCanPutArray(){
  //   ArrayList<int[]> can_put_position = new ArrayList<>();
  //   for(int i=0;i<board_size;i++){
  //     for(int j=0;j<board_size;j++){
  //       if(back_judge_array[i][j]==3){
  //         int[] k={i,j};
  //         can_put_position.add(k);
  //       }
  //     }
  //   }
  //   return can_put_position;
  // }

  // //引数に渡した配列でx,yの位置に石をおいてひっくり返して、ひっくり返した石の数を返す。
  // private int back_search(int x,int y,int[][] back_board_array){
  //   int count=0;//ひっくり返した数。
  //   int search_x=x;int search_y=y;
  //   for(int i=-1;i<=1;i++){
  //     for(int j=-1;j<=1;j++){
  //       search_x=x+i;
  //       search_y=y+j;
  //       if(search_x==-1||search_y==-1||search_x==8||search_y==8){//範囲外なら抜ける。
  //         continue;
  //       }else if(back_board_array[search_x][search_y]!=reversiModel.getOpponentStone(player)){//相手の石じゃないなら抜ける
  //         continue;
  //       }
  //       while(search_x!=-1&&search_y!=-1&&search_x!=8&&search_y!=8&&back_board_array[search_x][search_y]==reversiModel.getOpponentStone(player)){//範囲内かつ相手の石ならループする
  //         search_x+=i;search_y+=j;
  //       }
  //       if(search_x!=-1&&search_y!=-1&&search_x!=8&&search_y!=8&&back_board_array[search_x][search_y]==player){//範囲内であるかつ自分の石である
  //         search_x-=i;search_y-=j;
  //         while(search_x!=x||search_y!=y){
  //           back_board_array[search_x][search_y]=player;//ひっくり返す。
  //           count++;//ひっくり返した数を数える。
  //           search_x-=i;search_y-=j;
  //         }
  //       }
  //     }
  //   }
  //   return count;
  // }
}