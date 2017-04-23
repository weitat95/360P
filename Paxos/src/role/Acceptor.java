package role;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import run.MessageSendThread;
import message.Message;
import message.PromiseAgreeMessage;

public class Acceptor extends PaxosRole{
  AtomicInteger serverReceivedSeq;
  int seqReceived;
  String serverCommand;
  public Acceptor(Integer myID,Integer paxInstance,Integer seq,ArrayList<String> servers,AtomicInteger receivedSeq,String Value){
    super(myID,paxInstance,servers);
    this.serverReceivedSeq=receivedSeq;
    this.seqReceived=seq;
    this.serverCommand=command;
    this.startRole();
  }
  public void startRole(){
    if(serverReceivedSeq.get()<seqReceived){
      //received a larger sequence number
      serverReceivedSeq.set(seqReceived);
      Message promiseagree=new PromiseAgreeMessage(myID,serverReceivedSeq.get(),paxosInstance,command);
      Thread t=new Thread(new MessageSendThread(promiseagree,servers.get(0)));
      t.start();
    }
  }
}
