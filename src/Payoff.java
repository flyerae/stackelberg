package comp34120.ex2;
class Payoff {

  double a, b;

  public Payoff(double a, double b) {
    this.a = a;
    this.b = b;
  }

  public float globalMaximum() {
    return (float) ((2.7 + 0.3 * this.a) / (2.0 - 0.6 * this.b));
  }
}
