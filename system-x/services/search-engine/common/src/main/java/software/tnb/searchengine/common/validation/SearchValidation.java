package software.tnb.searchengine.common.validation;

import software.tnb.common.validation.Validation;

import java.util.List;

public interface SearchValidation extends Validation {

    void createIndex(String index);

    void deleteIndex(String index);

    <T> Object getData(String index, Class<T> documentClass);

    Object insert(String index, Object content);

    List<String> getIndices();

    boolean indexExists(String index);
}
