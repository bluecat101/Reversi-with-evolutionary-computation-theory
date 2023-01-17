import java.net.*;
import java.io.*;

class CommServer {
  private ServerSocket serverS = null;
  private Socket clientS = null;
  private PrintWriter out = null;
  private BufferedReader in = null;
  private int port = 0;
  private Model.ReversiModel reversiModel;
  private Model.ChatModel chatModel;

  CommServer() {
  }

  CommServer(int port, Model model) {
    open(port);
    this.reversiModel = model.getReversiModel();
    this.chatModel = model.getChatModel();
  }

  CommServer(CommServer cs) {
    serverS = cs.getServerSocket();
    open(cs.getPortNo());
  }

  ServerSocket getServerSocket() {
    return serverS;
  }

  int getPortNo() {
    return port;
  }

  boolean open(int port) {
    this.port = port;
    try {
      if (serverS == null) {
        serverS = new ServerSocket(port);
      }
    } catch (IOException e) {
      System.err.println("ポートにアクセスできません。");
      System.exit(1);
    }
    try {
      clientS = serverS.accept();
      out = new PrintWriter(clientS.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientS.getInputStream()));
    } catch (IOException e) {
      System.err.println("Acceptに失敗しました。");
      System.exit(1);
    }
    return true;
  }

  // clientのsend->serverのrecv後に実行
  // clientのrecvを発火させるためにout.println(msg)をする
  boolean send(String msg) {// サーバー側で石を置いたことを知らせる。
    if (out == null) {
      return false;
    }
    out.println(msg);// クライアントにサーバー側が石を置いたことを知らせる。
    return true;
  }

  // server->clientにp来るときに実行
  boolean send(int x, int y) {// サーバー側から石を置く(クライアントが先に置かない)。
    if (out == null) {
      return false;
    }
    String msg = x + " " + y;// 置いた場所を文字化
    out.println("reversi" + " " + msg);
    reversiModel.xySetStone(x, y);// サーバー側で石を置く
    return true;
  }

  boolean sendChat(String sentence, int chatPlayerNum) {//
    if (out == null) {
      return false;
    }
    chatModel.setChat(sentence, chatPlayerNum);
    sentence = "chat" + " " + sentence + " " + chatPlayerNum;
    out.println(sentence);// 文字としてサーバーに送信
    return true;
  }

  String recv() {// クライアントが置いた場所が文字列となっているので受け取る
    String msg = null;
    if (in == null) {
      return null;
    }
    try {
      msg = in.readLine();
      String[] xy_chat = msg.split(" ");// 文字列を数字化
      if (xy_chat[0].compareTo("reversi") == 0) {
        reversiModel.xySetStone(Integer.parseInt(xy_chat[1]), Integer.parseInt(xy_chat[2]));// クライアント側でも石を置く。
      } else if (xy_chat[0].compareTo("chat") == 0) {
        chatModel.setChat(xy_chat[1], Integer.parseInt(xy_chat[2]));
      }
    } catch (SocketTimeoutException e) {
      // System.err.println("タイムアウトです．");
      return null;
    } catch (IOException e) {
      System.err.println("受信に失敗しました。");
      System.exit(1);
    }
    return msg;
  }

  int setTimeout(int to) {
    try {
      clientS.setSoTimeout(to);
    } catch (SocketException e) {
      System.err.println("タイムアウト時間を変更できません．");
      System.exit(1);
    }
    return to;
  }

  void close() {
    try {
      in.close();
      out.close();
      clientS.close();
      serverS.close();
    } catch (IOException e) {
      System.err.println("ソケットのクローズに失敗しました。");
      System.exit(1);
    }
    in = null;
    out = null;
    clientS = null;
    serverS = null;
  }

}

class CommClient {
  Socket clientS = null;
  BufferedReader in = null;
  PrintWriter out = null;
  private Model.ReversiModel reversiModel;
  private Model.ChatModel chatModel;

  CommClient() {
  }

  CommClient(String host, int port, Model model) {
    open(host, port);
    this.reversiModel = model.getReversiModel();
    this.chatModel = model.getChatModel();

  }

  boolean open(String host, int port) {
    try {
      clientS = new Socket(InetAddress.getByName(host), port);
      in = new BufferedReader(new InputStreamReader(clientS.getInputStream()));
      out = new PrintWriter(clientS.getOutputStream(), true);
    } catch (UnknownHostException e) {
      System.err.println("ホストに接続できません。");
      System.exit(1);
    } catch (IOException e) {
      System.err.println("IOコネクションを得られません。");
      System.exit(1);
    }
    return true;
  }

  boolean send(int x, int y) {// clientが石を置いたときに動く
    if (out == null) {
      return false;
    }
    String msg = "reversi" + " " + x + " " + y;// 置いた場所を文字化
    out.println(msg);// 文字としてサーバーに送信
    return true;
  }

  boolean sendChat(String sentence, int chatPlayerNUM) {// clientが石を置いたときに動く
    if (out == null) {
      return false;
    }
    sentence = "chat" + " " + sentence + " " + chatPlayerNUM;
    out.println(sentence);// 文字としてサーバーに送信
    return true;
  }

  boolean recv() {// サーバー側が石を置いたことを認識する。
    String msg = null;
    if (in == null) {
      return false;
    }
    try {
      msg = in.readLine();// サーバーが置いた場所を文字列として取得
      String[] xy_chat = msg.split(" ");// 文字列を解析
      if (xy_chat[0].compareTo("reversi") == 0) {
        reversiModel.xySetStone(Integer.parseInt(xy_chat[1]), Integer.parseInt(xy_chat[2]));// クライアント側でも石を置く。
      } else if (xy_chat[0].compareTo("chat") == 0) {
        chatModel.setChat(xy_chat[1], Integer.parseInt(xy_chat[2]));
      }
    } catch (SocketTimeoutException e) {
      return false;
    } catch (IOException e) {
      System.err.println("受信に失敗しました。");
      System.exit(1);
    }
    return true;
  }

  int setTimeout(int to) {
    try {
      clientS.setSoTimeout(to);
    } catch (SocketException e) {
      System.err.println("タイムアウト時間を変更できません．");
      System.exit(1);
    }
    return to;
  }

  void close() {
    try {
      in.close();
      out.close();
      clientS.close();
    } catch (IOException e) {
      System.err.println("ソケットのクローズに失敗しました。");
      System.exit(1);
    }
    in = null;
    out = null;
    clientS = null;
  }
}
