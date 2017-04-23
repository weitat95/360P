package role;

import java.util.ArrayList;

public class Learner extends PaxosRole{
  public Learner(Integer myID,Integer paxInstance,Integer seq,ArrayList<String> servers){
    super(myID,paxInstance,servers);
  }
}
