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
  public MessageSendThread(Message m,String server){
    assert(s!=null&&m!=null);
    Scanner token = new Scanner(server);
    token.useDelimiter(":");
    inet=new InetSocketAddress(token.next(),token.nextInt());
    assert(inet!=null);
    token.close();
    this.m=m;
  }
  @Override
  public void run() {
      Scanner sin;
      PrintStream pout;
      ObjectOutputStream oos;
      s=new Socket();
      try {
        //System.out.println("Connecting: "+inet.getPort()+" "+inet.getHostString());
        s.connect(inet);
        sin = new Scanner(s.getInputStream());
        //pout = new PrintStream(s.getOutputStream());
        oos = new ObjectOutputStream(s.getOutputStream());
        oos.writeObject(m);
        oos.flush();
          
        //System.out.println("Sent Message: "+m.toString());
        if(sin.nextLine().equals("Acknowledge")){
          s.close();
          //System.out.println("Received Acknowledgement");
        }  
      } catch (IOException e) {
        e.printStackTrace();
      }
    
  }
  
  
}
