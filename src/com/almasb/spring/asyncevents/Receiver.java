package com.almasb.spring.asyncevents;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import reactor.event.Event;
import reactor.function.Consumer;

@Service
class Receiver implements Consumer<Event<Integer>> {

    @Autowired
    CountDownLatch latch;

    RestTemplate restTemplate = new RestTemplate();

    @Override
    public void accept(Event<Integer> ev) {
        Quote quote = restTemplate.getForObject("http://www.iheartquotes.com/api/v1/random?format=json", Quote.class);
        System.out.println("Quote " + ev.getData() + ": " + quote.getQuote() + " source: " + quote.getSource());
        latch.countDown();

        System.out.println(latch.getCount());
    }

}