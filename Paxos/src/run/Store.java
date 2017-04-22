package run;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Store {
	HashMap<String,Integer> inventory=new HashMap<String,Integer>();
	ArrayList<Order> orderList = new ArrayList<Order>();
	int orderID = 0;
	public Store(String inputFileName){
		try {
			Scanner in = new Scanner(new FileReader(inputFileName)); // CHANGE DIR WHEN SUBMITTING!!!
			while(in.hasNext()) {
				inventory.put(in.next(), in.nextInt());
			}
			in.close();
		} 
		catch (FileNotFoundException e) {
			System.err.println ("Error: File not found. Exiting...");
			e.printStackTrace();
			System.exit(-1);
		} 
	}
	public synchronized String searchOrder(String userName){
		int numberOrdered=0;
		String ret="";
		for(Order o: orderList) {
			if (o.userName.equals(userName)) {
				ret+=o.orderID + " " + o.productName + " " + o.quantity+"\n";	
				numberOrdered++;
			}
		}
		if(numberOrdered>0){
			return ret;
		}else{
			return "No order found for " + userName;
		}
	}
	public synchronized String purchase(String userName,String productName,int quantity){
		if(quantity>0 && inventory.containsKey(productName) && inventory.get(productName)>=quantity){
			orderID = orderID + 1;
			inventory.put(productName,inventory.get(productName)-quantity);
			orderList.add(new Order(orderID, userName, productName, quantity));
			return "Your order has been placed, " + orderID + " " + userName + " " + 
			productName + " " + quantity;
		} else if (inventory.containsKey(productName) == false){
			return "Not Available - We do not sell this product";
		} else {
			return "Not Available - Not enough items";
		}
	}
	public synchronized String cancelOrder(int orderID){
		if(orderList.size() < 1) {		
			return orderID + " not found, no such order";
		}
		for(Order o: orderList){
			if(o.orderID==orderID){
				inventory.put(o.productName,inventory.get(o.productName)+o.quantity);
				orderList.remove(o);
				return "Order " + orderID + " is canceled";
			}
		}
		return orderID+ " not found, no such order";
	}
	public synchronized String listProduct(){
		String ret="";
		for(String ent:inventory.keySet()){
			ret+=ent+" "+inventory.get(ent)+"\n";
		}

		return ret;
	}
	public synchronized String executeCommand(String command){
	  Scanner st = new Scanner(command);
    String tag = st.next();
    if (tag.equals("search")) {
    String userName = st.next();
    return searchOrder(userName);

    } else if (tag.equals("purchase")) {
      String userName = st.next();
      String productName = st.next();
      int quantityWanted = st.nextInt();
      return purchase(userName, productName, quantityWanted);

    
    } else if (tag.equals("cancel")) {
      // cancel order with <order-ID>
      int orderID = st.nextInt();
      return cancelOrder(orderID);

    
    } else if (tag.equals("list")) {
      // show <product-name> <quantity>
      return listProduct();
    }
    return null;
	}
}	