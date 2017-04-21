package message;

public class Message implements java.io.Serializable{
  private String content="example";
  public Message(String c){
    content=c;
  }
  public String getContent(){
    return content;
  }
}
