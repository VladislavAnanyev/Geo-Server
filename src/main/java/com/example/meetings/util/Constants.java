package com.example.meetings.util;

public class Constants {
    public static final String SIGNIN_EMAIL_SUBJECT = "Код для входа";

    public static final String CONTENT = "Привет! Это чат поддержки. Пиши если возникнут вопросы";
    public static final String DEFAULT_FIRST_NAME = "Анонимный";
    public static final String DEFAULT_LAST_NAME = "Пользователь";
    public static final long ADMIN_ID = 2151L;

    public static final String MODERATION_FIELD_FIRST_NAME = "имя";
    public static final String MODERATION_FIELD_DESCRIPTION = "описание";
    public static final String MODERATION_FIELD_PLAN = "план";

    public static final String MODERATION_PLAN_TEXT_SEPARATOR = "\n\n";

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
}
