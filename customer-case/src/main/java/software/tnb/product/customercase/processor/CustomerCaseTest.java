package software.tnb.product.customercase.processor;

import java.util.List;

public record CustomerCaseTest(List<String> jiraIds, String implementedByClass, List<String> implementedByTests) {
}
