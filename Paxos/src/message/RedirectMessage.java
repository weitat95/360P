package message;

public class RedirectMessage extends Message {

  public RedirectMessage(String c) {
    super(c);
    // TODO Auto-generated constructor stub
  }

  public RedirectMessage() {
    // TODO Auto-generated constructor stub
  }

  public RedirectMessage(Integer myID, Integer seq, Integer instance) {
    super(myID, seq, instance);
    // TODO Auto-generated constructor stub
  }
  public String toString(){
    return "Redirected Message: "+getContent();
  }

}
