package io.me.itemstream.config;

import org.springframework.batch.item.*;

import java.util.List;

public class StatefulItemReader implements ItemStreamReader<String> {
    private final List<String> items;
    private int curIndex = -1;
    private boolean restart = false;

    public StatefulItemReader(List<String> items) {
        this.items = items;
        curIndex = 0;
    }

    @Override
    public String read() throws Exception {
        String item = null;

        if (this.curIndex < this.items.size()) {
            item = this.items.get(this.curIndex);
            this.curIndex++;
        }

        if (this.curIndex == 42 && !restart) {
            throw new RuntimeException("The Answer to the Ultimate Question of Life, the Universe and all the rest.");
        }

        return item;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey("curIndex")) {
            this.curIndex = executionContext.getInt("curIndex");
            this.restart = true;
        } else {
            this.curIndex = 0;
            executionContext.put("curIndex", this.curIndex);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.put("curIndex", this.curIndex);
    }

    @Override
    public void close() throws ItemStreamException {

    }
}
