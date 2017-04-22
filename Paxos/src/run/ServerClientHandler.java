package run;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class ServerClientHandler implements Runnable{
  Socket s;
  Scanner cin;
  PrintWriter pout;
  Store store;
  Server server;
  public ServerClientHandler(Socket s,Server server){
    this.s=s;
    this.server=server;
    this.store=server.store;
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
      pout.println("A");
      pout.flush();
//     
      pout.println(executeCommand(command,server.store));
      pout.println("|ENDMSG|");
      pout.flush();
    }
  }
}