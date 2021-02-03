package io.me.skipretrylisteners.components;

import org.springframework.batch.core.SkipListener;

public class CustomSkipListener implements SkipListener<String, String> {
    @Override
    public void onSkipInRead(Throwable throwable) {
    }

    @Override
    public void onSkipInWrite(String item, Throwable throwable) {
        System.out.println(">> Skipping " + item + " because writing it caused the error: " + throwable);
    }

    @Override
    public void onSkipInProcess(String item, Throwable throwable) {
        System.out.println(">> Skipping " + item + " because writing it caused the error: " + throwable);
    }
}
