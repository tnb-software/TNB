package software.tnb.product.customercase.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("software.tnb.product.customercase.processor.CustomerCase")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class CustomerCaseProcessor extends AbstractProcessor {

    private static final Set<CustomerCaseTest> CUSTOMER_CASES = new HashSet<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(CustomerCase.class);
        for (Element element : annotated) {
            if (element.getKind().isClass()) {
                // In case of annotation on a class, it would be nice to get all the annotated Test methods of the class
//                List<String> methods = new ArrayList<>();
//                try {
//                    for (Method method : Class.forName(element.toString()).getDeclaredMethods()) {
//                        if (method.getAnnotations().length > 0)
//                            methods.add(method.getName());
//                    }
//
//                } catch (ClassNotFoundException e) {
//                    throw new RuntimeException(e);
//                }

                CUSTOMER_CASES.add(new CustomerCaseTest(
                    List.of(element.getAnnotationsByType(CustomerCase.class)[0].jiraIds()),
                    element.toString(),
                    new ArrayList<>()));
            } else {
                // It's a method
                CUSTOMER_CASES.add(new CustomerCaseTest(
                    List.of(element.getAnnotationsByType(CustomerCase.class)[0].jiraIds()),
                    element.getEnclosingElement().toString(),
                    List.of(element.getSimpleName().toString())));
            }
        }

        try {
            File customerCaseFile = new File("CustomerCase.yaml");
            String yaml = new ObjectMapper(new YAMLFactory()).writeValueAsString(CUSTOMER_CASES);
            if (!customerCaseFile.exists() || !Files.readString(customerCaseFile.toPath()).contains(yaml)) {
                Files.writeString(customerCaseFile.toPath(), yaml, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}
