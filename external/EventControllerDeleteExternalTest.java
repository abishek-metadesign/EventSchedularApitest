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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
                ).andExpect(MockMvcResultMatchers.status().isNotFound());
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
    public void shouldReturn200OnDelete() throws Exception {
        testPrinter.print(()->{
            Event event = getEvent();
            Event savedEvent = eventRepository.save(event);
            try {
                mockMvc.perform(delete(SCHEDULE_URL+"/"+savedEvent.getId())
                                .header(authorization,token))
                        .andExpect(MockMvcResultMatchers.status().isOk());
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
                mockMvc.perform(delete(SCHEDULE_URL+"/"+event.getId())
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
            Event secondPlayer = getEvent();
            eventRepository.save(secondPlayer);
            try {
                mockMvc.perform(delete(SCHEDULE_URL+"/"+secondPlayer.getId())
                        .header(authorization,token));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            boolean secondPlayerExists = eventRepository.existsById(secondPlayer.getId());
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
            Event event = getEvent();
            event.setPriority(1.0);
            event.setStartDate(LocalDate.parse("2023/03/27"));
            event.setStartDate(LocalDate.parse("2023/03/27"));
            event.setScheduledDate(LocalDate.parse("2023/03/27"));
            event.setTitle("old work");
            event.setStartTime("9:00");
            event.setEndTime("11:00");
            event.setTimePeriod(TimePeriod.TWO_HOUR);
            Event savedEvent = eventRepository.save(event);

            Event event1 = getEvent();
            event1.setPriority(1.0);
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setStartDate(LocalDate.parse("2023/03/27"));
            event1.setScheduledDate(LocalDate.parse("2023/03/27"));
            event1.setTitle("old work");
            event1.setStartTime("11:00");
            event1.setEndTime("13:00");
            event1.setTimePeriod(TimePeriod.TWO_HOUR);
            Event savedEvent1 = eventRepository.save(event1);

            Event event2 = getEvent();
            event2.setPriority(1.0);
            event2.setStartDate(LocalDate.parse("2023/03/27"));
            event2.setStartDate(LocalDate.parse("2023/03/27"));
            event2.setScheduledDate(LocalDate.parse("2023/03/27"));
            event2.setTitle("old work");
            event2.setStartTime("13:00");
            event2.setEndTime("15:00");
            event2.setTimePeriod(TimePeriod.TWO_HOUR);
            Event savedEvent2 = eventRepository.save(event2);

            Event event3 = getEvent();
            event3.setPriority(1.0);
            event3.setStartDate(LocalDate.parse("2023/03/27"));
            event3.setStartDate(LocalDate.parse("2023/03/27"));
            event3.setScheduledDate(LocalDate.parse("2023/03/27"));
            event3.setTitle("old work");
            event3.setStartTime("15:00");
            event3.setEndTime("17:00");
            event3.setTimePeriod(TimePeriod.TWO_HOUR);
            Event savedEvent3 = eventRepository.save(event3);


            try {
                mockMvc.perform(
                        delete(SCHEDULE_URL+event1.getId())
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            List<Event> events = eventRepository.findAll();
            Set<String> collect = events.stream().map(Event::getStartTime).collect(Collectors.toSet());
            List<String> data = new ArrayList<>();
            data.add("9:00");
            data.add("12:00");
            data.add("15:00");
            Assertions.assertTrue(collect.containsAll(data));


        },12,Position.JUNIOR);
    }

}
