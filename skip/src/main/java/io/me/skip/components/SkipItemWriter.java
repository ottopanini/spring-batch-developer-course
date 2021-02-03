package io.me.skip.components;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class SkipItemWriter implements ItemWriter<String> {
    boolean skip = false;
    private int attemptCount = 0;

    @Override
    public void write(List<? extends String> items) throws Exception {
        for (String item : items) {
            System.out.println("Writing item " + item);
            if (skip && item.equalsIgnoreCase("-84")) {
                attemptCount++;

                System.out.println("Writing of item " + item + " failed");
                throw new CustomRetryableException("Writing failed. Attempt: " + attemptCount);
            }
            else {
                System.out.println(item);
            }
        }
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}
