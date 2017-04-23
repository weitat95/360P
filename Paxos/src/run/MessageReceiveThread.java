package run;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import role.Acceptor;
import role.Proposer;
import message.AcceptMessage;
import message.Message;
import message.PromiseAgreeMessage;
import message.ProposeMessage;

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
              System.out.println("Client connected!");
              Thread clientH=new Thread(new ServerClientHandler(s,server));
              clientH.start();
            }else if(m instanceof ProposeMessage){
              ProposeMessage pm=(ProposeMessage) m;
              System.out.println(pm.toString());
              int seq=pm.getSeq();
              Acceptor acceptor=new Acceptor(server.myID, pm.getInstance(),seq, server.servers,server.receivedSeq,server.command);
              acceptor.startRole();
            }else if(m instanceof PromiseAgreeMessage){
              PromiseAgreeMessage pam=(PromiseAgreeMessage) m;
              System.out.println(pam.toString());
              //if(server.instanceNum.get()==pam.getInstance()){
                if(server.incrementPromise(pam.getInstance())==server.numServer/2+1){
                  System.out.println("[DEBUG]: Reaches quorum for promises,proceed to phase2");
                  Proposer p=new Proposer(server.myID,pam.getInstance(),pam.getSeq(),server.servers);
                  p.sendAccepts(server.commands.removeFirst());
                }
              //}
            }else if(m instanceof AcceptMessage){
              AcceptMessage am=(AcceptMessage) m;
              System.out.println(am.toString());
              String command=am.getCommand();
              int instance=am.getInstance();
              int seq=am.getSeq();
              // This part should be in the learner
              
              if(server.instanceCommandMap.get(instance)==null){
                server.instanceCommandMap.put(instance, command);
                System.out.println("Accept command: "+command+" instance: "+instance);
                server.store.executeCommand(command);
                server.promiseCounter=0;
              }else if(server.instanceCommandMap.get(instance)==command){
                System.out.println("do nothing");
              }else{
                System.out.println("Server inconsistency?");
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
  
}
