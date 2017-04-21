package run;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import message.Message;

public class MessageReceiveThread implements Runnable{
  Integer port;
  BlockingQueue<Message> q;
  public MessageReceiveThread(String server,BlockingQueue<Message> q){
    assert(server!=null && q!=null);
    this.q=q;
    Scanner token = new Scanner(server);
    token.useDelimiter(":");
    token.next();
    port=token.nextInt();
    assert(port!=null);
  }
  @Override
  public void run() {
    ServerSocket listernerSocket;
    ObjectInputStream ois;
    PrintWriter pout;
    while(true){
      try {
        listernerSocket = new ServerSocket(port);
        Socket s = null;
        while((s=listernerSocket.accept())!=null){
          Thread t=new Thread(new MessageReceiveHandler(s,q));
          t.start();
          /*
          ois = new ObjectInputStream(s.getInputStream());
          pout = new PrintWriter(s.getOutputStream());
          q.add((Message) ois.readObject());
          //System.out.println(q.poll().getContent());
          pout.println("Acknowledge");
          pout.flush();
          */
          //s.close();
        }
        
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      //} catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}
