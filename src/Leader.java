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
  private Payoff payoff = null;

  private int windowSize = -1;
  private final static int MAX_WINDOW_SIZE = 55;

  private Leader() throws RemoteException, NotBoundException {
    super(PlayerType.LEADER, "Leader");
  }

  @Override
  public void startSimulation(final int p_steps) throws RemoteException {
    double minimumError = Double.POSITIVE_INFINITY;
    int optimalSize = Int.NEGATIVE_INFINITY;

    for (windowSize = 1; windowSize <= MAX_WINDOW_SIZE; ++windowSize) {
      for (int day = windowSize + 1; day <= 60; ++day) {
        findReactionFunction(day);
        double price = payoff.globalMaximum();
        Record currentDay = m_platformStub.query(this.m_type, day);
        if (
        (profit(currentDay.m_leaderPrice, currentDay.m_followerPrice) - profit(price, currentDay.m_followerPrice));
      }
    }
    windowSize = optimalSize;

    for (int size = 1; size < MAX_WINDOW_SIZE; ++size) {
    m_platformStub.log(this.m_type, "WS: " + size + " error: " + error[size] + " num: " + (60 - size) + " AVG: " + (error[size] / (60 - size)));
    }
  }

  @Override
  public void endSimulation() throws RemoteException {
    //TODO: Think about.
  }

  @Override
  public void proceedNewDay(int p_date) throws RemoteException {
    findReactionFunction(p_date);
    m_platformStub.publishPrice(m_type, payoff.globalMaximum());
  }

  private void findReactionFunction(int endDate) throws RemoteException {
    double sumXSquared = 0;
    double sumY = 0;
    double sumX = 0;
    double sumXsumY = 0;

    for (int date = endDate - windowSize; date < endDate; ++date) {
      Record day = m_platformStub.query(this.m_type, date);
      sumX += day.m_leaderPrice;
      sumY += day.m_followerPrice;
      sumXSquared += Math.pow(day.m_leaderPrice, 2);
      sumXsumY += day.m_leaderPrice * day.m_followerPrice;
    }

    int T = endDate - 1;
    double a = (sumXSquared * sumY - sumX * sumXsumY)  / (T * sumXSquared - Math.pow(sumX, 2));
    double b = (T * sumXsumY - sumX * sumY) / (T * sumXSquared - Math.pow(sumX, 2));

    this.payoff = new Payoff(a, b);
  }

  private double profit(double leaderPrice, double followerPrice) {
    return (leaderPrice - 1.00) * ((2.0 - leaderPrice) + (0.3 * followerPrice));
  }

  public static void main(final String[] p_args) throws RemoteException, NotBoundException {
    new Leader();
  }
}
