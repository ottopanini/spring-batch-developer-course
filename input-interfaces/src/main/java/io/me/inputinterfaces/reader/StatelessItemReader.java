package io.me.inputinterfaces.reader;

import org.springframework.batch.item.ItemReader;

import java.util.Iterator;

public class StatelessItemReader implements ItemReader<String> {

    private final Iterator<String> data;

    public StatelessItemReader(Iterator<String> data) {
        this.data = data;
    }

    @Override
    public String read() throws Exception {
        if (data.hasNext())
            return data.next();
        else
            return null;
    }
}
