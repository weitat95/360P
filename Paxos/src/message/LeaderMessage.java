package message;

public class LeaderMessage extends Message {

  public LeaderMessage(String c) {
    super(c);
    // TODO Auto-generated constructor stub
  }

  public LeaderMessage() {
    // TODO Auto-generated constructor stub
  }

  public LeaderMessage(Integer myID, Integer seq, Integer instance) {
    super(myID, seq, instance);
    // TODO Auto-generated constructor stub
  }
  public LeaderMessage(Integer myID){
    this.myID=myID;
  }
  public String toString(){
    return "I'm Leader (ID: "+myID+").";
  }
}
