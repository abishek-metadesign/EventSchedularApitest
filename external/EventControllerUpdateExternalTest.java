package uk.co.metadesignsolutions.javachallenge.external;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.co.metadesignsolutions.javachallenge.enums.TimePeriod;
import uk.co.metadesignsolutions.javachallenge.external.testlogger.Position;
import uk.co.metadesignsolutions.javachallenge.external.testlogger.TestPrinter;
import uk.co.metadesignsolutions.javachallenge.models.Event;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class EventControllerUpdateExternalTest extends BaseEventControllerExternalTest {

    private static TestPrinter testPrinter;

    @BeforeAll
    public static void setup(){
        testPrinter = new TestPrinter();
    }

    @Test
    public void shouldReturn4xxIfEventDoesNotExist()  {

     testPrinter.print(()->{
         h2Util.resetDatabase();
         Map<String, Object> playerMap = getEventRequestMap();
         String content = this.asJsonString(playerMap);
         try {
             mockMvc.perform(
                     put(SCHEDULE_URL+"/"+1)
                             .contentType(MediaType.APPLICATION_JSON)
                             .content(content)
             ).andExpect(MockMvcResultMatchers.status().is4xxClientError());
         } catch (Exception e) {
             throw new RuntimeException(e);
         }

     },12, Position.JUNIOR);
    }

    @Test
    public void shouldReturn404IfEventDoesNotExist() throws Exception {
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Map<String, Object> playerMap = getEventRequestMap();
            String content = this.asJsonString(playerMap);
            try {
                mockMvc.perform(
                        put(SCHEDULE_URL+"/"+1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(MockMvcResultMatchers.status().isNotFound());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        },12,Position.JUNIOR);

    }

    @Test
    public void shouldReturn200onUpdate() throws Exception {
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Event event = getEvent();
            Event firstEvent = eventRepository.save(event);
            Map<String, Object> eventPlayerRequestMap = getEventRequestMap();
            String content = asJsonString(eventPlayerRequestMap);
            try {
                mockMvc.perform(
                        put(SCHEDULE_URL+"/"+firstEvent.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(MockMvcResultMatchers.status().isOk());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },12,Position.JUNIOR);
    }

    @Test
    public void shouldReturnCorrectFormatWhenUpdated() throws Exception {
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Event event = getEvent();
            Event firstEvent = eventRepository.save(event);
            Map<String, Object> eventRequestMap = getEventRequestMap();
            String content = asJsonString(eventRequestMap);
            try {
                mockMvc.perform(
                        put(SCHEDULE_URL+"/" + firstEvent.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(mvcResult -> {
                    MockHttpServletResponse response = mvcResult.getResponse();
                    String contentAsString = response.getContentAsString();
                    validateResponseAsPerSchema(contentAsString, "/createPlayerResponseSchema.json");
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },12,Position.JUNIOR);
    }

    @Test
    public void shouldRescheduleIfThePlayerIsUpdated(){
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Event event1 = getEvent();
            event1.setPriority(1.0);
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setScheduledDate(LocalDate.parse("2023/03/27"));
            event1.setTitle("old work1");
            event1.setStartTime("9:00");
            event1.setEndTime("13:00");
            event1.setTimePeriod(TimePeriod.FOUR_HOUR);
            Event savedEvent1 = eventRepository.save(event1);

            Event event2 = getEvent();
            event1.setPriority(1.0);
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setScheduledDate(LocalDate.parse("2023/03/27"));
            event1.setTitle("old work2");
            event1.setStartTime("9:00");
            event1.setEndTime("13:00");
            event1.setTimePeriod(TimePeriod.FOUR_HOUR);
            Event savedEvent2 = eventRepository.save(event1);

            Map<String, Object> eventRequestMap = getEventRequestMap();
            eventRequestMap.put("startDate","2023/03/27");
            eventRequestMap.put("endDate","2023/03/27");
            eventRequestMap.put("title","oldWork1");
            eventRequestMap.put("timePeriod","two_hour");
            eventRequestMap.put("priority",1.0);

            String content = asJsonString(eventRequestMap);

            try {
                mockMvc.perform(
                        put(SCHEDULE_URL+event1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Event updatedEvent1 = eventRepository.findById(event1.getId())
                    .orElseThrow(() -> new RuntimeException("event not found"));

            Assertions.assertEquals("11:00",updatedEvent1.getEndTime());


            Event updatedEvent2 = eventRepository.findById(event2.getId())
                    .orElseThrow(() -> new RuntimeException("event not found"));

            Assertions.assertEquals("13:00",event2.getStartTime());

        },12,Position.JUNIOR);
    }

    @Test
    public void shouldChangePositionIfPriorityIsUpdatedSufficiently(){
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Event event1 = getEvent();
            event1.setPriority(1.0);
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setScheduledDate(LocalDate.parse("2023/03/27"));
            event1.setTitle("old work1");
            event1.setStartTime("9:00");
            event1.setEndTime("13:00");
            event1.setTimePeriod(TimePeriod.FOUR_HOUR);
            Event savedEvent1 = eventRepository.save(event1);

            Event event2 = getEvent();
            event2.setPriority(1.0);
            event2.setStartDate(LocalDate.parse("2023/03/27"));
            event2.setStartDate(LocalDate.parse("2023/03/27"));
            event2.setScheduledDate(LocalDate.parse("2023/03/27"));
            event2.setTitle("old work1");
            event2.setStartTime("13:00");
            event2.setEndTime("16:00");
            event2.setTimePeriod(TimePeriod.THREE_HOUR);
            Event savedEvent2 = eventRepository.save(event2);

            Map<String, Object> eventRequestMap = getEventRequestMap();
            eventRequestMap.put("startDate","2023/03/27");
            eventRequestMap.put("endDate","2023/03/27");
            eventRequestMap.put("title","oldWork1");
            eventRequestMap.put("timePeriod","two_hour");
            eventRequestMap.put("priority",3.5);

            String content = asJsonString(eventRequestMap);

            try {
                mockMvc.perform(
                        put(SCHEDULE_URL+event2.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Event updatedEvent = eventRepository.findById(event2.getId()).orElseThrow(() -> new RuntimeException("event not found"));
            Assertions.assertEquals("9:00",updatedEvent.getStartTime());

        },12,Position.JUNIOR);

    }

    @Test
    public void shouldReturn4xxWhenUpdateIsNotPossible(){
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Event event1 = getEvent();
            event1.setPriority(1.0);
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setScheduledDate(LocalDate.parse("2023/03/27"));
            event1.setTitle("old work1");
            event1.setStartTime("9:00");
            event1.setEndTime("13:00");
            event1.setTimePeriod(TimePeriod.FOUR_HOUR);
            Event savedEvent1 = eventRepository.save(event1);

            Event event2 = getEvent();
            event2.setPriority(1.0);
            event2.setStartDate(LocalDate.parse("2023/03/27"));
            event2.setStartDate(LocalDate.parse("2023/03/27"));
            event2.setScheduledDate(LocalDate.parse("2023/03/27"));
            event2.setTitle("old work1");
            event2.setStartTime("13:00");
            event2.setEndTime("16:00");
            event2.setTimePeriod(TimePeriod.THREE_HOUR);
            Event savedEvent2 = eventRepository.save(event2);

            Event event3 = getEvent();
            event3.setPriority(1.0);
            event3.setStartDate(LocalDate.parse("2023/03/27"));
            event3.setStartDate(LocalDate.parse("2023/03/27"));
            event3.setScheduledDate(LocalDate.parse("2023/03/27"));
            event3.setTitle("old work1");
            event3.setStartTime("16:00");
            event3.setEndTime("17:00");
            event3.setTimePeriod(TimePeriod.ONE_HOUR);
            Event savedEvent3 = eventRepository.save(event3);

            Map<String, Object> eventRequestMap = getEventRequestMap();
            eventRequestMap.put("startDate","2023/03/27");
            eventRequestMap.put("endDate","2023/03/27");
            eventRequestMap.put("title","oldWork1");
            eventRequestMap.put("timePeriod","four_hour");
            eventRequestMap.put("priority",1.0);

            String content = asJsonString(eventRequestMap);

            try {
                mockMvc.perform(
                        put(SCHEDULE_URL+event3.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                ).andExpect(MockMvcResultMatchers.status().is4xxClientError());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }





        },12,Position.JUNIOR);
    }

    @Test
    public void shouldChangeTimePeriodBetweenEventsWhenUpdated(){
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Event event1 = getEvent();
            event1.setPriority(1.0);
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setScheduledDate(LocalDate.parse("2023/03/27"));
            event1.setTitle("old work1");
            event1.setStartTime("9:00");
            event1.setEndTime("13:00");
            event1.setTimePeriod(TimePeriod.FOUR_HOUR);
            Event savedEvent1 = eventRepository.save(event1);

            Event event2 = getEvent();
            event1.setPriority(1.0);
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setScheduledDate(LocalDate.parse("2023/03/27"));
            event1.setTitle("old work2");
            event1.setStartTime("9:00");
            event1.setEndTime("13:00");
            event1.setTimePeriod(TimePeriod.FOUR_HOUR);
            Event savedEvent2 = eventRepository.save(event1);

            Map<String, Object> eventRequestMap = getEventRequestMap();
            eventRequestMap.put("startDate","2023/03/27");
            eventRequestMap.put("endDate","2023/03/27");
            eventRequestMap.put("title","oldWork1");
            eventRequestMap.put("timePeriod","two_hour");
            eventRequestMap.put("priority",1.0);

            String content = asJsonString(eventRequestMap);

            try {
                mockMvc.perform(
                        put(SCHEDULE_URL+event1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Event updatedEvent1 = eventRepository.findById(event1.getId())
                    .orElseThrow(() -> new RuntimeException("event not found"));

            Assertions.assertEquals("11:00",updatedEvent1.getEndTime());


            Event updatedEvent2 = eventRepository.findById(event2.getId())
                    .orElseThrow(() -> new RuntimeException("event not found"));

            Assertions.assertEquals("13:00",updatedEvent2.getStartTime());
        },12,Position.JUNIOR);
    }


}
