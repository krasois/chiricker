package com.chiricker.areas.home.schedules;

import com.chiricker.areas.home.utils.greeting.Greeting;
import com.chiricker.areas.home.utils.greeting.GreetingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledGreetingTasks {

    private final Greeting greeting;

    @Autowired
    public ScheduledGreetingTasks(Greeting greeting) {
        this.greeting = greeting;
    }

    @Scheduled(cron = "0 */10 6-10 * * *")
    public void morning() {
        this.greeting.setGreetingStatus(GreetingStatus.MORNING);
    }

    @Scheduled(cron = "0 */10 11-13 * * *")
    public void noon() {
        this.greeting.setGreetingStatus(GreetingStatus.NOON);
    }

    @Scheduled(cron = "0 */10 14-17 * * *")
    public void afternoon() {
        this.greeting.setGreetingStatus(GreetingStatus.AFTERNOON);
    }

    @Scheduled(cron = "0 */10 18-23 * * *")
    public void evening() {
        this.greeting.setGreetingStatus(GreetingStatus.EVENING);
    }

    @Scheduled(cron = "0 */10 0-5 * * *")
    public void night() {
        this.greeting.setGreetingStatus(GreetingStatus.NIGHT);
    }
}