package com.example.meetings.util;

import com.example.meetings.request.model.domain.RequestStatus;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static com.example.meetings.request.model.domain.RequestStatus.ACCEPTED;
import static com.example.meetings.request.model.domain.RequestStatus.PENDING;
import static java.util.List.of;

public final class Constants {

    private Constants() {
    }

    public static final long ADMIN_ID = 2151L;

    public static final String SIGNIN_EMAIL_SUBJECT = "Код для входа";
    public static final String CONTENT = "Привет! Это чат поддержки. Пиши если возникнут вопросы";
    public static final String DEFAULT_FIRST_NAME = "Анонимный";
    public static final String DEFAULT_LAST_NAME = "Пользователь";

    public static final String FILE_STORAGE_BASE_URL = "https://storage.yandexcloud.net/meetings-file-storage/";
    public static final String DEFAULT_PHOTO_URL = FILE_STORAGE_BASE_URL + "free-icon-dog-17617815.png";
    public static final String PLAN_DEFAULT_IMAGE_URL_TEMPLATE = FILE_STORAGE_BASE_URL + "plan-default-%02d.png";

    public static final String NOTIFICATION_MEETING_TITLE = "Встреча!";
    public static final String NOTIFICATION_MEETING_DESCRIPTION = "У вас новая встреча! Подробности внутри";
    public static final String NOTIFICATION_REQUEST_TITLE = "Заявка!";
    public static final String NOTIFICATION_REQUEST_DESCRIPTION = "Кто-то добавляется в друзья! Подробности внутри";
    public static final String NOTIFICATION_LOCATION_SHARING_TITLE = "Геолокация";
    public static final String NOTIFICATION_LOCATION_SHARING_DESCRIPTION = "Запрос на обмен геолокацией. Подробности внутри";

    public static final String PLAN_RESPONSE_MARKER = "Отклик на план: ";
    public static final String PLAN_CHAT_MESSAGE_TEMPLATE = "Это чат плана: «%s», начало: %s.";
    public static final DateTimeFormatter PLAN_START_AT_FORMAT =
            DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.forLanguageTag("ru"));

    public static final String INCOMING_MESSAGES_QUEUE = "incoming-messages";
    public static final String RABBIT_EXCHANGE_SALT = "$1$Zyi$3";
    public static final String GEOLOCATION_MEETING_EXECUTOR = "geolocationMeetingExecutor";

    public static final double METERS_PER_DEGREE_LAT = 111_320d;
    public static final int EARTH_RADIUS_METERS = 6_371_210;

    public static final String DADATA_HEADER_X_SECRET = "X-Secret";
    public static final String DADATA_HEADER_TOKEN_PREFIX = "Token ";

    public static final String SIGMA_SMS_AWS_TATYANA = "aws:tatyana";
    public static final String SIGMA_SMS_SENDER_NUMBER = "74956665610";

    public static final String IDIRECT_VOICE_CODE = "VOICECODE";

    public static final String OLLAMA_GENERATE_PATH = "/api/generate";
    public static final String OLLAMA_RESPONSE_FORMAT_JSON = "json";

    public static final String YANDEX_FINISH_REASON_CONTENT_FILTER = "content_filter";

    public static final String MODERATION_FIELD_FIRST_NAME = "имя";
    public static final String MODERATION_FIELD_DESCRIPTION = "описание";
    public static final String MODERATION_FIELD_PLAN = "план";

    public static final String MODERATION_PLAN_TEXT_SEPARATOR = "\n\n";

    public static final String EXCEPTION_INTERNAL_SERVER = "exception.internal.server";
    public static final String EXCEPTION_TEXT_MODERATION_REJECTED = "exception.text.moderation.rejected";
    public static final String EXCEPTION_TEXT_MODERATION_UNAVAILABLE = "exception.text.moderation.unavailable";

    public static final int MODERATION_LLM_MAX_TOKENS = 32;
    public static final double MODERATION_TEMPERATURE = 0;

    public static final String YANDEX_CHAT_COMPLETIONS_PATH = "/chat/completions";
    public static final String YANDEX_CONTENT_FILTER_REJECTED_JSON = "{\"result\":false}";

    public static final String MODERATION_PROMPT_FIRST_NAME = """
            Ты проверяешь значение поля "Имя" в профиле. Значение: «%s»
            Нужно определить только наличие запрещённого контента.

            Верни {"result": false}, если в значении явно есть:
            наркотики, клады, меф, соль, трава, шишки;
            интим, секс, эскорт, секс-услуги, порно, лгбт, однополые отношения;
            реклама, услуги, прайс, указание цены, доставка, призыв купить или написать;
            ссылки, телефон, telegram, whatsapp;
            мат, оскорбления, угрозы;
            оружие, мошенничество, расизм, нацизм, призывы к противоправным действиям.

            Верни {"result": true}, если запрещённого контента нет.

            Ответ только JSON: {"result": true} или {"result": false}
            """;

    public static final String MODERATION_PROMPT_DESCRIPTION = """
            Ты проверяешь описание профиля. Текст: «%s»
            Обычные фразы о себе, интересах, планах и нейтральные шутки разрешены.
            Нужно определить наличие запрещённого контента.

            Верни {"result": false}, если текст содержит:
            наркотики, клады, меф, соль, трава, шишки;
            интим, секс, эскорт, секс-услуги, порно, ЛГБТ;
            рекламу услуг или товаров, прайс, указание цены, доставку, призыв купить или написать;
            ссылки, телефон, telegram, whatsapp;
            мат, оскорбления, угрозы, ненависть;
            оружие, мошенничество, расизм, нацизм, призывы к противоправным действиям.

            Верни {"result": true}, если запрещённого контента нет.
            
            Ответ только JSON: {"result": true} или {"result": false}
            """;

    public static final String MODERATION_PROMPT_PLAN = """
            Ты проверяешь заголовок и описание плана встречи. Текст: «%s»
            Обычные формулировки о встрече, месте, времени и нейтральные шутки разрешены.
            Нужно определить наличие запрещённого контента.

            Верни {"result": false}, если текст содержит:
            наркотики, клады, меф, соль, трава, шишки;
            интим, секс, эскорт, секс-услуги, порно, лгбт, однополые отношения;
            рекламу услуг или товаров, прайс, указание цены, доставку, призыв купить или написать;
            ссылки, телефон, telegram, whatsapp;
            мат, оскорбления, угрозы, ненависть;
            оружие, мошенничество, расизм, нацизм, призывы к противоправным действиям.

            Верни {"result": true}, если запрещённого контента нет.
            
            Ответ только JSON: {"result": true} или {"result": false}
            """;

    public static final char JSON_OBJECT_START = '{';
    public static final char JSON_OBJECT_END = '}';
    public static final String PROMPT_QUOTE_LEFT = "«";
    public static final String PROMPT_QUOTE_RIGHT = "»";
    public static final String PROMPT_QUOTE_REPLACEMENT = "'";

    public static final Long ERROR_WRONG_USERNAME_OR_PASSWORD = 1000L;
    public static final Long ERROR_USER_ALREADY_REGISTERED = 1001L;
    public static final Long ERROR_WRONG_CHANGE_PASSWORD_CODE = 1002L;
    public static final Long ERROR_USER_NOT_FOUND = 1003L;
    public static final Long ERROR_CODE_EXPIRED = 1004L;
    public static final Long UNAUTHORIZED = 1005L;

    public static final Long ERROR_SUBSCRIPTION_FRIEND_REQUESTS_DAILY_LIMIT = 1100L;
    public static final Long ERROR_SUBSCRIPTION_MEETINGS_HISTORY_LIMIT = 1101L;
    public static final Long ERROR_SUBSCRIPTION_SCHEDULED_PLANS_ACTIVE_LIMIT = 1102L;
    public static final Long ERROR_SUBSCRIPTION_LOCATION_PEOPLE = 1103L;

    public static final List<RequestStatus> OUTGOING_FRIEND_REQUEST_STATUSES = of(PENDING, ACCEPTED);
}
