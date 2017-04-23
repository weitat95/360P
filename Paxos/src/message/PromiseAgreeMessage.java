package message;

public class PromiseAgreeMessage extends Message{
  String command;
  public PromiseAgreeMessage(String c) {
    super(c);
    // TODO Auto-generated constructor stub
  }
  public PromiseAgreeMessage() {
    super();
    // TODO Auto-generated constructor stub
  }
  public PromiseAgreeMessage(Integer myID,Integer seq,Integer instance,String command){
    super(myID,seq,instance);
    this.command=command;
  }
  public String toString(){
    return "[i="+instance+"]Promise Agree Message from ID: "+myID+" seq: "+seq+" command: "+command;
  }

}
