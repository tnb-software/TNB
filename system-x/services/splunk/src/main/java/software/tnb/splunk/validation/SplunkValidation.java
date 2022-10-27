package software.tnb.splunk.validation;

import software.tnb.common.utils.WaitUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.splunk.IndexCollection;
import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.ResultsReaderXml;
import com.splunk.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public List<String> getAllMessages(String indexName) {
        List<String> results = new ArrayList<>();

        String searchQuery = String.format("search index=\"%s\"", indexName.toLowerCase());
        JobArgs jobargs = new JobArgs();
        jobargs.setExecutionMode(JobArgs.ExecutionMode.BLOCKING);
        Job job = client.getJobs().create(searchQuery, jobargs);
        WaitUtils.waitFor(job::isDone, "Waiting till Splunk search job is done");
        try (InputStream resultsNormalSearch = job.getResults()) {
            ResultsReaderXml events = new ResultsReaderXml(resultsNormalSearch);
            HashMap<String, String> event;
            while ((event = events.getNextEvent()) != null) {
                results.add(event.get("_raw")); // we are interested in only _raw data
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return results;
    }
}
