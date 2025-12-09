package com.neon.mcp.server.csdn.infrastructure.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleResponseDTO {
    private Integer code;
    private String traceId;
    private ArticleData data;
    private String msg;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArticleData {
        private String url;
        private Long id;
        private String qrcode;
        private String title;
        private String description;
    }
}