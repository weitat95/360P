package message;

public class RequestCatchupMessage extends Message {

  public RequestCatchupMessage(String c) {
    super(c);
    // TODO Auto-generated constructor stub
  }

  public RequestCatchupMessage() {
    // TODO Auto-generated constructor stub
  }

  public RequestCatchupMessage(Integer myID, Integer seq, Integer instance) {
    super(myID, seq, instance);
    // TODO Auto-generated constructor stub
  }
  public RequestCatchupMessage(Integer myID,Integer instance){
    this.myID=myID;
    this.instance=instance;
  }
  public String toString(){
    return "Request Catchup from (ID: "+myID+") still on instance: "+instance;
  }
}
