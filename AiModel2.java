//阪上用

import java.util.ArrayList;
import java.util.Random;
class Ai_2 extends Model{
  private int player;
  private int board_size;
  private Model.ReversiModel reversiModel;
  //back_judge_arrayの中身は変えないでね(変えるなら一度コピーしてからしてください。)
  private int[][] back_judge_array;
  
  //コンストラクタ
  public Ai_2(Model m){
    reversiModel = m.getReversiModel();
    this.player=reversiModel.getOpponentStone(reversiModel.getPlayer());//playerの指定
    this.board_size=reversiModel.board_size;//盤面のサイズを指定。
  }
  
  //呼び出される関数
  public void run(){//みんなのプログラムを追加する。
    int x=-1,y=-1;
    System.out.println("2da-");
    back_judge_array=reversiModel.getJudgeBoardArray(player);
    ArrayList<int[]> can_put_array_edge = new ArrayList<>();
    ArrayList<int[]> can_put_array_sub_edge = new ArrayList<>();
    ArrayList<int[]> can_put_array_otherwise = new ArrayList<>();
    ArrayList<int[]> can_put_array_outside = new ArrayList<>();
    ArrayList<int[]> can_put_array_inside = new ArrayList<>();
    for(int i=0;i<board_size;i++){
      for(int j=0;j<board_size;j++){
        if(back_judge_array[i][j]==3 && ((i==0 || i==7) && (j==0 || j==7))){
          int[] k={i,j};
          can_put_array_edge.add(k);
        }else if(back_judge_array[i][j]==3 && ((i<=1 || i>=6) && (j<=1 || j>=6))){
          int[] k={i,j};
          can_put_array_sub_edge.add(k);
        }else if(back_judge_array[i][j]==3 && (((i==0 || i==7) && ((j>=2 && j<=5))) || ((j==0 || j==7) && ((i>=2 && i<=5))))){
          int[] k={i,j};
          can_put_array_outside.add(k);
        }else if(back_judge_array[i][j]==3 && ((i>=2 && i<=5) && (j>=2 && j<=5))){
          int[] k={i,j};
          can_put_array_inside.add(k);
        }else if(back_judge_array[i][j]==3){
          int[] k={i,j};
          can_put_array_otherwise.add(k);
        }
      }
    }
    int edge_size = can_put_array_edge.size();
    int sub_edge_size = can_put_array_sub_edge.size();
    int outside_size = can_put_array_outside.size();
    int inside_size = can_put_array_inside.size();
    int otherwise_size = can_put_array_otherwise.size();
    Random rand = new Random();
    if(edge_size != 0){
      int element = rand.nextInt(edge_size);
      reversiModel.xySetStone(can_put_array_edge.get(element)[0],can_put_array_edge.get(element)[1]);
      return;
    }else if(outside_size != 0){
      int element = rand.nextInt(outside_size);
      reversiModel.xySetStone(can_put_array_outside.get(element)[0],can_put_array_outside.get(element)[1]);
      return;
    }else if(inside_size != 0){
      int element = rand.nextInt(inside_size);
      reversiModel.xySetStone(can_put_array_inside.get(element)[0],can_put_array_inside.get(element)[1]);
      return;
    }else if(otherwise_size != 0){
      int element = rand.nextInt(otherwise_size);
      reversiModel.xySetStone(can_put_array_otherwise.get(element)[0],can_put_array_otherwise.get(element)[1]);
      return;
    }else if(sub_edge_size != 0){
      int element = rand.nextInt(sub_edge_size);
      reversiModel.xySetStone(can_put_array_sub_edge.get(element)[0],can_put_array_sub_edge.get(element)[1]);
      return;
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