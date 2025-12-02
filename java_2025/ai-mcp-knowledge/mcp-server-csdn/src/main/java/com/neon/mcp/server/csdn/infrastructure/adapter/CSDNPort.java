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
import java.util.Collections;

@Slf4j
@Component
public class CSDNPort implements ICSDNPort {

    @Resource
    private ICSDNService csdnService;

    @Resource
    private CSDNApiProperties csdnApiProperties;

    @Override
    public ArticleFunctionResponse writeArticle(ArticleFunctionRequest request) throws IOException {
        log.info("CSDNPort.writeArticle 被调用，请求参数: {}", JSON.toJSONString(request));
        System.out.println("CSDNPort.writeArticle 被调用，请求参数: " + JSON.toJSONString(request));

        ArticleRequestDTO articleRequestDTO = new ArticleRequestDTO();
        // 必需字段
        articleRequestDTO.setTitle(request.getTitle());
        articleRequestDTO.setMarkdowncontent(request.getMarkdowncontent());
        articleRequestDTO.setContent(request.getContent());
        articleRequestDTO.setTags(request.getTags());
        articleRequestDTO.setDescription(request.getDescription());
        articleRequestDTO.setCategories(csdnApiProperties.getCategories());

        // 补充其他字段确保完整性
        articleRequestDTO.setReadType("public");
        articleRequestDTO.setLevel("0");
        articleRequestDTO.setStatus(0);
        articleRequestDTO.setType("original");
        articleRequestDTO.setOriginal_link("");
        articleRequestDTO.setAuthorized_status(true);
        articleRequestDTO.setResource_url("");
        articleRequestDTO.setNot_auto_saved("0");
        articleRequestDTO.setSource("pc_mdeditor");
        articleRequestDTO.setCover_images(Collections.emptyList());
        articleRequestDTO.setCover_type(0);
        articleRequestDTO.setIs_new(1);
        articleRequestDTO.setVote_id(0);
        articleRequestDTO.setResource_id("");
        articleRequestDTO.setPubStatus("draft");
        articleRequestDTO.setSync_git_code(0);

        Call<ArticleResponseDTO> call = csdnService.saveArticle(articleRequestDTO);
        Response<ArticleResponseDTO> response = call.execute();
        if (!response.isSuccessful()) {
            String errorBody = response.errorBody().string();
            System.out.println("完整错误响应：" + errorBody);
            log.error("CSDN API调用失败，HTTP状态码: {}, 错误信息: {}", response.code(), errorBody);
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
