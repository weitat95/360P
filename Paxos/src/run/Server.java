package run;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import message.LeaderMessage;
import message.Message;
import message.RequestCatchupMessage;

public class Server {
  //String[] ServerIp;
  //int[] ServerPort;
  int myID;
  int numServer;
  Integer leaderID;
  ArrayList<String> servers;
  Store store;
  AtomicInteger instanceNum;
  AtomicInteger sequenceNum;
  AtomicInteger receivedSeq;
  String command;
  //Integer promiseCounter;
  //Integer acceptedCounter;
  Boolean[] crashedServer; 
  HashMap<Integer,String> instanceCommandMap;
  HashMap<Integer,Integer> promiseCounter;
  HashMap<Integer,Integer> acceptedCounter;
  LinkedList<String> commands;
  final ReentrantLock rl=new ReentrantLock();
  final Condition paxosFinishExecuting = rl.newCondition();
  boolean commandsProcessing;
  public Server(int myID,int numServer,String inventoryPath,ArrayList<String> servers){
    this.myID=myID;
    this.numServer=numServer;
    this.leaderID=1;
    this.store=new Store(inventoryPath);
    //this.ServerIp=ServerIp;
    //this.ServerPort=ServerPort;
    this.servers=servers;
    this.instanceNum=new AtomicInteger();
    this.sequenceNum=new AtomicInteger();
    this.receivedSeq=new AtomicInteger();
    this.promiseCounter=new HashMap<Integer,Integer>();
    this.acceptedCounter=new HashMap<Integer,Integer>();
    this.instanceCommandMap=new HashMap<Integer,String>();
    this.commands=new LinkedList<String>();
    this.commandsProcessing=false;
    this.crashedServer=new Boolean[numServer];
    for(int i=0;i<numServer;i++){
      crashedServer[i]=false;
    }
  }
  /*
  public synchronized int incrementPromise(Integer instanceNum){
    promiseCounter++;
    return promiseCounter;
  }
  public synchronized int incrementAccepted(Integer instanceNum){
    acceptedCounter++;
    return acceptedCounter;
  }
  */
  public static void main (String[] args) {

    Scanner sc = null;
    try {
      sc = new Scanner(new FileReader("Paxos/server3.cfg"));
      //sc = new Scanner(new FileReader(args[0]));

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    int myID = sc.nextInt();
    int numServer = sc.nextInt();
    //String[] ServerIp=new String[numServer];
    //int[] ServerPort=new int[numServer];
    ArrayList<String> servers=new ArrayList<String>();
    String inventoryPath = sc.next();
    System.out.println("[DEBUG] my id: " + myID);
    System.out.println("[DEBUG] numServer: " + numServer);
    System.out.println("[DEBUG] inventory path: " + inventoryPath);
    for (int i = 0; i < numServer; i++) {
      String str = sc.next(); //change back to sc once done
      servers.add(str);
      //Scanner token = new Scanner(str);
      //token.useDelimiter(":");
      //ServerIp[i]=token.next();
      //ServerPort[i]=Integer.parseInt(token.next());
      //token.close();
      System.out.println("address for server " + i + ": " + str);
    }
    Server server=new Server(myID,numServer,inventoryPath,servers);
    Thread t=new Thread(new MessageReceiveThread(servers.get(myID-1),server));
    t.start();
    
    try {
      Scanner scf = new Scanner(new FileReader(myID+"_LastProposalLog.txt"));
      //sc = new Scanner(new FileReader(args[0]));
      System.out.println("[DEBUG]: read previous proposal number");
      String read=scf.nextLine();
      server.sequenceNum.set((Integer.parseInt(read)-server.myID)/server.numServer);
      scf.close();
    } catch (FileNotFoundException e) {
      System.out.println("[DEBUG]: No previous proposal");
    }
    try {
      Scanner scff = new Scanner(new FileReader(myID+"_instComm.txt"));
      //sc = new Scanner(new FileReader(args[0]));
      System.out.println("[DEBUG]: read previous learned value");
      
      while(true){
        String read=scff.nextLine();
        String[] tokens=read.split("#");
        server.instanceCommandMap.put(Integer.parseInt(tokens[1]), tokens[2]);
        String[] tokens2=tokens[2].split(":");
        server.store.executeCommand(tokens2[1]);
      }
        
      
      //server.sequenceNum.set((Integer.parseInt(read)-server.myID)/server.numServer);
    } catch (FileNotFoundException e) {
      System.out.println("[DEBUG]: No previous learned value");
    } catch (NoSuchElementException e){
      System.out.println("[DEBUG]: Read until last value");
      //Ask for leader to catch up
      RequestCatchupMessage rcm=new RequestCatchupMessage(server.myID,server.instanceCommandMap.size());
      Thread t3=new Thread(new MessageSendThread(rcm,server.servers.get(server.leaderID-1)));
      t3.start();
    }
    
    if(server.myID==server.leaderID){
      LeaderMessage lm=new LeaderMessage(server.myID);
      for(int i=0;i<server.numServer;i++){
        if(i!=server.myID-1){
          Thread t2=new Thread(new MessageSendThread(lm,servers.get(i)));
          t2.start();
        }
      }
    }
    /*
    ServerSocket listener;
    Socket s=null;
    try{
      listener=new ServerSocket(ServerPort[myID-1]);
      while(true){

        while((s=listener.accept())!=null){
          Scanner scin=new Scanner(s.getInputStream());
          PrintWriter pout=new PrintWriter(s.getOutputStream());
          String receivedMsg=scin.nextLine();
          String[] tags=receivedMsg.split(" ");
          if(tags[0].equals("connect")){
            pout.println("Server Ready");
            pout.flush();
            Thread clientH = new Thread(new ServerClientHandler(s,server));
            clientH.start();
          }else{

          }
        }
      }
    }catch (IOException e){
      e.printStackTrace();
    }


    //Smallest Server ID be the leader, Starting at 1;

    //Leader starts connection to other server;
    BlockingQueue<Message> receiveBuffer=new LinkedBlockingDeque<Message>();
    ArrayList<BlockingQueue<Message>> sendBuffers=new ArrayList<BlockingQueue<Message>>();
    if(myID==1){
      for(int i=0;i<numServer-1;i++){
        sendBuffers.add(new LinkedBlockingDeque<Message>());
        Thread t= new Thread(new MessageSendThread(sendBuffers.get(i),servers.get(i+1)));
        t.start();
      }
      Thread t=new Thread(new MessageReceiveThread(servers.get(0),receiveBuffer));
      t.start();
    }else{

    }

    Thread t2=new Thread(new MessageSendThread(sendBuffer,servers.get(0)));
    t2.start();
    BlockingQueue<Message> sendBuffer2=new LinkedBlockingDeque<Message>();

    Thread t3=new Thread(new MessageSendThread(sendBuffer2,servers.get(0)));
    t3.start();
    while (true) {
      //if(!q.take()){
      Message m=new Message("a");
      sendBuffer.add(m);
      sendBuffer2.add(new Message("b"));
        try {
          System.out.println("Received Content: "+receiveBuffer.take().getContent());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      //}
    }
    */
    // TODO: start server socket to communicate with clients and other servers

    // TODO: parse the inventory file

    // TODO: handle request from client
  }
}
