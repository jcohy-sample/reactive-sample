package com.jcohy.sample.reactive.chapter_03.news_service;

import com.jcohy.sample.reactive.chapter_03.news_service.dto.NewsLetter;
import org.reactivestreams.Subscriber;
import org.reactivestreams.tck.SubscriberBlackboxVerification;
import org.reactivestreams.tck.TestEnvironment;

public class NewsServiceSubscriberTest
        extends SubscriberBlackboxVerification<NewsLetter> {

    public NewsServiceSubscriberTest() {
        super(new TestEnvironment());
    }

    @Override
    public Subscriber<NewsLetter> createSubscriber() {
        return new NewsServiceSubscriber(Integer.MAX_VALUE);
    }

    @Override
    public void triggerRequest(Subscriber<? super NewsLetter> s) {
        ((NewsServiceSubscriber) s).eventuallyReadDigest();
    }

    @Override
    public NewsLetter createElement(int element) {
        return new StubNewsLetter(element);
    }
}
