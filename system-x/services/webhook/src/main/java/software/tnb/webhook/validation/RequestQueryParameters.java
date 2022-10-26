package software.tnb.webhook.validation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Class to hold the Webhook request query parameters
 */
public class RequestQueryParameters {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private StringBuilder parameters;

    public RequestQueryParameters setSorting(QuerySorting sorting) {
        appendParameter("sorting", sorting.value);
        return this;
    }

    public RequestQueryParameters setRequestsPerPage(int requests) {
        appendParameter("per_page", String.valueOf(requests));
        return this;
    }

    public RequestQueryParameters setPage(int page) {
        appendParameter("page", String.valueOf(page));
        return this;
    }

    public RequestQueryParameters setDateFrom(LocalDateTime dateFrom) {
        appendParameter("date_from", DATE_FORMATTER.format(dateFrom));
        return this;
    }

    public RequestQueryParameters setDateTo(LocalDateTime dateFrom) {
        appendParameter("date_to", DATE_FORMATTER.format(dateFrom));
        return this;
    }

    public RequestQueryParameters setQuery(String query) {
        appendParameter("query", query);
        return this;
    }

    private void appendParameter(String name, String value) {
        if (parameters == null) {
            parameters = new StringBuilder("?");
        } else {
            parameters.append("&");
        }
        parameters.append(name);
        parameters.append("=");
        parameters.append(value);
    }

    @Override
    public String toString() {
        return parameters.toString();
    }

    public enum QuerySorting {
        OLDEST("oldest"),
        NEWEST("newest");

        private String value;

        QuerySorting(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
