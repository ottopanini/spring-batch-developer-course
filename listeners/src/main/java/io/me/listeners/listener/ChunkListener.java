package io.me.listeners.listener;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

public class ChunkListener {
    @BeforeChunk
    void beforeChunk(ChunkContext context) {
        System.out.println(">> before the chunk");
    }

    @AfterChunk
    void afterChunk() {
        System.out.println(">> after the chunk");
    }
}
