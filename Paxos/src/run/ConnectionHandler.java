package run;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import message.Message;

public class ConnectionHandler {
  
  BlockingQueue<Message> receiveBuffer=new LinkedBlockingDeque<Message>();
  BlockingQueue<Message> sendBuffer=new LinkedBlockingDeque<Message>();
  String server;
  public ConnectionHandler(String serverIPPort){
    assert(serverIPPort!=null);
    this.server=serverIPPort;
  }
  public void startSender(){
   // Thread t=new Thread(new MessageSendThread(sendBuffer,server));
   // t.start();
  }
  public void sendMessage(Message m){
    sendBuffer.add(m);
  }
  public void startReceiver(){
   // Thread t=new Thread(new MessageReceiveThread(server,receiveBuffer));
   // t.start();
  }
}
