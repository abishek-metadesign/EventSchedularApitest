package uk.co.metadesignsolutions.javachallenge.external;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.co.metadesgnsolutions.javachallenge.enums.TimePeriod;
import uk.co.metadesgnsolutions.javachallenge.external.testlogger.Position;
import uk.co.metadesgnsolutions.javachallenge.external.testlogger.TestPrinter;
import uk.co.metadesgnsolutions.javachallenge.models.Event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class EventControllerCreateExternalTest extends BaseEventControllerExternalTest {

    private static TestPrinter testPrinter;

    @BeforeAll
    public static void setup(){
      testPrinter = new TestPrinter(" Event Creation Test");
    }

    @Test
    public void shouldReturn2xx(){

        testPrinter.print(()->{
            Map<String, Object> eventMap = getEventRequestMap();
            String content = this.asJsonString(eventMap);
            try {
                mockMvc.perform(
                        post(SCHEDULE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        },10, Position.JUNIOR);

    }

    @Test
    public void shouldReturn201(){

        testPrinter.print(()->{
            Map<String, Object> eventMap = getEventRequestMap();
            String content = this.asJsonString(eventMap);
            try {
                mockMvc.perform(
                        post(SCHEDULE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(MockMvcResultMatchers.status().isCreated());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        },10, Position.JUNIOR);

    }

    @Test
    public void shouldReturnCreatedEventInProperFormat() throws Exception {
        testPrinter.print(()->{
            Map<String, Object> eventMap = getEventRequestMap();
            String content = this.asJsonString(eventMap);
            try {
                mockMvc.perform(
                        post(SCHEDULE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(mvcResult -> {
                    MockHttpServletResponse response = mvcResult.getResponse();
                    if (response.getStatus()==201 || response.getStatus()==200){
                        String contentAsString = response.getContentAsString();
                        validateResponseAsPerSchema(contentAsString, "/createEventResponseSchema.json");
                    }else{
                        throw  new RuntimeException("request failed");
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },10,Position.JUNIOR);

    }
    @Test
    public void shouldAddEventToDatabase(){
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Map<String, Object> eventMap = getEventRequestMap();
            String content = this.asJsonString(eventMap);
            try {
                mockMvc.perform(
                        post(SCHEDULE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            long count = eventRepository.count();
            Assertions.assertEquals(1,count,()->"Event should be added to api");
        },12,Position.JUNIOR);
    }

    @Test
    public void shouldReturn4xxForInvalidTimePeriod(){
        testPrinter.print(()->{
            Map<String, Object> eventMap = getEventRequestMap();
            eventMap.put("timePeriod", "fiteen_min");
            String content = this.asJsonString(eventMap);
            try {
                mockMvc.perform(
                        post(SCHEDULE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(MockMvcResultMatchers.status().is4xxClientError());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        },12,Position.JUNIOR);

    }
    @Test
    public void shouldReturn422ForInvalidTimePeriod(){
        testPrinter.print(()->{
            Map<String, Object> eventMap = getEventRequestMap();
            eventMap.put("timePeriod", "fiteen_min");
            String content = this.asJsonString(eventMap);
            try {
                mockMvc.perform(
                        post(SCHEDULE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },12,Position.JUNIOR);
    }


    @Test
    public void shouldReturnCorrectErrorWhenTheTimePeriodIsInCorrect(){

        testPrinter.print(()->{
            Map<String, Object> eventMap = getEventRequestMap();
            eventMap.put("timePeriod", "fiteen_min");
            String content = this.asJsonString(eventMap);
            try {
                mockMvc.perform(
                        post(SCHEDULE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsStringIgnoringCase("timePeriod")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },12,Position.JUNIOR);
    }


    @Test
    public void shouldReturn4xxForInvalidStartDate(){

        testPrinter.print(()->{
            testPrinter.print(()->{
                Map<String, Object> eventMap = getEventRequestMap();
                eventMap.put("startDate", "ad");
                String content = this.asJsonString(eventMap);
                try {
                    mockMvc.perform(
                            post(SCHEDULE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content)
                    ).andExpect(MockMvcResultMatchers.status().is4xxClientError());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            },12,Position.JUNIOR);

        },12,Position.JUNIOR);
    }
    @Test
    public void shouldReturn422ForInvalidStartDate(){

        testPrinter.print(()->{
            Map<String, Object> eventMap = getEventRequestMap();
            eventMap.put("startDate", "ad");
            String content = this.asJsonString(eventMap);
            try {
                mockMvc.perform(
                        post(SCHEDULE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        },12,Position.JUNIOR);
    }


    @Test
    public void shouldReturnCorrectErrorWhenTheStartDateIsInCorrect(){

        testPrinter.print(()->{
            Map<String, Object> eventMap = getEventRequestMap();
            eventMap.put("startDate", "ad");
            String content = this.asJsonString(eventMap);
            try {
                mockMvc.perform(
                                post(SCHEDULE_URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(content)
                        ).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("startDate")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },12,Position.JUNIOR);

    }


    @Test
    public void shouldReturn4xxForTryingToScheduleOnSaturdayOrSunday(){
        testPrinter.print(()->{
            testPrinter.print(()->{
                Map<String, Object> eventMap = getEventRequestMap();
                eventMap.put("startDate", "2023/03/25");
                eventMap.put("endDate","2023/03/25");
                String content = this.asJsonString(eventMap);
                try {
                    mockMvc.perform(
                            post(SCHEDULE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content)
                    ).andExpect(MockMvcResultMatchers.status().is4xxClientError());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            },12,Position.JUNIOR);

        },12,Position.JUNIOR);
    }

    @Test
    public void shouldReturn422ForTryingToScheduleOnSaturdayOrMonday(){
        testPrinter.print(()->{
            Map<String, Object> eventMap = getEventRequestMap();
            eventMap.put("startDate", "2023/03/25");
            eventMap.put("endDate","2023/03/25");
            String content = this.asJsonString(eventMap);
            try {
                mockMvc.perform(
                        post(SCHEDULE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        },12,Position.JUNIOR);

    }


    @Test
    public void shouldDistributeTheEventsCorrectly(){
        testPrinter.print(()->{
            h2Util.resetDatabase();

            Map<String, Object> eventMap1 = getEventRequestMap();
            eventMap1.put("title","1work");
            eventMap1.put("startDate", "2023/03/27");
            eventMap1.put("endDate","2023/03/27");
            eventMap1.put("timePeriod","two_hour");
            String event1 = this.asJsonString(eventMap1);

            Map<String, Object> eventMap2 = getEventRequestMap();
            eventMap2.put("title","2work");
            eventMap2.put("startDate", "2023/03/27");
            eventMap2.put("endDate","2023/03/27");
            eventMap2.put("timePeriod","two_hour");
            String event2 = this.asJsonString(eventMap2);


            Map<String, Object> eventMap3 = getEventRequestMap();
            eventMap3.put("title","3work");
            eventMap3.put("startDate", "2023/03/27");
            eventMap3.put("endDate","2023/03/27");
            eventMap3.put("timePeriod","two_hour");
            String event3 = this.asJsonString(eventMap3);

            try {
                mockMvc.perform(
                        post(SCHEDULE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(event1));

                mockMvc.perform(
                        post(SCHEDULE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(event2));

                mockMvc.perform(
                        post(SCHEDULE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(event3));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            List<Event> event = eventRepository.findAll();
            Set<String> collect = event.stream()
                    .map(Event::getStartTime)
                    .map(LocalTime::toString)
                    .collect(Collectors.toSet());


            List<String> data = new ArrayList<>();
            data.add("09:00");
            data.add("11:00");
            data.add("13:00");
            Assertions.assertTrue(collect.containsAll(data));
        },12,Position.JUNIOR);
    }


    @Test
    public void shouldNotScheduleEventOutsideWorkHours(){
        testPrinter.print(()->{
            h2Util.resetDatabase();

            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

            Event event1 = getEvent();
            event1.setStartDate(LocalDate.parse("2023/03/27",format));
            event1.setStartDate(LocalDate.parse("2023/03/27",format));
            event1.setScheduledDate(LocalDate.parse("2023/03/27",format));
            event1.setTitle("old work1");
            event1.setStartTime(LocalTime.parse("09:00",timeFormat));
            event1.setEndTime(LocalTime.parse("13:00",timeFormat));
            event1.setTimePeriod(TimePeriod.FOUR_HOUR);
            Event savedEvent1 = eventRepository.save(event1);

            Event event2 = getEvent();
            event2.setStartDate(LocalDate.parse("2023/03/27",format));
            event2.setStartDate(LocalDate.parse("2023/03/27",format));
            event2.setScheduledDate(LocalDate.parse("2023/03/27",format));
            event2.setTitle("old work 1");
            event2.setStartTime(LocalTime.parse("13:00",timeFormat));
            event2.setEndTime(LocalTime.parse("17:00",timeFormat));
            event2.setTimePeriod(TimePeriod.THREE_HOUR);
            Event savedEvent2 = eventRepository.save(event2);

            Map<String, Object> eventMap = getEventRequestMap();
            eventMap.put("title","work");
            eventMap.put("startDate", "2023/03/27");
            eventMap.put("endDate","2023/03/27");
            eventMap.put("timePeriod","two_hour");
            String content = this.asJsonString(eventMap);
            try {
                mockMvc.perform(
                        post(SCHEDULE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(MockMvcResultMatchers.status().is4xxClientError());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        },12,Position.JUNIOR);

    }


}
