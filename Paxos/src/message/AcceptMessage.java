package message;

public class AcceptMessage extends Message{
  String command;
  public AcceptMessage(String c){
    super(c);
  }
  public AcceptMessage(){
    super();
  }
  public AcceptMessage(Integer myID,Integer seq,Integer instance,String command){
    super(myID,seq,instance);
    assert(command!=null);
    this.command=command;
  }
  public String toString(){
    return "[i="+instance+"]Accept Message from ID: " +myID+" seq: "+seq+" command: "+command;
  }
  public String getCommand(){
    return command;
  }
}
