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
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;

final class Leader extends PlayerImpl
{
  private Payoff payoff = null;

  private int windowSize;
  private final static int MAX_WINDOW_SIZE = 49;

  private Leader() throws RemoteException, NotBoundException {
    super(PlayerType.LEADER, "Leader");
  }

  private double calculateError(Record actual) {
    double followerPrice = this.payoff.followerEstimate(actual.m_leaderPrice);
    return Math.abs(followerPrice - actual.m_followerPrice);
  }

  private void naiveWindowSize() throws RemoteException {
    double currentError, minimumError = Double.POSITIVE_INFINITY;
    int optimalSize = 0;

    for (windowSize = 1; windowSize <= MAX_WINDOW_SIZE; ++windowSize) {
      currentError = 0.0;
      for (int day = windowSize + 1; day <= 60; ++day) {
        findReactionFunction(day);
        currentError += calculateError(cache[day]);
      }
      currentError /= 60 - windowSize;

      if (currentError < minimumError) {
        minimumError = currentError;
        optimalSize = windowSize;
        System.out.println("New Minimum: " + optimalSize);
      }

      System.out.printf("Size: %2d Current: %.5f Best: %.5f\n", windowSize, currentError, minimumError);
    }
    windowSize = optimalSize;
  }

  private Record[] cache;

  // Rearranges the cache for cross-validation.
  private void rearrangeCache(int testingFold) {
    for (int date = 1; date <= foldSize(); ++date) {       // For each element in the fold.
      int testing = foldSize() * testingFold + date;       // Index of the element to move.
      int training = 60 - foldSize() + date;               // Index to move it to.
      swapCache(testing, training);                        // Swap the two elements.
    }
  }

  // Reapplying the rearrangement reverses it.
  private void resetCache(int testingFold) {
    rearrangeCache(testingFold);
  }

  private void swapCache(int a, int b) {
    Record temp = cache[a];
    cache[a] = cache[b];
    cache[b] = temp;
  }

  private final int NO_FOLDS = 5;
  private int foldSize() { return 60 / NO_FOLDS; }

  private void crossValidation() { 
    double[] error = new double[MAX_WINDOW_SIZE + 1];
    for (int testingFold = 0; testingFold < NO_FOLDS; ++testingFold) {
      rearrangeCache(testingFold);
      for (windowSize = 1; windowSize <= MAX_WINDOW_SIZE; ++windowSize) {
        for (int testing = 50; testing <= 60; ++testing) {
          findReactionFunction(testing);
          error[windowSize] += calculateError(cache[testing]);
        }
      }
      resetCache(testingFold);
    }

    int minimum = 1;
    for (int i = 2; i < error.length; ++i)
      if (error[i] < error[minimum])
        minimum = i;

    this.windowSize = minimum;
  }

  @Override
  public void startSimulation(final int p_steps) throws RemoteException {
    this.cache = new Record[60 + p_steps];
      
    for (int day = 1; day <= 60; ++day)
      this.cache[day] = m_platformStub.query(this.m_type, day);
      
    //naiveWindowSize();
    crossValidation();
  }

  @Override
  public void endSimulation() {
    //TODO: Think about.
  }

  @Override
  public void proceedNewDay(int p_date) throws RemoteException {
    findReactionFunction(p_date);
    m_platformStub.publishPrice(m_type, payoff.globalMaximum());
    this.cache[p_date] = m_platformStub.query(this.m_type, p_date);
  }

  private void findReactionFunction(int endDate) {
    double sumXSquared = 0;
    double sumY        = 0;
    double sumX        = 0;
    double sumXsumY    = 0;

    for (int date = endDate - windowSize; date < endDate; ++date) {
      Record day   = this.cache[date];
      sumX        += day.m_leaderPrice;
      sumY        += day.m_followerPrice;
      sumXSquared += Math.pow(day.m_leaderPrice, 2);
      sumXsumY    += day.m_leaderPrice * day.m_followerPrice;
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
