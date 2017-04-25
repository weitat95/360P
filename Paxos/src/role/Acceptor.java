package role;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import run.MessageSendThread;
import run.Server;
import message.AcceptAcceptMessage;
import message.Message;
import message.PromiseAgreeMessage;

public class Acceptor extends PaxosRole{
  AtomicInteger serverReceivedSeq;
  int seqReceived;
  String serverCommand;
  Integer leaderID;
  Server server;
  public Acceptor(Integer myID,Integer paxInstance,Integer seq,ArrayList<String> servers,AtomicInteger receivedSeq,String value){
    super(myID,paxInstance,servers);
    this.serverReceivedSeq=receivedSeq;
    this.seqReceived=seq;
    this.serverCommand=value;
  }
  public Acceptor(Integer myID,Integer paxInstance,Integer seq,ArrayList<String> servers,String value){
    super(myID,paxInstance,servers);
    this.seqReceived=seq;
    this.serverCommand=value;
  }
  public void startRole(){
    System.out.println("##[DEBUG]: "+serverReceivedSeq.get()+"should be less than"+seqReceived);
    //if(serverReceivedSeq.get()<seqReceived){
      //received a larger sequence number
      System.out.println("[DEBUG]: Acceptor(id: "+myID+"): sending out promise messages");
      serverReceivedSeq.set(seqReceived);
      try{
        PrintWriter writer =new PrintWriter(myID+"_highestSeenProposal.txt","UTF-8");
        writer.println(seqReceived);
        writer.close();
      }catch (IOException e){
        e.printStackTrace();
      }
      Message promiseagree=new PromiseAgreeMessage(myID,serverReceivedSeq.get(),paxosInstance,serverCommand);
      Thread t=new Thread(new MessageSendThread(promiseagree,servers.get(leaderID-1),server));
      t.start();
    //}else{
      
    //}
  }
  public void startAcceptingPhase(){
    System.out.println("[DEBUG]: Acceptor(id: "+myID+"): sending to leader telling that we accepted the value");
    AcceptAcceptMessage aam=new AcceptAcceptMessage(myID,seqReceived,paxosInstance,serverCommand);
    Thread t=new Thread(new MessageSendThread(aam,servers.get(leaderID-1),server));
    t.start();
  }
  public void setLeader(Integer leaderID){
    this.leaderID=leaderID;
  }
  public void setServer(Server server){
    this.server=server;
  }
}
