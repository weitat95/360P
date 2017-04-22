package run;

import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ServerServerHandler implements Runnable{
  InetSocketAddress inet;

  public ServerServerHandler(String serverIpPort){
    assert(serverIpPort!=null);
    Scanner token = new Scanner(serverIpPort);
    token.useDelimiter(":");
    inet=new InetSocketAddress(token.next(),token.nextInt());
    assert(inet!=null);
    token.close();
  }

  @Override
  public void run() {
      Scanner sin;
      ObjectOutputStream oos;
      Socket s=new Socket();
  }
}
