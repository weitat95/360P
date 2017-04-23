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

            }else if(m instanceof PromiseAgreeMessage){
              PromiseAgreeMessage pam=(PromiseAgreeMessage) m;
              System.out.println(pam.toString());
              //Continue here
              //debug promiseagreemessage not sending
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
