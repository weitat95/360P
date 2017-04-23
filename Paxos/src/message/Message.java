package message;

public class Message implements java.io.Serializable{
  private String content="example";
  int myID;
  int seq;
  int instance;
  public Message(String c){
    content=c;
  }
  public Message(){
    content="HelloWorld";
  }
  public Message(Integer myID,Integer seq,Integer instance){
    this.myID=myID;
    this.seq=seq;
    this.instance=instance;
  }
  public String getContent(){
    return content;
  }
}
