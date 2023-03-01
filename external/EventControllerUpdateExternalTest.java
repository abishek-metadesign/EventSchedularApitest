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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
                        put(SCHEDULE_URL+1)
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
                    validateResponseAsPerSchema(contentAsString, "/createEventResponseSchema.json");
                });
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        },12,Position.JUNIOR);
    }

    @Test
    public void shouldRescheduleIfThePlayerIsUpdated(){
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Event event1 = getEvent();

            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
            event1.setStartDate(LocalDate.parse("2023/03/27",format));
            event1.setStartDate(LocalDate.parse("2023/03/27",format));
            event1.setScheduledDate(LocalDate.parse("2023/03/27",format));
            event1.setTitle("old work1");
            event1.setStartTime(LocalTime.parse("09:00",timeFormat));
            event1.setEndTime(LocalTime.parse("13:00",timeFormat));
            event1.setTimePeriod(TimePeriod.FOUR_HOUR);
            Event savedEvent1 = eventRepository.save(event1);

            Event event2 = getEvent();
            event1.setStartDate(LocalDate.parse("2023/03/27",format));
            event1.setStartDate(LocalDate.parse("2023/03/27",format));
            event1.setScheduledDate(LocalDate.parse("2023/03/27",format));
            event1.setTitle("old work2");
            event1.setStartTime(LocalTime.parse("13:00",timeFormat));
            event1.setEndTime(LocalTime.parse("17:00",timeFormat));
            event1.setTimePeriod(TimePeriod.FOUR_HOUR);
            Event savedEvent2 = eventRepository.save(event2);

            Map<String, Object> eventRequestMap = getEventRequestMap();
            eventRequestMap.put("startDate","2023/03/27");
            eventRequestMap.put("endDate","2023/03/27");
            eventRequestMap.put("title","oldWork1");
            eventRequestMap.put("timePeriod","two_hour");

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

            Event updatedEvent2 = eventRepository.findById(event2.getId())
                    .orElseThrow(() -> new RuntimeException("event not found"));

            Assertions.assertEquals("13:00",updatedEvent2.getStartTime().toString());
            Assertions.assertEquals("15:00",updatedEvent2.getEndTime().toString());

        },12,Position.JUNIOR);
    }

    @Test
    public void shouldReturn4xxWhenUpdateIsNotPossible(){
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Event event1 = getEvent();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
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
            event2.setTitle("old work1");
            event2.setStartTime(LocalTime.parse("13:00",timeFormat));
            event2.setEndTime(LocalTime.parse("16:00",timeFormat));
            event2.setTimePeriod(TimePeriod.THREE_HOUR);
            Event savedEvent2 = eventRepository.save(event2);

            Event event3 = getEvent();
            event3.setStartDate(LocalDate.parse("2023/03/27",format));
            event3.setStartDate(LocalDate.parse("2023/03/27",format));
            event3.setScheduledDate(LocalDate.parse("2023/03/27",format));
            event3.setTitle("old work1");
            event3.setStartTime(LocalTime.parse("16:00",timeFormat));
            event3.setEndTime(LocalTime.parse("17:00",timeFormat));
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
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
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
            event2.setTitle("old work2");
            event2.setStartTime(LocalTime.parse("13:00",timeFormat));
            event2.setEndTime(LocalTime.parse("17:00",timeFormat));
            event2.setTimePeriod(TimePeriod.FOUR_HOUR);
            Event savedEvent2 = eventRepository.save(event2);

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

            Assertions.assertEquals("11:00",updatedEvent1.getEndTime().toString());


            Event updatedEvent2 = eventRepository.findById(event2.getId())
                    .orElseThrow(() -> new RuntimeException("event not found"));

            Assertions.assertEquals("15:00",updatedEvent2.getEndTime().toString());
        },12,Position.JUNIOR);
    }


}
