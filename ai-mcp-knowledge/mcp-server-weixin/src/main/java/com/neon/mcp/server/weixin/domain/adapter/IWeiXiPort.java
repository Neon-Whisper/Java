package com.neon.mcp.server.weixin.domain.adapter;

import com.neon.mcp.server.weixin.domain.model.WeiXinNoticeFunctionRequest;
import com.neon.mcp.server.weixin.domain.model.WeiXinNoticeFunctionResponse;

import java.io.IOException;

public interface IWeiXiPort {
    WeiXinNoticeFunctionResponse weixinNotice(WeiXinNoticeFunctionRequest request) throws IOException;

}
