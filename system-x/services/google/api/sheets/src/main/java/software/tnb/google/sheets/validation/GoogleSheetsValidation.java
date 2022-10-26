package software.tnb.google.sheets.validation;

import software.tnb.common.service.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;

import java.io.IOException;

public class GoogleSheetsValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleSheetsValidation.class);

    private final Sheets client;

    public GoogleSheetsValidation(Sheets client) {
        this.client = client;
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
