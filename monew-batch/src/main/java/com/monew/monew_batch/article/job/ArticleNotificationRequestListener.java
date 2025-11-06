package com.monew.monew_batch.article.job;

import com.monew.monew_batch.notification.service.NotificationAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleNotificationRequestListener implements JobExecutionListener {

    private final NotificationAsyncService notificationAsyncService;

    @Override
    public void afterJob(JobExecution jobExecution) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> stats =
                (Map<Long, Integer>) jobExecution.getExecutionContext().get("newLinkCountsByInterestId");

        notificationAsyncService.sendNotification(stats);
    }
}