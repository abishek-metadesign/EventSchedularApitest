package uk.co.metadesignsolutions.javachallenge.external;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.co.metadesignsolutions.javachallenge.external.testlogger.Position;
import uk.co.metadesignsolutions.javachallenge.external.testlogger.TestPrinter;
import uk.co.metadesignsolutions.javachallenge.models.Event;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class EventControllerListingExternalTest extends BaseEventControllerExternalTest {

    private static TestPrinter testPrinter;

    private  static String listSchema = "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"type\":\"array\",\"items\":[{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\"},\"title\":{\"type\":\"string\"},\"startDate\":{\"type\":\"string\"},\"endDate\":{\"type\":\"string\"},\"startTime\":{\"type\":\"string\"},\"endTime\":{\"type\":\"string\"},\"timePeriod\":{\"type\":\"string\"},\"scheduledDate\":{\"type\":\"string\"}},\"required\":[\"id\",\"title\",\"startDate\",\"endDate\",\"startTime\",\"endTime\",\"timePeriod\",\"scheduledDate\"]}]}";

    @BeforeAll
    public static void setup(){
        testPrinter = new TestPrinter();
    }

    @Test
    public void shouldReturn200(){
        testPrinter.print(()->{
            try {
                mockMvc.perform(
                        get(SCHEDULE_URL+"all")
                ).andExpect(MockMvcResultMatchers.status().isOk());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },23, Position.JUNIOR);
    }
    @Test
    public void shouldReturnScheduleList(){
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Event event = getEvent();
            event.setTitle(" current meeting");
            eventRepository.save(event);
            try {
                mockMvc.perform(get(SCHEDULE_URL+"/all"))
                        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(" current meeting")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },23, Position.JUNIOR);
    }
    @Test
    public void  shouldReturnScheduleListInProperFormat(){
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Event event = getEvent();
            eventRepository.save(event);
            try {
                mockMvc.perform(get(SCHEDULE_URL+"all"))
                        .andExpect(mvcResult -> {
                            MockHttpServletResponse response = mvcResult.getResponse();
                            String contentAsString = response.getContentAsString();
                            validateResponseAsPerSchema(contentAsString,listSchema);
                        });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },23, Position.JUNIOR);
    }


}
