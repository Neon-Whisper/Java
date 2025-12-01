package com.neon.mcp.server.csdn.infrastructure.adapter;

import com.neon.mcp.server.csdn.domain.adapter.ICSDNPort;
import com.neon.mcp.server.csdn.domain.model.ArticleFunctionRequest;
import com.neon.mcp.server.csdn.domain.model.ArticleFunctionResponse;
import com.neon.mcp.server.csdn.infrastructure.gateway.ICSDNService;
import com.neon.mcp.server.csdn.infrastructure.gateway.dto.ArticleRequestDTO;
import com.neon.mcp.server.csdn.infrastructure.gateway.dto.ArticleResponseDTO;
import com.neon.mcp.server.csdn.types.properties.CSDNApiProperties;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

@Slf4j
@Component
public class CSDNPort implements ICSDNPort {

    @Resource
    private ICSDNService csdnService;

    @Resource
    private CSDNApiProperties csdnApiProperties;

    @Override
    public ArticleFunctionResponse writeArticle(ArticleFunctionRequest request) throws IOException {

        ArticleRequestDTO articleRequestDTO = new ArticleRequestDTO();
        articleRequestDTO.setTitle(request.getTitle());
        articleRequestDTO.setMarkdowncontent(request.getMarkdowncontent());
        articleRequestDTO.setContent(request.getContent());
        articleRequestDTO.setTags(request.getTags());
        articleRequestDTO.setDescription(request.getDescription());
        articleRequestDTO.setCategories(csdnApiProperties.getCategories());

        Call<ArticleResponseDTO> call = csdnService.saveArticle(articleRequestDTO);
        Response<ArticleResponseDTO> response = call.execute();
        if (!response.isSuccessful()) {
            // 关键代码：读取完整 errorBody
            String errorBody = response.errorBody().string();
            System.out.println("完整错误响应：" + errorBody); // 控制台打印
            // 或断点停在这里，查看 errorBody 变量的完整值
        }
        log.info("请求CSDN发帖 \nreq:{} \nres:{}", JSON.toJSONString(articleRequestDTO), JSON.toJSONString(response));

        if (response.isSuccessful()) {
            ArticleResponseDTO articleResponseDTO = response.body();
            if (null == articleResponseDTO) return null;

            ArticleFunctionResponse articleFunctionResponse = new ArticleFunctionResponse();
            articleFunctionResponse.setCode(articleResponseDTO.getCode());
            articleFunctionResponse.setMsg(articleResponseDTO.getMsg());

            return articleFunctionResponse;
        }

        return null;
    }

}
