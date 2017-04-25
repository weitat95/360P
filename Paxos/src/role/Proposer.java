package role;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import run.MessageSendThread;
import run.Server;
import message.AcceptMessage;
import message.Message;
import message.ProposeMessage;

public class Proposer extends PaxosRole{
  int seq;
  Server server;
  public Proposer(Integer myID,Integer paxInstance,Integer seq,ArrayList<String> servers){
    super(myID,paxInstance,servers);
    this.seq=seq;
  }

  
  public void startProposal(){
    System.out.println("[DEBUG]: Starting Prepare State: Sending out prepare messages");
    Message prepare=new ProposeMessage(myID,seq,paxosInstance);
    saveToDisk((seq));
    for(int i=0;i<servers.size();i++){
      if(i!=myID-1){
        Thread t=new Thread(new MessageSendThread(prepare,servers.get(i),server));
        t.start();
      }
    }
  }
  public void sendAccepts(String command){
    System.out.println("[DEBUG]: Starting Phase2: Sending out accept! messages");
    AcceptMessage am=new AcceptMessage(myID,seq,paxosInstance,command);
    for(int i=0;i<servers.size();i++){
      if(i!=myID-1){
      Thread t=new Thread(new MessageSendThread(am,servers.get(i),server));
      t.start();
      }
    }
  }
  public void saveToDisk(int str){
    try{
      PrintWriter writer =new PrintWriter(myID+"_LastProposalLog.txt","UTF-8");
      writer.println(str);
      writer.close();
    }catch (IOException e){
      e.printStackTrace();
    }
    
    
  }
  public void setServer(Server server){
    this.server=server;
  }
}
