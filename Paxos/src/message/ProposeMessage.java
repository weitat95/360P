package message;

public class ProposeMessage extends Message{

  public ProposeMessage(String c) {
    super(c);
  }
  public ProposeMessage() {
    super();
  }
  public ProposeMessage(Integer myID,Integer seq,Integer instance){
    super(myID,seq,instance);
  }
  public String toString(){
    return "[i="+instance+"]Proposal Message from ID: "+myID+" seq:"+seq;
  }
  public int getSeq(){
    return seq;
  }
  public int getSenderID(){
    return myID;
  }
  public int getInstance(){
    return instance;
  }
}
