package uk.co.metadesignsolutions.javachallenge.external;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.co.metadesignsolutions.javachallenge.enums.TimePeriod;
import uk.co.metadesignsolutions.javachallenge.external.testlogger.Position;
import uk.co.metadesignsolutions.javachallenge.external.testlogger.TestPrinter;
import uk.co.metadesignsolutions.javachallenge.models.Event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class EventControllerDeleteExternalTest extends BaseEventControllerExternalTest {

    final String token = "Bearer SkFabTZibXE1aE14ckpQUUxHc2dnQ2RzdlFRTTM2NFE2cGI4d3RQNjZmdEFITmdBQkE=";
    final String authorization = "Authorization";

    private static TestPrinter testPrinter ;

     @BeforeAll
     public static void setupAll(){
         testPrinter = new TestPrinter();
     }

    @Test
    public void shouldReturn4xxIfEventDoesNotExist() throws Exception {
        testPrinter.print(()->{
            h2Util.resetDatabase();
            try {
                mockMvc.perform(
                        delete(SCHEDULE_URL+"/1")
                                .header(authorization,token)
                                .contentType(MediaType.APPLICATION_JSON)
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
             try {
                 mockMvc.perform(
                         delete(SCHEDULE_URL+"/1")
                                 .header(authorization,token)
                                 .contentType(MediaType.APPLICATION_JSON)
                 ).andExpect(MockMvcResultMatchers.status().isNotFound());
             } catch (Exception e) {
                 throw new RuntimeException(e);
             }
         },12,Position.JUNIOR);

    }

    @Test
    public void shouldReturn404IfTokenIsInvalid(){
        testPrinter.print(()->{
            h2Util.resetDatabase();
            try {
                mockMvc.perform(
                        delete(SCHEDULE_URL+"/1")
                                .header("Authorization",token+"invalid")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isForbidden());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },12,Position.JUNIOR);
    }


    @Test
    public void shouldReturn401IfTokenIsNotPresentInHeader() throws Exception {
        testPrinter.print(()->{
            h2Util.resetDatabase();
            try {
                mockMvc.perform(
                        delete(SCHEDULE_URL+"/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },12,Position.JUNIOR);

    }


    @Test
    public void shouldReturn202OnDelete() throws Exception {
        testPrinter.print(()->{
            Event event = getEvent();
            Event savedEvent = eventRepository.save(event);
            try {
                mockMvc.perform(delete(SCHEDULE_URL+"/"+savedEvent.getId())
                                .header(authorization,token))
                        .andExpect(MockMvcResultMatchers.status().isAccepted());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },12,Position.JUNIOR);
    }

    @Test
    public void shouldDeleteEventFromDatabase(){
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Event event = getEvent();
            Event savedEvent = eventRepository.save(event);
            long eventCount = eventRepository.count();
            Assertions.assertEquals(1,eventCount);
            try {
                mockMvc.perform(delete(SCHEDULE_URL+"/"+savedEvent.getId())
                        .header(authorization,token));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            long count = eventRepository.count();
            Assertions.assertEquals(0,count);
        },12,Position.JUNIOR);
    }

    @Test
    public void shouldDeleteOnlyEventWithSpecifiedId(){
        testPrinter.print(()->{
            h2Util.resetDatabase();
            Event event = getEvent();
            eventRepository.save(event);
            Event savedEvent = getEvent();
            eventRepository.save(savedEvent);
            try {
                mockMvc.perform(delete(SCHEDULE_URL+"/"+savedEvent.getId())
                        .header(authorization,token));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            boolean secondPlayerExists = eventRepository.existsById(savedEvent.getId());
            Assertions.assertFalse(secondPlayerExists);
            long count = eventRepository.count();
            Assertions.assertEquals(1,count);
            eventRepository.deleteAll();
        },12,Position.JUNIOR);
    }

    @Test
    public void shouldRescheduleWhenAEventIsDeleted(){
        testPrinter.print(()->{
            h2Util.resetDatabase();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

            Event event = getEvent();
            event.setStartDate(LocalDate.parse("2023/03/27",format));
            event.setStartDate(LocalDate.parse("2023/03/27",format));
            event.setScheduledDate(LocalDate.parse("2023/03/27",format));
            event.setTitle("event");
            event.setStartTime(LocalTime.parse("09:00",timeFormat));
            event.setEndTime(LocalTime.parse("11:00",timeFormat));
            event.setTimePeriod(TimePeriod.TWO_HOUR);
            Event savedEvent = eventRepository.save(event);

            Event event1 = getEvent();
            event1.setStartDate(LocalDate.parse("2023/03/27",format));
            event1.setStartDate(LocalDate.parse("2023/03/27",format));
            event1.setScheduledDate(LocalDate.parse("2023/03/27",format));
            event1.setTitle("event 1");
            event1.setStartTime(LocalTime.parse("11:00",timeFormat));
            event1.setEndTime(LocalTime.parse("13:00",timeFormat));
            event1.setTimePeriod(TimePeriod.TWO_HOUR);
            Event savedEvent1 = eventRepository.save(event1);

            Event event2 = getEvent();
            event2.setStartDate(LocalDate.parse("2023/03/27",format));
            event2.setStartDate(LocalDate.parse("2023/03/27",format));
            event2.setScheduledDate(LocalDate.parse("2023/03/27",format));
            event2.setTitle("event 2");
            event2.setStartTime(LocalTime.parse("13:00",timeFormat));
            event2.setEndTime(LocalTime.parse("15:00",timeFormat));
            event2.setTimePeriod(TimePeriod.TWO_HOUR);
            Event savedEvent2 = eventRepository.save(event2);

            Event event3 = getEvent();
            event3.setStartDate(LocalDate.parse("2023/03/27",format));
            event3.setStartDate(LocalDate.parse("2023/03/27",format));
            event3.setScheduledDate(LocalDate.parse("2023/03/27",format));
            event3.setTitle("event 3");
            event3.setStartTime(LocalTime.parse("15:00",timeFormat));
            event3.setEndTime(LocalTime.parse("17:00",timeFormat));
            event3.setTimePeriod(TimePeriod.TWO_HOUR);
            Event savedEvent3 = eventRepository.save(event3);


            try {
                mockMvc.perform(delete(SCHEDULE_URL+"/"+event1.getId())
                        .header(authorization,token));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            List<Event> events = eventRepository.findAll();
            Set<String> collect = events.stream().map(Event::getStartTime).map(LocalTime::toString).collect(Collectors.toSet());
            List<String> data = new ArrayList<>();
            data.add("09:00");
            data.add("11:00");
            data.add("13:00");
            Assertions.assertTrue(collect.containsAll(data));


        },12,Position.JUNIOR);
    }

}
