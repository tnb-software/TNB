package software.tnb.hyperfoil.validation;

public class TestRun {

  private String started;
  private String terminated;
	
  public TestRun(String started, String terminated) {
    this.started = started;
    this.terminated = terminated;
  }

  public String getStarted() {
    return started;
  }

  public String getTerminated() {
    return terminated;
  }

}
