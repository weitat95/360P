package run;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import message.Message;

public class MessageReceiveHandler implements Runnable{
  Socket s;
  BlockingQueue q;
  public MessageReceiveHandler(Socket s, BlockingQueue q){
    assert(s!=null && q!=null);
    this.s=s;
    this.q=q;
  }
  @Override
  public void run() {
    ObjectInputStream ois;
    PrintWriter pout;
    while(true){
      try {
        ois=new ObjectInputStream(s.getInputStream());
        pout=new PrintWriter(s.getOutputStream());
        q.add((Message) ois.readObject());
        pout.println("Acknowledge");
        pout.flush();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }
    
  }
}
