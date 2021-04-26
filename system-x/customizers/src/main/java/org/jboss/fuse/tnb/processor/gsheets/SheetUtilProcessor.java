package org.jboss.fuse.tnb.processor.gsheets;

import org.jboss.fuse.tnb.processor.Processor;

import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateSpreadsheetPropertiesRequest;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeSpec;

import java.util.Collections;
import java.util.List;

public class SheetUtilProcessor {
    private SheetUtilProcessor() {
    }

    public static TypeSpec setNewSpreadsheetBody(String title) {

        CodeBlock.Builder builder = CodeBlock.builder();

        newInstance(builder, "spreadsheet", Spreadsheet.class);
        newInstance(builder, "props", SpreadsheetProperties.class);

        builder.addStatement("props.setTitle($S)", title)
            .addStatement("spreadsheet.setProperties(props)")
            .addStatement("exchange.getOut().setBody(spreadsheet)");

        return Processor.create(builder.build());
    }

    public static TypeSpec setUpdateSpreadsheetHeader(String title) {
        CodeBlock.Builder builder = CodeBlock.builder();

        newInstance(builder, "batchUpdate", BatchUpdateSpreadsheetRequest.class);
        newInstance(builder, "request", Request.class);
        newInstance(builder, "updatePropsRequest", UpdateSpreadsheetPropertiesRequest.class);
        newInstance(builder, "props", SpreadsheetProperties.class);

        builder.addStatement("props.setTitle($S)", title)
            .addStatement("updatePropsRequest.setProperties(props)")
            .addStatement("updatePropsRequest.setFields(\"title\")")
            .addStatement("request.setUpdateSpreadsheetProperties(updatePropsRequest)")
            .addStatement("$T<Request> requests = $T.singletonList(request)", List.class, Collections.class)
            .addStatement("batchUpdate.setIncludeSpreadsheetInResponse(true)")
            .addStatement("batchUpdate.setRequests(requests)")

            .addStatement("exchange.getIn().setHeader(\"CamelGoogleSheets.batchUpdateSpreadsheetRequest\", batchUpdate)");

        return Processor.create(
            builder.build()
        );
    }

    private static CodeBlock.Builder newInstance(CodeBlock.Builder builder, String name, Class c) {
        return builder.addStatement(String.format("$T %s = new $T()", name), ClassName.get(c), ClassName.get(c));
    }
}
