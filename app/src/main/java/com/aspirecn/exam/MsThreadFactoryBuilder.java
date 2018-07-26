package com.aspirecn.exam;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ding
 * Created by ding on 11/6/17.
 */

public class MsThreadFactoryBuilder implements ThreadFactory {

    private static final String THREAD_NAME = "Ms-Thread-";

    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public Thread newThread(@NonNull Runnable r) {

        Thread thread = new Thread(r);

        thread.setName(THREAD_NAME + index.incrementAndGet());

        return thread;
    }

    static class Builder {


        Builder() {

        }

        MsThreadFactoryBuilder builder() {

            return new MsThreadFactoryBuilder();
        }

    }
}
