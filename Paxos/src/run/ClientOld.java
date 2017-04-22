package run;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientOld {
	static ArrayList<String> servers = new ArrayList<String>();
  public static void main (String[] args) {
	Socket server;
    Scanner sc = new Scanner(System.in);
    int numServer = sc.nextInt();
    
    for (int i = 0; i < numServer; i++) {
      // TODO: parse inputs to get the ips and ports of servers
    	String str = sc.next();
    	System.out.println("address for server " + i + ": " + str);
    	servers.add(str);
    }
    
    while(sc.hasNextLine()) {
      String cmd = sc.nextLine();
      System.out.println(cmd);
      String[] tokens = cmd.split(" ");
      String messageresponse = "";
      if (tokens[0].equals("purchase") || tokens[0].equals("cancel") || tokens[0].equals("search") || tokens[0].equals("list")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
    	  boolean serviced = false;
    	  while(!serviced){ //****** If server times out, execution returns to this while loop after catch block is finished executing, right?
        	  server = obtainSocket();
              //try{
            	//  server.setSoTimeout(100);//set read timeout to 100ms
              //}catch(SocketException e){
            	//  System.err.println(e);
              //}
	    	  try{
		    	  Scanner din = new Scanner (server.getInputStream());
				  PrintStream pout = new PrintStream(server.getOutputStream());
				  pout.println(cmd);
				  pout.flush();
				  //System.out.println("waiting for response");
				  while(din.hasNextLine()){
					  String response;
					  if((response = din.nextLine()).equals("end")){
						  serviced = true;
						  server.close();
						  System.out.println(messageresponse);
						  break;
					  }
					  if(response.equals("received")){
						  //System.out.println(response);
					  }
					  if(!response.equals("")){
						  if(!messageresponse.contains("received")){
							  messageresponse = messageresponse + "\n" + response;
						  }else{
							  messageresponse = response;
						  }
					  }
					  //System.out.println(response);
				  }
				  
	    	  }catch(IOException e){//server has crashed/timed out, connect to new server and service request
	    		  System.err.println(e);
	    		  server = obtainSocket();
	    	  }
    	  }
      } else {
        System.out.println("ERROR: No such command");
      }
    }
  }
  
  public static Socket obtainSocket(){
	  int index = 0;
	  while(true){//if all servers crash, this loop is continuous. Per instructions, client loops through addresses until connection is established
		  Socket server = new Socket();
		  String[] serverComponents = servers.get(index).split(":");
		  SocketAddress sockaddr = new InetSocketAddress(serverComponents[0], Integer.parseInt(serverComponents[1]));
		  try{
			  server.connect(sockaddr, 100);
			  //System.out.println("obtained connection to server " + (index+1));
			  return server;
		  }catch(IOException e){ // maybe just need to catch SocketTimeoutException
			  index = ((index + 1) % servers.size());
			  //System.out.println("connection failed. cycling");
		  }
	  }
  }
}