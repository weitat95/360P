package run;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import message.*;


public class MessageSendThread implements Runnable{
  private Socket s;
  private Message m;
  private InetSocketAddress inet;
  Server server;
  String serverIP;
  public MessageSendThread(Message m,String serverIP,Server serv){
    assert(s!=null&&m!=null);
    this.serverIP=serverIP;
    Scanner token = new Scanner(serverIP);
    token.useDelimiter(":");
    inet=new InetSocketAddress(token.next(),token.nextInt());
    assert(inet!=null);
    token.close();
    this.m=m;
    this.server=serv;
  }
  @Override
  public void run() {
      Scanner sin;
      PrintStream pout;
      ObjectOutputStream oos;
      s=new Socket();
      try {
        //System.out.println("Connecting: "+inet.getPort()+" "+inet.getHostString());
        s.connect(inet,100);
        sin = new Scanner(s.getInputStream());
        //pout = new PrintStream(s.getOutputStream());
        oos = new ObjectOutputStream(s.getOutputStream());
        oos.writeObject(m);
        oos.flush();
          
        //System.out.println("Sent Message: "+m.toString());
        if(sin.nextLine().equals("Acknowledge")){
          s.close();
          //System.out.println("Received Acknowledgement");
        }  
      } catch (SocketTimeoutException e){
        //index=myID-1
        int idOFCrashedNode=server.servers.indexOf(serverIP);
        server.crashedServer[idOFCrashedNode]=true;
        //System.out.println("$$DEBUG: server (ID: "+(idOFCrashedNode+1)+") Crashed");
        if(idOFCrashedNode+1==server.leaderID){
          if(server.leaderID+1==server.myID){
        
            //Leader crashed and Im the backup leader
            System.out.println("[$DEBUG]: I'm the next backup Leader");
            LeaderMessage lm=new LeaderMessage(server.myID);
            server.leaderID=server.myID;
              for(int i=0;i<server.numServer;i++){
                if(i!=server.myID-1){
                  Thread t2=new Thread(new MessageSendThread(lm,server.servers.get(i),server));
                  t2.start();
                }
              }
              server.instanceNum.set(server.instanceCommandMap.size());
              server.sequenceNum.set(server.receivedSeq.get());
              Thread t=new Thread(new MessageSendThread(new RedirectMessage(server.tempCommand),server.servers.get(server.leaderID-1),server));
              t.start();
          }else{
            System.out.println("[$DEBUG]: I'm not the next backupLeader");
            Thread t=new Thread(new MessageSendThread(new RedirectMessage(server.tempCommand),server.servers.get(server.leaderID),server));
            t.start();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      } 
    
  }
  
  
}
