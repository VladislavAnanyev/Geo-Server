package com.example.meetings.integration.dadata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@Slf4j
public class DaDataClient {

    public static final String X_SECRET = "X-Secret";
    public static final String TOKEN = "Token ";
    private final RestTemplate restTemplate;

    @Value("dadata.key")
    private String apiKey;
    @Value("dadata.secret")
    private String apiSecret;
    @Value("dadata.url")
    private String url;

    public DaDataClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Поиск координат по адресу
     *
     * @param address адрес
     */
    @SuppressWarnings("unchecked")
    public DaDataSuggestions findSuggestions(@NonNull String address) {
        AddressQuery addressQuery = getAddressQuery(address);
        HttpEntity<?> requestEntity = new HttpEntity<>(addressQuery, getHeaders());
        try {

            ResponseEntity<DaDataSuggestions> response = (ResponseEntity<DaDataSuggestions>) doRequest(requestEntity);
            DaDataSuggestions body = requireNonNull(response.getBody());

            List<DaDataSuggestions.DaDataResponse> suggestions = body.getSuggestions().stream()
                    .filter(suggestion -> suggestion.getData() != null && suggestion.getData().getLat() != null && suggestion.getData().getLon() != null)
                    .toList();

            DaDataSuggestions result = new DaDataSuggestions();
            result.setSuggestions(suggestions);

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ResponseEntity<?> doRequest(HttpEntity<?> httpEntity) {
        log.info("Выполнение запроса в DaData Suggestions");

        return restTemplate.exchange(url, POST, httpEntity, DaDataSuggestions.class);
    }

    /**
     * Получить строку адреса для запроса в DaData
     *
     * @param address адрес
     * @return строка адреса
     */
    private AddressQuery getAddressQuery(String address) {
        AddressQuery query = new AddressQuery();
        query.setQuery(address);
        return query;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        requestHeaders.add(AUTHORIZATION, TOKEN + apiKey);
        requestHeaders.add(X_SECRET, apiSecret);

        return requestHeaders;
    }
}
