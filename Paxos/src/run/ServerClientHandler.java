package run;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import message.Message;
import message.RedirectMessage;
import role.Proposer;


public class ServerClientHandler implements Runnable{
  Socket s;
  Scanner cin;
  PrintWriter pout;
  Server server;
  public ServerClientHandler(Socket s,Server server){
    this.s=s;
    this.server=server;
    try {
      cin=new Scanner(s.getInputStream());
      pout=new PrintWriter(s.getOutputStream());

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  static String executeCommand(String command,Store store){
    Scanner st = new Scanner(command);
    String tag = st.next();
    if (tag.equals("search")) {
    String userName = st.next();
    return store.searchOrder(userName);

    } else if (tag.equals("purchase")) {
      String userName = st.next();
      String productName = st.next();
      int quantityWanted = st.nextInt();
      return store.purchase(userName, productName, quantityWanted);

    
    } else if (tag.equals("cancel")) {
      // cancel order with <order-ID>
      int orderID = st.nextInt();
      return store.cancelOrder(orderID);

    
    } else if (tag.equals("list")) {
      // show <product-name> <quantity>
      return store.listProduct();
    }
    return null;
  }
  public void run() {
    while(cin.hasNextLine()){
      String command = cin.nextLine();
      System.out.println("Server received: "+command);
      //Check if you are a leader (for now id smallest be the leader)
      if(server.myID==1){
        server.commands.add(server.myID+":"+command);
        server.commandsProcessing=true;
        int instance=server.instanceNum.addAndGet(1);
        int seq=server.sequenceNum.incrementAndGet();
        System.out.println("[DEBUG]: Starting proposal with instance:" +instance+" seq Num: "+seq);
        Proposer proposer=new Proposer(server.myID,instance,seq,server.servers);
        proposer.startProposal();
        
        
      }else{
        server.commandsProcessing=true;
        System.out.println("[DEBUG]: Redirecting command to leader");
        Thread t=new Thread(new MessageSendThread(new RedirectMessage(server.myID+":"+command),server.servers.get(0)));
        t.start();
      }
      try{
        server.rl.lock();
        while(server.commandsProcessing){
          server.paxosFinishExecuting.await();
        }
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }finally{
        server.rl.unlock();
      }
      //Synchronization for the server responding to client
      //Need to prevent executing twice
      pout.println("ab");
      pout.println(executeCommand(command,server.store));
      pout.println("|ENDMSG|");
      pout.flush();

    }
  }
}