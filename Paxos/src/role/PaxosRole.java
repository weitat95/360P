package role;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import message.Message;

public class PaxosRole {
  int paxosInstance;
  ArrayList<String> servers;
  int myID;
  public PaxosRole(Integer myID,Integer paxInstance,ArrayList<String> servers){
    assert(paxInstance!=null &&myID!=null&& servers!=null);
    this.paxosInstance=paxInstance;
    this.servers=servers;
    this.myID=myID;
  }
 
  public void sendMessage(Message m){
    
  }
}
