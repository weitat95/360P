package run;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

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
  
    while (true) {
      break;
    }
    // TODO: start server socket to communicate with clients and other servers
    
    // TODO: parse the inventory file

    // TODO: handle request from client
  }
}
