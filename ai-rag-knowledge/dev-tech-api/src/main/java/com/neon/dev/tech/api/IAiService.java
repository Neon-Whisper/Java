package com.neon.dev.tech.api;

import com.neon.dev.tech.api.Response.Response;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

public interface IAiService {

    ChatResponse generate(String  model, String prompt);

    Flux<ChatResponse> generateStream(String  model, String prompt);

    Flux<ChatResponse> generateStreamRag( String model, String ragTag, String message);


}
