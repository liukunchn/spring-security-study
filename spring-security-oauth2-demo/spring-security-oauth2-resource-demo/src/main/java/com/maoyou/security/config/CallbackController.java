package com.maoyou.security.config;

import com.maoyou.security.util.Response;
import com.maoyou.security.util.ResponseCode;
import com.maoyou.security.util.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName CallbackController
 * @Description
 * @Author 刘坤 kunliu@yinhai.com
 * @Date 2022/8/11 18:15
 * @Version 1.0
 */
@RestController
public class CallbackController {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/callback")
    public Response callback(@RequestParam("code") String code, HttpServletResponse response) {
        // grant_type=authorization_code&client_id=c1&client_secret=secret&code=mP_7aV&redirect_uri=http://www.baidu.com
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> param= new LinkedMultiValueMap<String, String>();
        param.add("grant_type", "authorization_code");
        param.add("client_id", "self");
        param.add("client_secret", "secret");
        param.add("code", code);
        param.add("redirect_uri", "http://127.0.0.1:8081/callback");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(param, headers);
        ResponseEntity<TokenResponse> responseEntity = restTemplate.postForEntity("http://127.0.0.1:8080/oauth/token", request, TokenResponse.class);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            // 通过响应头设置token
            response.setHeader("Authorization", responseEntity.getBody().getAccess_token());
            // todo 通过响应设置重定向到之前保存的ref
            return Response.success();
        } else {
            return Response.error(ResponseCode.AUTHENTICATE_ERROR).setMessage(responseEntity.getBody().getError());
        }
    }
}
