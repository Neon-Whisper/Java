package com.neon.mcp.server.csdn.domain.adapter;

import com.neon.mcp.server.csdn.domain.model.ArticleFunctionRequest;
import com.neon.mcp.server.csdn.domain.model.ArticleFunctionResponse;

import java.io.IOException;

public interface ICSDNPort {

    ArticleFunctionResponse writeArticle(ArticleFunctionRequest request) throws IOException;

}
