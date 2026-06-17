package com.odoru.badgeservice.consumer;

import com.odoru.badgeservice.config.RabbitMQConfig;
import com.odoru.badgeservice.dto.AttendanceScanRequest;
import com.odoru.badgeservice.service.BadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AttendanceConsumer {

    private final BadgeService badgeService;

    @RabbitListener(queues = RabbitMQConfig.ATTENDANCE_QUEUE)
    public void processAttendanceScan(final AttendanceScanRequest request) {
        log.info("Received async badge scan event: {}", request);
        try {
            badgeService.logAttendance(
                request.getBadgeNumber(),
                request.getLessonId()
            );
            log.info(
                "Successfully processed attendance scan for badge: {}",
                request.getBadgeNumber()
            );
        } catch (Exception ex) {
            log.error(
                "Failed to process attendance scan for request: {}",
                request,
                ex
            );
        }
    }
}
