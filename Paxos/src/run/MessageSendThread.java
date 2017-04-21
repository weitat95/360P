package run;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import message.*;


public class MessageSendThread implements Runnable{
  private Socket s;
  private Message m;
  private InetSocketAddress inet;
  BlockingQueue<Message> q;
  public MessageSendThread(BlockingQueue<Message> q,String server){
    assert(s!=null&&q!=null);
    Scanner token = new Scanner(server);
    token.useDelimiter(":");
    inet=new InetSocketAddress(token.next(),token.nextInt());
    assert(inet!=null);
    token.close();
    this.q=q;
  }
  @Override
  public void run() {
      Scanner sin;
      PrintStream pout;
      ObjectOutputStream oos;
      s=new Socket();
      try {
        System.out.println("Connecting: "+inet.getPort()+inet.getHostString());
        s.connect(inet);
        sin = new Scanner(s.getInputStream());
        pout = new PrintStream(s.getOutputStream());
        oos = new ObjectOutputStream(s.getOutputStream());
        while(true){
          Message m=q.take();
          oos.writeObject(m);
          oos.flush();
          
          System.out.println("Sent Message: "+m.getContent());
          if(sin.nextLine().equals("Acknowledge")){
         //   s.close();
            System.out.println("Received Acknowledgement");
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e){
        e.printStackTrace();
      }
    
  }
  
  
}
