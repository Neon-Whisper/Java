package com.neon.dev.tech.api;

import org.springframework.ai.chat.ChatResponse;
import reactor.core.publisher.Flux;

public interface IAiService {

    ChatResponse generate(String  model, String prompt);

    Flux<ChatResponse> generateStream(String  model, String prompt);

}
