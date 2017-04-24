package message;

public class AcceptAcceptMessage extends Message{
  String command;
  public AcceptAcceptMessage(String c){
    super(c);
  }
  public AcceptAcceptMessage(){
    super();
  }
  public AcceptAcceptMessage(Integer myID,Integer seq,Integer instance,String command){
    super(myID,seq,instance);
    assert(command!=null);
    this.command=command;
  }
  public String toString(){
    return "[i="+instance+"]Accept Accepted Message from ID: " +myID+" seq: "+seq+" command: "+command;
  }
  public String getCommand(){
    return command;
  }
}
