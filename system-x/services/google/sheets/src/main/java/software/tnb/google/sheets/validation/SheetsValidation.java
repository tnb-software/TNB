package software.tnb.google.sheets.validation;

import software.tnb.google.sheets.account.GoogleAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;

import java.io.IOException;

public class SheetsValidation {
    private static final Logger LOG = LoggerFactory.getLogger(SheetsValidation.class);

    private final Sheets client;
    private final GoogleAccount account;

    public SheetsValidation(Sheets client, GoogleAccount account) {
        this.client = client;
        this.account = account;
    }

    public Spreadsheet createNewSpreadsheet(String title) {
        Spreadsheet spreadsheet = new Spreadsheet()
            .setProperties(new SpreadsheetProperties()
                .setTitle(title));
        try {
            spreadsheet = client.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Can't create new spreadsheet", e);
        }
        LOG.debug("Created new spreadsheet with ID: " + spreadsheet.getSpreadsheetId());
        return spreadsheet;
    }

    public Spreadsheet getSpreadsheet(String id) {
        try {
            return client.spreadsheets().get(id).execute();
        } catch (IOException e) {
            throw new RuntimeException("Can't get spreadsheet with ID:" + id, e);
        }
    }
}
