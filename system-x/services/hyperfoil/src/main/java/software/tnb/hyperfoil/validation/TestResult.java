package software.tnb.hyperfoil.validation;

import software.tnb.hyperfoil.validation.generated.model.RequestStatisticsResponse;
import software.tnb.hyperfoil.validation.generated.model.Run;

public class TestResult {

    private Run run;
    private RequestStatisticsResponse totalStats;

    public TestResult(Run run, RequestStatisticsResponse totalStats) {
        super();
        this.run = run;
        this.totalStats = totalStats;
    }

    public Run getRun() {
        return run;
    }

    public RequestStatisticsResponse getTotalStats() {
        return totalStats;
    }
}
