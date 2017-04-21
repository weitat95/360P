package run;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import message.Message;

public class MessageReceiveThread implements Runnable{
  Integer port;
  public MessageReceiveThread(String server,Message m){
    assert(server!=null);
    Scanner token = new Scanner(server);
    token.useDelimiter(":");
    token.next();
    port=token.nextInt();
    assert(port!=null);
  }
  @Override
  public void run() {
    ServerSocket listernerSocket;
    Socket s = null;
    try {
      listernerSocket = new ServerSocket(port);
      //LISTEN FOR PORT
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}
