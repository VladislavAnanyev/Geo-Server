package com.example.meetings.integration.dadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Ответ от сервиса DaData
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DaDataSuggestions {

    /**
     * Список адресов по запросу
     */
    private List<DaDataResponse> suggestions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DaDataResponse {
        /**
         * Адрес одной строкой (как показывается в списке подсказок)
         */
        @JsonProperty("value")
        private String value;
        /**
         * Адрес одной строкой (полный, с индексом)
         */
        @JsonProperty("unrestricted_value")
        private String unrestrictedValue;

        @JsonProperty("data")
        private DataResponse data;

        /**
         * Дополнительная информация по адресу
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class DataResponse {
            /**
             * Координата долгота Y
             */
            @JsonProperty("geo_lat")
            private String lat;
            /**
             * Координата широта X
             */
            @JsonProperty("geo_lon")
            private String lon;
            /**
             * Улица
             */
            @JsonProperty("street")
            private String street;
            /**
             * Дом
             */
            @JsonProperty("house")
            private String house;
        }
    }
}
