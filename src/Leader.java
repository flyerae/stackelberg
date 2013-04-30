package comp34120.ex2;

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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

final class Leader extends PlayerImpl
{
  private List<Record> data = new ArrayList<Record>();
  private Payoff payoff = null;
  
  private static final int WINDOW_SIZE = 60;

  //private double totalProfit = 0.0;

  private Leader() throws RemoteException, NotBoundException
  {
    super(PlayerType.LEADER, "Leader");
  }

  @Override
  public void startSimulation(final int p_steps) {
    readData("data/Mk1.csv");
    //findReactionFunction();
  }

	@Override
	public void endSimulation() throws RemoteException {
    double profit = 0;
    for (Record record : this.data) {
      profit += this.payoff((double)record.m_leaderPrice, (double)record.m_followerPrice);
    }
    m_platformStub.log(this.m_type, "We are rich: " + String.valueOf(profit));
  }

  @Override
  public void proceedNewDay(int p_date) throws RemoteException
  {
    // updateReactionFunction();
    findReactionFunction(p_date-1);

    m_platformStub.publishPrice(m_type, payoff.globalMaximum());
    Record r = m_platformStub.query(this.m_type, p_date-1);
    data.add(r);
      
    //this.totalProfit += this.payoff((double) r.m_leaderPrice, (double) r.m_followerPrice);
//    m_platformStub.log(this.m_type,  String.valueOf(this.payoff((double)r.m_leaderPrice, (double)r.m_followerPrice)));

  }

  public static void main(final String[] p_args) throws RemoteException, NotBoundException
  {
    new Leader();
  }

  private void readData(String fileName) {
    BufferedReader input = null;

    try {
      input = new BufferedReader(new FileReader(fileName));
      input.readLine(); // skip headers

      String line;
      String[] features;
      Record record;

      while ((line = input.readLine()) != null) {
        features = line.split(",");
        record = new Record(Integer.parseInt(features[0]),
                            Float.parseFloat(features[1]),
                            Float.parseFloat(features[2]),
                            Float.parseFloat(features[3]));
        data.add(record);
      }
    } catch (IOException e) {
      System.err.println("Error occurred reading file " + input + ": " + e.getMessage());
    } finally {
      try {
        input.close();
      } catch (IOException e) {
        System.err.println("Failed to close file " + input + ": " + e.getMessage());
      }
    }
  }

  private void findReactionFunction(int offset) {
    // set a and b from data
    // according to lecture 4 slide 20
    float sum_x = 0;
    float sum_y = 0;
    float sum_x_squared = 0;
    float sum_x_y = 0;

    for (int i = offset; i <= offset + WINDOW_SIZE; ++i) {
      sum_x += data[i].m_leaderPrice;
      sum_y += data[i].m_followerPrice;
      sum_x_squared += Math.pow(data[i].m_followerPrice, 2);
      sum_x_y += data[i].m_leaderPrice * data[i].m_followerPrice;
    }

    double a = ((sum_x_squared * sum_y) - (sum_x * sum_x_y)) / (sum_x_squared - Math.pow(sum_x, 2));
    double b = (sum_x_y - (sum_x * sum_y)) / (sum_x_squared - Math.pow(sum_x, 2));

    this.payoff = new Payoff(a, b);
  }
    
  /*
  private float payoff(float leaderPrice, float followerPrice) {
    return (leaderPrice - 1.0) * demand(leaderPrice, followerPrice);
  }

  private float demand(float leaderPrice, float followerPrice) {
    return 2.0 - leaderPrice + 0.3 * followerPrice;
  }
  */

  private double payoff(double leaderPrice, double followerPrice) {
    return (leaderPrice - 1.00) * ((2.0 - leaderPrice) + (0.3 * followerPrice));
  }

  private void updateReactionFunction() {
    // online learning
  }

}
