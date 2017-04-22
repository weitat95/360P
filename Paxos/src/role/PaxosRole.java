package role;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import message.Message;

public class PaxosRole {
  BlockingQueue<Message> receiveBuffer=new LinkedBlockingDeque<Message>();
  BlockingQueue<Message> sendBuffer=new LinkedBlockingDeque<Message>();
  public PaxosRole(){
    
  }
  public void sendMessage(Message m){
    
  }
}
