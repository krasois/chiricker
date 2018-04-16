package com.chiricker.areas.home.utils.greeting;

import org.springframework.beans.factory.FactoryBean;

public class GreetingFactory implements FactoryBean<Greeting> {

    private Greeting greetingStatus;

    @Override
    public Greeting getObject() throws Exception {
        if (this.greetingStatus == null) {
            this.greetingStatus = new Greeting(GreetingStatus.GENERAL);
        }

        return this.greetingStatus;
    }

    @Override
    public Class<?> getObjectType() {
        return Greeting.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
