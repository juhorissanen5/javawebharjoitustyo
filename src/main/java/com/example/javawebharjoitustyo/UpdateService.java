package com.example.javawebharjoitustyo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class UpdateService {
    private static final Logger logger = LoggerFactory.getLogger(UpdateService.class);
    private MainView mainView;

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    @Scheduled(fixedRate = 5000)
    public void scheduledUpdate() {
        try {
            if (mainView != null) {
                mainView.pushUpdate();
            }
        } catch (Exception e) {
            logger.error("Error during scheduled update", e);
        }
    }
}