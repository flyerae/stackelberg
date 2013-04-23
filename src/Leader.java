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
  private double a, b;

  private Leader() throws RemoteException, NotBoundException
  {
    super(PlayerType.LEADER, "Leader");
  }

  @Override
  public void startSimulation(final int p_steps) {
    readData("data/Mk1.csv");
    findReactionFunction();
  }

  @Override
  public void proceedNewDay(int p_date) throws RemoteException
  {
    // data.add(m_platformSub.query(m_type, p_date?));
    // updateReactionFunction();

    float price = maximisePayoff();
    m_platformStub.publishPrice(m_type, price);
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

  private void findReactionFunction() {
    // set a and b from data
    // according to lecture 4 slide 20
    float sum_x = 0;
    float sum_y = 0;
    float sum_x_squared = 0;
    float sum_x_y = 0;

    for (Record record : data) {
      sum_x += record.m_leaderPrice;
      sum_y += record.m_followerPrice;
      sum_x_squared += Math.pow(record.m_followerPrice, 2);
      sum_x_y += record.m_leaderPrice * record.m_followerPrice;
    }

    a = ((sum_x_squared * sum_y) - (sum_x * sum_x_y)) / (sum_x_squared - Math.pow(sum_x, 2));
    b = (sum_x_y - (sum_x * sum_y)) / (sum_x_squared - Math.pow(sum_x, 2));
  }

  private void updateReactionFunction() {
    // online learning
  }

  private float maximisePayoff() {
    // iterate to find maximum
    return 1.0f;
  }
}
