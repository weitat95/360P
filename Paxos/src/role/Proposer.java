package role;

import java.util.ArrayList;

import run.MessageSendThread;
import message.AcceptMessage;
import message.Message;
import message.ProposeMessage;

public class Proposer extends PaxosRole{
  int seq;
  public Proposer(Integer myID,Integer paxInstance,Integer seq,ArrayList<String> servers){
    super(myID,paxInstance,servers);
    this.seq=seq;
  }

  
  public void startProposal(){
    System.out.println("Starting Prepare State: Sending out prepare messages");
    Message prepare=new ProposeMessage(myID,seq,paxosInstance);
    for(int i=0;i<servers.size();i++){
      //if(i!=myID-1){
        Thread t=new Thread(new MessageSendThread(prepare,servers.get(i)));
        t.start();
      //}
    }
  }
  public void sendAccepts(String command){
    System.out.println("Starting Phase2: Sending out accept! messages");
    AcceptMessage am=new AcceptMessage(myID,seq,paxosInstance,command);
    for(int i=0;i<servers.size();i++){
      Thread t=new Thread(new MessageSendThread(am,servers.get(i)));
      t.start();
    }
  }
}
