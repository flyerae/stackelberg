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
import java.lang.Math;

final class Leader extends PlayerImpl
{
  private List<Record> data = new ArrayList<Record>();
  private Payoff payoff = null;

  private int windowSize = 60;

  private Leader() throws RemoteException, NotBoundException {
    super(PlayerType.LEADER, "Leader");
  }

  private final static int MAX_WINDOW_SIZE = 55;

  @Override
  public void startSimulation(final int p_steps) throws RemoteException {

      m_platformStub.log(this.m_type, "Day 1: " + profit(1.776, 1.807));
      m_platformStub.log(this.m_type, "Day 2: " + profit(1.858, 1.762));
      m_platformStub.log(this.m_type, "Day 3: " + profit(1.769, 1.770));
      m_platformStub.log(this.m_type, "Day 4: " + profit(1.879, 1.786));

      /*
      float[] error = new float[MAX_WINDOW_SIZE];
      readData("data/Mk1.csv");

      for (windowSize = 1; windowSize < MAX_WINDOW_SIZE; ++windowSize) {
        for (int day = windowSize; day < 60; ++day) {
          findReactionFunction(day);
          float price = payoff.globalMaximum();
          Record currentDay = data.get(day);
          error[windowSize] += (profit(currentDay.m_leaderPrice, currentDay.m_followerPrice)
                              - profit(price, currentDay.m_followerPrice)); // Should this be m_followerPrice?
                                                                            // If we submit price, their response would not be the same as followerPrice.
                                                                            // Maybe we should send what we expect their price to be given our estimate of R(Ul)?
        }
      }

        for (int size = 1; size < MAX_WINDOW_SIZE; ++size) {
          m_platformStub.log(this.m_type, "WS: " + size + " error: " + error[size] + " num: " + (60 - size) + " AVG: " + (error[size] / (60 - size)));
        }
        */

          //TODO: choose window size -> minimum error.
          //TODO: Don't user error[], just have a minimumError float.
      findReactionFunction(61);
      System.out.println("a: " + this.payoff.a + ", " + this.payoff.b);
    }

  @Override
    public void endSimulation() throws RemoteException {
      double profit = 0;
      for (Record record : this.data) {
        profit += this.profit((double)record.m_leaderPrice, (double)record.m_followerPrice);
      }
      m_platformStub.log(this.m_type, "We are rich: " + String.valueOf(profit));
    }

  @Override
    public void proceedNewDay(int p_date) throws RemoteException {
      System.exit(1);
      m_platformStub.log(this.m_type, "NEW DAY");
      // updateReactionFunction();
      findReactionFunction(p_date-1);

      m_platformStub.log(this.m_type, "PUB: " + payoff.globalMaximum());
      m_platformStub.publishPrice(m_type, payoff.globalMaximum());
      Record r = m_platformStub.query(this.m_type, p_date-1);
      data.add(r);


    }

  public static void main(final String[] p_args) throws RemoteException, NotBoundException {
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

  private void findReactionFunction(int endDate) throws RemoteException {
    // set a and b from data
    // according to lecture 4 slide 20
    float sum_x = 0;
    float sum_y = 0;
    float sum_x_squared = 0;
    float sum_x_y = 0;

    for (int date = endDate - windowSize; date < endDate; ++date) {
      Record day = m_platformStub.query(this.m_type, date);
      System.out.println(date);

      sum_x += day.m_leaderPrice;
      sum_y += day.m_followerPrice;
      sum_x_squared += Math.pow(day.m_followerPrice, 2);
      sum_x_y += day.m_leaderPrice * day.m_followerPrice;
    }

    double a = ((sum_x_squared * sum_y) - (sum_x * sum_x_y)) / (sum_x_squared - Math.pow(sum_x, 2));
    double b = (sum_x_y - (sum_x * sum_y)) / (sum_x_squared - Math.pow(sum_x, 2));

    this.payoff = new Payoff(a, b);
  }

  private double profit(double leaderPrice, double followerPrice) {
    return (leaderPrice - 1.00) * ((2.0 - leaderPrice) + (0.3 * followerPrice));
  }

  /*
     private double error(int start, int end) {
     for (int day = start; day < end; ++day) {
     Record record = data.get(day);
     }
     }

     private void updateReactionFunction() {
// online learning
}
   */

}
