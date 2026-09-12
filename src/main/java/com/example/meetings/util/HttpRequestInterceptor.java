package com.example.meetings.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class HttpRequestInterceptor implements ClientHttpRequestInterceptor {
    /**
     * Логирование всех запросов через restTemplate (в нашем случае)
     *
     * @param request   запрос
     * @param body      тело запроса
     * @param execution клиент
     * @return ClientHttpResponse
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            log.info("Method: {}", request.getMethod());
            log.info("URI: {}", request.getURI());
            log.info("Headers: {}", request.getHeaders());
            if (!request.getURI().toString().contains("uploadFile")) {
                log.info("Request Body: {}", new String(body));
            }

            ClientHttpResponse response = execution.execute(request, body);

            return log(response);
        } catch (Exception e) {
            log(null);
            throw e;
        }
    }

    /**
     * Логирование ответа
     *
     * @param response ответ
     * @return ClientHttpResponse
     */
    private ClientHttpResponse log(ClientHttpResponse response) throws IOException {
        String logBody = "";
        ClientHttpResponse responseCopy = new ResponseWrapper(response);

        if (response != null) {
            logBody = new String(responseCopy.getBody().readAllBytes());
        }

        log.info("Response body: {}", logBody);

        return responseCopy;
    }
}
