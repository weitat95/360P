package run;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import message.Message;

public class Server {
  static ArrayList<String> servers = new ArrayList<String>();
  public static void main (String[] args) {
    
    Scanner sc = null;
    try {
      sc = new Scanner(new FileReader("Paxos/server3.cfg"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    int myID = sc.nextInt();
    int numServer = sc.nextInt();
    String inventoryPath = sc.next();
    System.out.println("[DEBUG] my id: " + myID);
    System.out.println("[DEBUG] numServer: " + numServer);
    System.out.println("[DEBUG] inventory path: " + inventoryPath);
    for (int i = 0; i < numServer; i++) {
      String str = sc.next(); //change back to sc once done
      servers.add(str);
      System.out.println("address for server " + i + ": " + servers.get(i));
    }
    BlockingQueue<Message> receiveBuffer=new LinkedBlockingDeque<Message>();
    BlockingQueue<Message> sendBuffer=new LinkedBlockingDeque<Message>();
    Thread t=new Thread(new MessageReceiveThread(servers.get(0),receiveBuffer));
    t.start();

    Thread t2=new Thread(new MessageSendThread(sendBuffer,servers.get(0)));
    t2.start();
    while (true) {
      //if(!q.take()){
      Message m=new Message("a");
      sendBuffer.add(m);
      sendBuffer.add(m);
      sendBuffer.add(m);
        try {
          System.out.println("Received Content: "+receiveBuffer.take().getContent());
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      //}
    }
    // TODO: start server socket to communicate with clients and other servers
    
    // TODO: parse the inventory file

    // TODO: handle request from client
  }
}
