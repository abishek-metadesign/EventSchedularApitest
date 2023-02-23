package uk.co.metadesignsolutions.javachallenge.external;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import de.cronn.testutils.h2.H2Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.metadesignsolutions.javachallenge.enums.TimePeriod;
import uk.co.metadesignsolutions.javachallenge.models.Event;
import uk.co.metadesignsolutions.javachallenge.repositories.EventRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@Import(H2Util.class)
public class BaseEventControllerExternalTest {

    final static String SCHEDULE_URL="/schedule/";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected H2Util h2Util;



    @Autowired
    protected EventRepository eventRepository;


    protected void validateResponseAsPerSchema(String json, String schemaUrl) throws IOException, ProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJsonNode = objectMapper.readTree(json);

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTrace[1];
        String packageName = stackTraceElement.getClassName();
        int lastDotIndex = packageName.lastIndexOf('.');
        if (lastDotIndex >= 0) {
            packageName = packageName.substring(0, lastDotIndex);
        }
        packageName= packageName.replaceAll("\\.","/");
        schemaUrl="src/test/java/"+packageName+schemaUrl;
        JsonNode schemaJsonNode = JsonLoader.fromPath(schemaUrl);
        JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.byDefault();
        JsonSchema jsonSchema = jsonSchemaFactory.getJsonSchema(schemaJsonNode);
        ProcessingReport validate = jsonSchema.validate(responseJsonNode);
        if (!validate.isSuccess()){
            throw  new RuntimeException();
        }
    }

    protected Event getEvent(){
        Event event = new Event();
        event.setTitle(" meeting");
        event.setStartDate(LocalDate.parse("2020/10/20"));
        event.setEndDate(LocalDate.parse("2020/10/21"));
        event.setPriority(2.5);
        event.setStartTime("9:00");
        event.setEndTime("10:00");
        event.setTimePeriod(TimePeriod.ONE_HOUR);
        event.setScheduledDate(LocalDate.now());
        return event;
    }
    protected String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static Map<String,Object> getEventRequestMap(){
        Map<String,Object> event = new HashMap<>();
        event.put("title","meeting");
        event.put("startDate","2021/10/20");
        event.put("endDate","2021/10/21");
        event.put("priority",2.5);
        event.put("timePeriod","one_hour");
        return event;
    }


}
