package run;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import message.Message;

public class Client {
  int numServer;
  String[] ServerIp;
  int[] ServerPort;
  Scanner din;
  PrintStream pout;
  Socket server;
  int connectedServerID;
  ObjectOutputStream oos;

  public Client(int numServer,String[] serverIp,int[] serverPort){
    this.numServer=numServer;
    this.ServerIp=serverIp;
    this.ServerPort=serverPort;
  }
  
  public void connectServer(){
    for(int i=0;i<numServer;i++){
      server = new Socket();
      try {
//        System.out.println("[DEBUG]: Initiating Connection to server "+(i+1));
        server.connect(new InetSocketAddress(ServerIp[i],ServerPort[i]),100);
        din = new Scanner(server.getInputStream());
        oos = new ObjectOutputStream(server.getOutputStream());
        Message m=new Message("connect");
        oos.writeObject(m);
        oos.flush();
        server.setSoTimeout(100);
        if(din.nextLine().equals("Acknowledge")){
          System.out.println("[DEBUG]: Connected to Server "+(i+1));
          connectedServerID=i;
          break;
        }
        
        
      }catch (SocketTimeoutException ste){
//        System.out.println("[DEBUG] TIMEOUT Connecting Server: "+(i+1));
        if(i==numServer-1){
          connectServer(-1);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  public void connectServer(int lastConnected){
    for(int i=lastConnected+1;i<numServer;i++){
      server = new Socket();
      try {
//        System.out.println("[DEBUG]: Initiating Connection to server "+(i+1));
        server.connect(new InetSocketAddress(ServerIp[i],ServerPort[i]),100);
        din = new Scanner(server.getInputStream());
        oos = new ObjectOutputStream(server.getOutputStream());
        Message m=new Message("connect");
        oos.writeObject(m);
        oos.flush();
        //pout.println("connect");
        //pout.flush();
        server.setSoTimeout(100);
        if(din.nextLine().equals("Acknowledge")){
//          System.out.println("[DEBUG]: Connected to Server "+(i+1));
          connectedServerID=i;
          break;
        }
        
        
      }catch (SocketTimeoutException ste){
//        System.out.println("[DEBUG] TIMEOUT Connecting Server: "+(i+1));
        if(i==numServer-1){
          connectServer(-1);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  public void sendRequest(String command){
    try {
      pout = new PrintStream(server.getOutputStream());
    } catch (IOException e2) {
      // TODO Auto-generated catch block
      e2.printStackTrace();
    }

    pout.println(command);
    pout.flush();
    String msg;
    try {
      server.setSoTimeout(100);
      server.getInputStream().read();
      server.setSoTimeout(0);
      server.getInputStream().read();
      while(!(msg=din.nextLine()).equals("|ENDMSG|")){
        System.out.println(msg); // prints server's message
      }
    } catch(SocketTimeoutException ste ){
//      System.out.println("[DEBUG] TIMEOUT sending Request to Server");

    }catch (IOException e) {
      // TODO Auto-generated catch block
      try {
        server.close();
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      connectServer(connectedServerID);
      sendRequest(command);
    }
   
  }
    
    
  
	public static void main (String[] args) {
	  //Scanner sc = new Scanner(System.in);
	  
	  //REMOVE FOR SUBMISSION
	  Scanner sc = null;
    try {
      sc = new Scanner(new FileReader("Paxos/client.cfg"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    //END
      
     
		int numServer = sc.nextInt();
		String[] ServerIp=new String[numServer];
		int[] ServerPort=new int[numServer];
		for (int i = 0; i < numServer; i++) {
		  String str = sc.next(); 
      Scanner token = new Scanner(str);
      token.useDelimiter(":");
      ServerIp[i]=token.next();
      ServerPort[i]=Integer.parseInt(token.next());
      token.close();
      System.out.println("address for server " + i + ": " + str);
		}
		Client client=new Client(numServer,ServerIp,ServerPort);
		
		
		client.connectServer();
    sc = new Scanner(System.in);

		while(sc.hasNextLine()) {
		  String cmd = sc.nextLine();
		  String[] tokens = cmd.split(" ");
//		  System.out.println("[DEBUGG]"+cmd);
      if (tokens[0].equals("purchase")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
        String userName = tokens[1];
        String productName = tokens[2];
        int quantity = Integer.parseInt(tokens[3]);
        String toSend="purchase " + userName + " " + productName + " " + quantity;
        client.sendRequest(toSend);
      } else if (tokens[0].equals("cancel")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
        int orderID = Integer.parseInt(tokens[1]);
        String toSend="cancel " + orderID;
        client.sendRequest(toSend);
      } else if (tokens[0].equals("search")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
        String userName = tokens[1];
        String toSend="search " + userName;
        client.sendRequest(toSend);
        
      } else if (tokens[0].equals("list")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
        String toSend="list";
        client.sendRequest(toSend);
        
      } else {
        System.out.println("ERROR: No such command");
      }
    }
  }
}