package software.tnb.splunk.validation;

import software.tnb.common.utils.WaitUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.splunk.Event;
import com.splunk.IndexCollection;
import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.ResultsReaderXml;
import com.splunk.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SplunkValidation {

    private static final Logger LOG = LoggerFactory.getLogger(SplunkValidation.class);

    private IndexCollection indexCollection = null;
    private final Service client;

    public SplunkValidation(Service client) {
        this.client = client;
        this.indexCollection = client.getIndexes();
    }

    public void createIndex(String name) {
        String indexNameLc = name.toLowerCase();
        LOG.debug("Creating Splunk index {}", indexNameLc);
        indexCollection.create(indexNameLc);
        WaitUtils.waitFor(() -> client.getIndexes().containsKey(indexNameLc), "Waiting until Splunk index is created.");
    }

    public void deleteIndex(String name) {
        String indexNameLc = name.toLowerCase();
        LOG.debug("Deleting Splunk index {}", indexNameLc);
        indexCollection.remove(indexNameLc);
        WaitUtils.waitFor(() -> !client.getIndexes().containsKey(indexNameLc), "Waiting until Splunk index is deleted.");
    }

    public void addDataToIndex(String indexName, String data) {
        indexCollection.get(indexName.toLowerCase()).submit(data);
    }

    public List<String> getAllMessagesFromIndex(String indexName) {
        return getAllEvents(String.format("search index=\"%s\"", indexName.toLowerCase()))
            .stream().map(event -> event.get("_raw")).collect(Collectors.toList());
    }

    public List<Event> getAllEvents(String query) {
        List<Event> results = new ArrayList<>();
        JobArgs jobargs = new JobArgs();
        jobargs.setExecutionMode(JobArgs.ExecutionMode.BLOCKING);
        Job job = client.getJobs().create(query, jobargs);
        WaitUtils.waitFor(job::isDone, "Waiting till Splunk search job is done");
        try (InputStream resultsNormalSearch = job.getResults()) {
            ResultsReaderXml events = new ResultsReaderXml(resultsNormalSearch);
            Event event;
            while ((event = events.getNextEvent()) != null) {
                results.add(event);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return results;
    }
}
