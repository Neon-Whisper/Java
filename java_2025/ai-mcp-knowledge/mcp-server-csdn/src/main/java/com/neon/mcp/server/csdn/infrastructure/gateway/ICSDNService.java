package com.neon.mcp.server.csdn.infrastructure.gateway;

import com.neon.mcp.server.csdn.infrastructure.gateway.dto.ArticleRequestDTO;
import com.neon.mcp.server.csdn.infrastructure.gateway.dto.ArticleResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ICSDNService {

    @Headers({
            "Accept: application/json, text/plain, */*",
            "Accept-Language: zh-TW,zh-CN;q=0.9,zh;q=0.8,en;q=0.7",
            "Content-Type: application/json;", // 保留原始请求的 Content-Type（带分号，与 Unirest 一致）
            "Origin: https://mp.csdn.net",
            "Referer: https://mp.csdn.net/",
            "Priority: u=1, i",
            "Sec-Ch-Ua: \"Chromium\";v=\"142\", \"Microsoft Edge\";v=\"142\", \"Not_A Brand\";v=\"99\"",
            "Sec-Ch-Ua-Mobile: ?1", // 还原原始值 ?1（Unirest 中是移动设备标识，之前的 ?0 是错误的）
            "Sec-Ch-Ua-Platform: \"Android\"", // 还原原始值 Android（Unirest 中是 Nexus 5 设备，之前的 Windows 是错误的）
            "Sec-Fetch-Dest: empty",
            "Sec-Fetch-Mode: cors",
            "Sec-Fetch-Site: same-site",
            "User-Agent: Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Mobile Safari/537.36 Edg/142.0.0.0",
            "X-Ca-Key: 203803574",
            "X-Ca-Nonce: f6004fd8-da0f-43ec-889b-c599c44e5662",
            "X-Ca-Signature: /hbujS14VK31MOUFbBiy3eYOM/zZEbStlBGm10fM1AI=",
            "X-Ca-Signature-Headers: x-ca-key,x-ca-nonce",
            "Cookie: UN=NEON_wf; uuid_tt_dd=10_37463564950-1734054865263-681383; FCNEC=%5B%5B%22AKsRol_EY_wr8yj-rPrUF4vMpkBy9vT7R09lGaqvHM22TEbRpOiXxtZ4o7OE4auaKhMC85Xzv4O7m2beqgOgnImUK_FDaRqqBgcPej5f5C5zgBvWdL3UiRLBnjszOrxYKDhNZ7e6kwLkMXDGA3jRwCIacLiTqXG26Q%3D%3D%22%5D%5D; Hm_ct_6bcd52f51e9b3dce32bec4a3997715ac=6525*1*10_37463564950-1734054865263-681383!5744*1*NEON_wf; c_dl_fref=https://so.csdn.net/so/search; ssxmod_itna2=QqRx0DBDRDcG5xBPGKiQ23BIoxxjxhuAddb2DA=WpND/DODFhERYIcLPAPKOZoNz+ah2tP2naObjxq9axOIhYz+erjoTf8LOI6uYB=OQkv/8zjUwW5kLFdkfvZ6RH2ipHWUUx2YWat6BqE/RotAKDPDKuGFD7=DeuDxD; ssxmod_itna=YqfxyD0GitD=7Lxlfx+rF=6PD5G=CKqDuQtCh40HPheGzDAxn4DaDHooy0hY=GWDrAWeiQ4w5=ECQ7aRKEBldP4f7m7qi8DCueboD44GTDt4DTD34DYDihQGyzqBQDjxAQDjAKGaDfwtDLxi7kEFPDBRY5+ikDQPDypPDIxD1=4wGDiI1DYv8Dim1D7HFDQI1t95D+gbvXxi36hDDBEG4DQe4s+9LXf5mT8oIkSOICIpA1/830=y1fxibkaSh6cC19ZfNi=0eZQD5bi03wWx5t7m5TQ04QiiYGjGepW24P7GgqCaNHGGDD==; c_ins_prid=-; c_ins_rid=1751623084544_737635; c_ins_fref=https://so.csdn.net/so/search; c_ins_fpage=/index.html; c_ins_um=-; ins_first_time=1751623095713; fid=20_76418248868-1760337631378-244709; c_adb=1; UserName=NEON_wf; UserInfo=0660d483c0944b6eaf8225fbe8084f8a; UserToken=0660d483c0944b6eaf8225fbe8084f8a; UserNick=%E6%99%9A%E9%A3%8ENEON; AU=4B5; BT=1760690578598; p_uid=U010000; csdn_newcert_NEON_wf=1; tfstk=gDhrL_cjhQdr-DsXNmFFuraKwIN8_W-6LXZQ-203PuquOLE3x2nYR6gH2k4E0owIFkNuxBoicDypykZ3YDgnFEO614389WqefCO_vOB1s0WutTY3oyUMlzmZNeSL9Wx6cG5HOuNKqPTX3W0DuyaCtkm3xiY4SPPhZJflniqY-WV3qzbcnPaFxTXltEu0DyV3xDVHuSqY-WqnxWDKVmiP6P2kuQuJ6t_gw8riro5i5jzo9T370GItgDezjWrVt6c4zqex1lfwdlDTVRwqmI1zTq4EvRhBX_EuQjHUIftFMq2TV7Gu_pIgjxnqWJcDj6zxuqDQLjtwg-GmyxwqHndsb7oUIb2V-e3quxVqLWJdjlDx0VenihfUkxo-gSGkRLFSCVGLIbAPXowT8DetOhC8qJjyLMUm9_cKUMXUErU4fE8VnvlpDrQ5YbXdp8pUulT889BLErCCUXTlp9eJyrr6liC..; c_dl_prid=1751605268560_494132; c_dl_rid=1761189292332_926471; c_dl_fpage=/download/qq_59957669/87911791; c_dl_um=distribute.pc_search_result.none-task-blog-2%7Eall%7Ebaidu_landing_v2%7Edefault-5-146123658-null-null.142%5Ev102%5Epc_search_result_base6; historyList-new=%5B%5D; c_segment=7; dc_sid=80e62395172529007fd281bc130ec2c9; Hm_lvt_6bcd52f51e9b3dce32bec4a3997715ac=1763618960,1764037896,1764039882,1764466541; HMACCOUNT=68D66E0EA05653C3; is_advert=1; vip_auto_popup=1; _clck=1ehgfq8%5E2%5Eg1g%5E0%5E1589; dc_session_id=10_1764469600385.289062; c_first_ref=default; c_first_page=https%3A//blog.csdn.net/NEON_wf%3Ftype%3Dblog; creative_btn_mp=3; SESSION=b893d139-69f4-4505-a91c-826d117d0b6c; c_dsid=11_1764470831213.659928; _clsk=1jt4xzd%5E1764470833656%5E3%5E0%5Ea.clarity.ms%2Fcollect; c_pref=https%3A//mp.csdn.net/mp_blog/manage/article%3Fspm%3D1011.2480.3001.8124; c_ref=https%3A//mp.csdn.net/; c_page_id=default; Hm_lpvt_6bcd52f51e9b3dce32bec4a3997715ac=1764472616; log_Id_pv=32; log_Id_view=336; dc_tos=t6irtv; log_Id_click=33",
            "Host: bizapi.csdn.net",
            "Connection: keep-alive"
    })
    @POST("blog-console-api/v1/postedit/saveArticle")
    Call<ArticleResponseDTO> saveArticle(@Body ArticleRequestDTO request);
}
