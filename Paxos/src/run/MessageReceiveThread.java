package run;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import role.Acceptor;
import role.Proposer;
import message.AcceptAcceptMessage;
import message.AcceptMessage;
import message.CatchupMessage;
import message.LeaderMessage;
import message.Message;
import message.PromiseAgreeMessage;
import message.ProposeMessage;
import message.RedirectMessage;
import message.RequestCatchupMessage;

public class MessageReceiveThread implements Runnable{
  Integer port;

  Server server;
  public MessageReceiveThread(String serverIPPort,Server server){
    assert(serverIPPort!=null&&server!=null);
    this.server=server;
    Scanner token = new Scanner(serverIPPort);
    token.useDelimiter(":");
    token.next();
    port=token.nextInt();
    token.close();
    assert(port!=null);
  }
  @Override
  public void run() {
    ServerSocket listernerSocket;
    ObjectInputStream ois;
    PrintWriter pout;
      try {
        listernerSocket = new ServerSocket(port);
        Socket s = null;
        while(true){

          while((s=listernerSocket.accept())!=null){
            
            ois = new ObjectInputStream(s.getInputStream());
            pout = new PrintWriter(s.getOutputStream());
            Message m=(Message) ois.readObject();
            String message=m.getContent();
            //System.out.println("Received message: "+message);
            
            String[] tags=message.split(" ");
            if(tags[0].equals("connect")){
              //System.out.println("Client connected!");
              Thread clientH=new Thread(new ServerClientHandler(s,server));
              clientH.start();
            }else {
              
              if(m instanceof ProposeMessage){
                ProposeMessage pm=(ProposeMessage) m;
                System.out.println("[RECEIVED]:"+pm.toString());
                int seq=pm.getSeq();
                String commandd;
                if(server.instanceCommandMap.containsKey(pm.getInstance())){
                  commandd=server.instanceCommandMap.get(pm.getInstance());
                }else{
                  commandd=null;
                }
                Acceptor acceptor=new Acceptor(server.myID, pm.getInstance(),seq, server.servers,server.receivedSeq,commandd);
                acceptor.setLeader(server.leaderID);
                acceptor.setServer(server);
                acceptor.startRole();
              }else if(m instanceof PromiseAgreeMessage){
                PromiseAgreeMessage pam=(PromiseAgreeMessage) m;
                System.out.println("[RECEIVED]:"+pam.toString());
                //if(server.instanceNum.get()==pam.getInstance()){
                int promiseCounter=incrementPromiseGet(pam.getInstance());
                //System.out.println("###DEBUG###: PromiseCounter:"+ promiseCounter);
                  //if(server.incrementPromise(pam.getInstance())==server.numServer/2  /*+1*/){
                  if(promiseCounter==server.numServer/2){
                    System.out.println("[DEBUG]: Reaches quorum for promises,proceed to phase2");
                    Proposer p=new Proposer(server.myID,pam.getInstance(),pam.getSeq(),server.servers);
                    p.setServer(server);
                    String command=server.commands.removeFirst();
                    p.sendAccepts(command);
                  }
                //}
              }else if(m instanceof AcceptMessage){
                AcceptMessage am=(AcceptMessage) m;
                System.out.println("[RECEIVED]:"+am.toString());
                String command=am.getCommand();
                int instance=am.getInstance();
                int seq=am.getSeq();
                //Sending accepts to leader
                Acceptor acceptor=new Acceptor(server.myID,instance,seq,server.servers,command);
                acceptor.setLeader(server.leaderID);
                acceptor.setServer(server);
                acceptor.startAcceptingPhase();
                // This part should be in the learner
                
                if(server.instanceCommandMap.get(instance)==null){
                 
                  server.instanceCommandMap.put(instance, command);
                  System.out.println("Learn command: "+command+" instance: "+instance);
                  
                  executeCommand(command);
  
                }else if(server.instanceCommandMap.get(instance)==command){
                  System.out.println("do nothing");
                }else{
                  System.out.println("Server inconsistency?");
                }
                
              }else if(m instanceof AcceptAcceptMessage){
                AcceptAcceptMessage aam=(AcceptAcceptMessage)m;
                System.out.println("[RECEIVED]:"+aam.toString());
                int instance=aam.getInstance();
                String command=aam.getCommand();
                int acceptedCount=incrementAcceptedGet(aam.getInstance());
                //System.out.println("###DEBUG###: AcceptedCounter:"+ acceptedCount);
  
                if(acceptedCount==server.numServer/2  /*+1*/){
                  if(server.instanceCommandMap.get(instance)==null){
                    
                    server.instanceCommandMap.put(instance, command);
                    System.out.println("[DEBUG]: Learn command: "+command+" instance: "+instance);
                    executeCommand(command);
                  }else if(server.instanceCommandMap.get(instance).equals(command)){
                    System.out.println("[DEBUG]: do nothing");
                  }else{
                    System.out.println("[DEBUG]: Server inconsistency?");
                  }           
                }
              }else if(m instanceof RedirectMessage){
                
                RedirectMessage rm=(RedirectMessage)m;
                System.out.println("[RECEIVED]:"+rm.toString());
                if(server.myID!=server.leaderID){
                  //Im the backup leader!
                  server.leaderID=server.myID;
                  
                  LeaderMessage lm=new LeaderMessage(server.myID);
                  for(int i=0;i<server.numServer;i++){
                    if(i!=server.myID-1){
                      Thread t2=new Thread(new MessageSendThread(lm,server.servers.get(i),server));
                      t2.start();
                    }
                  }
                  server.instanceNum.set(server.instanceCommandMap.size());
                  server.sequenceNum.set(server.receivedSeq.get());
                }
                
                int instance=server.instanceNum.addAndGet(1);
                int seq=server.sequenceNum.incrementAndGet()*server.numServer+server.myID;
                System.out.println("[DEBUG]: Starting proposal with instance:" +instance+" seq Num: "+seq);
                server.commands.add(rm.getContent());
                Proposer proposer=new Proposer(server.myID,instance,seq,server.servers);
                proposer.setServer(server);
                proposer.startProposal();
                
              }else if(m instanceof LeaderMessage){
                LeaderMessage lm=(LeaderMessage) m;
                server.crashedServer[lm.getID()-1]=false;
                System.out.println("[RECEIVED]:"+lm.toString());
                server.leaderID=lm.getID();
              }else if(m instanceof RequestCatchupMessage){
                RequestCatchupMessage rcm=(RequestCatchupMessage)m;
                System.out.println("[RECEIVED]:"+rcm.toString());
                if(server.instanceCommandMap.size()!=rcm.getInstance()){
                  int dif=server.instanceCommandMap.size()-rcm.getInstance();
                  String str="";
                  for(int i=0;i<dif;i++){
                    str=str+(i+1+rcm.getInstance())+"#"+server.instanceCommandMap.get(i+1+rcm.getInstance())+"\n";
                  }
                  CatchupMessage cm=new CatchupMessage(str);
                  Thread tt=new Thread(new MessageSendThread(cm,server.servers.get(rcm.getID()-1),server));
                  tt.start();
                }
              }else if(m instanceof CatchupMessage){
                CatchupMessage cm=(CatchupMessage) m;
                System.out.println("[RECEIVED]:"+cm.toString());
                Scanner scf=new Scanner(cm.getContent());
                try{
                  while(true){
                    String scanned=scf.nextLine();
                    //System.out.println("[###]: "+scanned);
                    String[] tokens=scanned.split("#");
                    server.instanceCommandMap.put(Integer.parseInt(tokens[0]),tokens[1]);
                    String[] tokens2=tokens[1].split(":");
                    server.store.executeCommand(tokens2[1]);
                  }
                  
                }catch(NoSuchElementException e){
                  try{
                    server.instanceNum.set(server.instanceCommandMap.size());
                    PrintWriter writer =new PrintWriter(server.myID+"_instComm.txt","UTF-8");
                    for(int i=0;i<server.instanceCommandMap.size();i++){
                      writer.println("instance#"+(i+1)+"#"+server.instanceCommandMap.get(i+1));
                    }
                    writer.close();
                  }catch (IOException e2){
                    e2.printStackTrace();
                  }
                }
                
              }
            }
            pout.println("Acknowledge");
            pout.flush();
            
            //s.close();
          }
        }
      } 
      catch (IOException | ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  public void executeCommand(String command){

    try{
      PrintWriter writer =new PrintWriter(server.myID+"_instComm.txt","UTF-8");
      for(int i=0;i<server.instanceCommandMap.size();i++){
        writer.println("instance#"+(i+1)+"#"+server.instanceCommandMap.get(i+1));
      }
      writer.close();
    }catch (IOException e){
      e.printStackTrace();
    }
    
    String[] tokens=command.split(":");
    if(Integer.parseInt(tokens[0])!=server.myID){
      System.out.println("[DEBUG]: command executed");
      server.store.executeCommand(tokens[1]);
    }else{
      server.commandsProcessing=false;
      try{
        server.rl.lock();

        server.paxosFinishExecuting.signalAll();
      }finally{
        server.rl.unlock();
      }
      
    }
  }
  
  public synchronized int incrementPromiseGet(int instance){
    if(server.promiseCounter.containsKey(instance)){
      server.promiseCounter.put(instance, server.promiseCounter.get(instance)+1);
    }else{
      server.promiseCounter.put(instance,1);
    }
    return server.promiseCounter.get(instance);
  }
  public synchronized int incrementAcceptedGet(int instance){
    if(server.acceptedCounter.containsKey(instance)){
      server.acceptedCounter.put(instance, server.acceptedCounter.get(instance)+1);
    }else{
      server.acceptedCounter.put(instance,1);
    }
    return server.acceptedCounter.get(instance);
  }
  
}
