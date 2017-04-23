package role;

import java.util.ArrayList;

import run.MessageSendThread;
import message.Message;
import message.ProposeMessage;

public class Proposer extends PaxosRole{
  int seq;
  public Proposer(Integer myID,Integer paxInstance,Integer seq,ArrayList<String> servers){
    super(myID,paxInstance,servers);
    this.seq=seq;
    this.startProposal();
  }

  
  public void startProposal(){
    System.out.println("Starting Prepare State");
    Message prepare=new ProposeMessage(myID,sequence,paxosInstance);
    for(int i=0;i<servers.size();i++){
      if(i!=myID-1){
        Thread t=new Thread(new MessageSendThread(prepare,servers.get(i)));
        t.start();
      }
    }
  }
}
