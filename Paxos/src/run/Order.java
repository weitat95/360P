package run;
// For creating Order objects by the server
public class Order {
	int orderID, quantity;
	String userName, productName;
	public Order(int orderID, String userName, String productName, int quantity)
	{
		this.orderID = orderID;
		this.userName = userName;
		this.productName = productName;
		this.quantity = quantity;
	}
}
