package com.example.meetings.controller.api;

import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.meeting.model.domain.Meeting;
import com.example.meetings.meeting.model.dto.output.MeetingView;
import com.example.meetings.meeting.repository.MeetingRepository;
import com.example.meetings.meeting.service.MeetingService;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS
}, executionPhase = BEFORE_TEST_METHOD)
public class MeetingServiceTest extends AbstractControllerIntegrationTest {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createMeetingIsIdempotentForSamePairAndReversedOrder() {
        User first = userRepository.findById(1001L).orElseThrow();
        User second = userRepository.findById(1003L).orElseThrow();
        LocalDateTime now = LocalDateTime.now();

        Meeting created = meetingService.createMeeting(geo(first, now), geo(second, now));
        Meeting duplicate = meetingService.createMeeting(geo(second, now), geo(first, now));

        assertNotNull(created);
        assertNull(duplicate);
        assertEquals(1, meetingsToday(1001L).size());
    }

    @Test
    public void concurrentCreateMeetingInsertsOnce() throws Exception {
        User first = userRepository.findById(1001L).orElseThrow();
        User second = userRepository.findById(1003L).orElseThrow();
        LocalDateTime now = LocalDateTime.now();
        Geolocation firstGeo = geo(first, now);
        Geolocation secondGeo = geo(second, now);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(2);
        AtomicInteger created = new AtomicInteger();

        executor.submit(() -> {
            await(start);
            try {
                if (meetingService.createMeeting(firstGeo, secondGeo) != null) {
                    created.incrementAndGet();
                }
            } finally {
                done.countDown();
            }
        });
        executor.submit(() -> {
            await(start);
            try {
                if (meetingService.createMeeting(secondGeo, firstGeo) != null) {
                    created.incrementAndGet();
                }
            } finally {
                done.countDown();
            }
        });

        start.countDown();
        done.await(10, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertEquals(1, created.get());
        assertEquals(1, meetingsToday(1001L).size());
    }

    private static Geolocation geo(User user, LocalDateTime time) {
        return new Geolocation()
                .setUser(user)
                .setLat(55.75)
                .setLng(37.61)
                .setUpdatedAt(time);
    }

    private List<MeetingView> meetingsToday(Long userId) {
        return meetingRepository.getMyMeetings(
                userId,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay()
        );
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
