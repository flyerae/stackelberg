import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;

final class Leader extends PlayerImpl
{
  private List<Record> data = new ArrayList<Record>();
  private float a, b;

  private Leader() throws RemoteException, NotBoundException
  {
    super(PlayerType.LEADER, "Leader");
  }

  @Override
    public void startSimulation(final int p_steps) {
      readData();
      findReactionFunction();
    }

  @Override
    public void proceedNewDay(int p_date) throws RemoteException
    {
      // data.add(m_platformSub.query(m_type, p_date?));
      // updateReactionFunction();

      float price = fredsFunction();
      m_platformStub.publishPrice(m_type, price);
    }

  public static void main(final String[] p_args) throws RemoteException, NotBoundException
  {
    new Leader();
  }

  private void readData() {
    // read CSV into data
  }

  private void findReactionFunction() {
    // set a and b from data
    // according to lecture 4 slide 20
  }

  private void updateReactionFunction() {
    // online learning
  }

  private float fredsFunction() {
    return 1.0f;
  }
}
