package comp34120.ex2;
class Payoff {

  double a, b;

  public Payoff(double a, double b) {
    this.a = a;
    this.b = b;
  }

  public float globalMaximum() {
    return (float) ((-2.7 - 0.3 * this.a) / (0.8 * this.b - 2.0));
  }
}
