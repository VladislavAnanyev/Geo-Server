package com.example.meetings.chat.facade;

import com.example.meetings.chat.model.*;
import com.example.meetings.chat.model.dto.output.*;

import java.io.InputStream;

/**
 * Фасад сценариев API чата: сообщения, диалоги и вложения.
 */
public interface MessageFacade {

    /**
     * Сохраняет сообщение, рассылает событие участникам диалога и push-уведомление.
     *
     * @param sendMessageModel параметры отправляемого сообщения
     */
    void sendMessage(SendMessageModel sendMessageModel);

    /**
     * Уведомляет участников диалога о наборе текста пользователем.
     *
     * @param dialogId идентификатор диалога
     * @param userId   идентификатор пользователя, который набирает сообщение
     */
    void typingMessage(Long dialogId, Long userId);

    /**
     * Помечает сообщение удалённым и рассылает событие участникам диалога.
     *
     * @param messageId идентификатор сообщения
     * @param userId    идентификатор пользователя, выполняющего удаление
     */
    void deleteMessage(Long messageId, Long userId);

    /**
     * Изменяет текст сообщения и рассылает событие участникам диалога.
     *
     * @param messageId идентификатор сообщения
     * @param content   новый текст сообщения
     * @param userId    идентификатор пользователя, выполняющего редактирование
     */
    void editMessage(Long messageId, String content, Long userId);

    /**
     * Возвращает диалог с постраничным списком сообщений и помечает их прочитанными.
     *
     * @param dialogId идентификатор диалога
     * @param page     номер страницы сообщений
     * @param pageSize размер страницы
     * @param sortBy   поле и направление сортировки сообщений
     * @param userId   идентификатор текущего пользователя
     * @return представление диалога с сообщениями
     */
    DialogDTO getChatRoom(Long dialogId, Integer page, Integer pageSize, String sortBy, Long userId);

    /**
     * Возвращает страницу последних диалогов пользователя.
     *
     * @param userId   идентификатор текущего пользователя
     * @param page     номер страницы
     * @param pageSize размер страницы
     * @return результат со списком диалогов
     */
    GetDialogsResult getLastDialogs(Long userId, Integer page, Integer pageSize);

    /**
     * Создаёт личный диалог между двумя пользователями.
     *
     * @param firstUserId  идентификатор первого участника
     * @param secondUserId идентификатор второго участника
     * @return результат с данными созданного или существующего диалога
     */
    CreateDialogResult createDialog(Long firstUserId, Long secondUserId);

    /**
     * Возвращает или создаёт диалог поддержки между пользователем и администратором.
     *
     * @param authUserId идентификатор текущего пользователя
     * @return результат с данными диалога поддержки
     */
    CreateDialogResult getSupportDialog(Long authUserId);

    /**
     * Помечает входящие сообщения диалога доставленными и уведомляет отправителей.
     *
     * @param userId   идентификатор текущего пользователя
     * @param dialogId идентификатор диалога
     */
    void receiveMessages(Long userId, Long dialogId);

    /**
     * Помечает сообщения диалога прочитанными и уведомляет других участников.
     *
     * @param userId   идентификатор текущего пользователя
     * @param dialogId идентификатор диалога
     */
    void readMessages(Long userId, Long dialogId);

    /**
     * Возвращает вложения диалога.
     *
     * @param id идентификатор диалога
     * @return результат со списком вложений
     */
    GetDialogAttachmentsResult getAttachments(Long id);

    /**
     * Сохраняет файл вложения во внешнем хранилище.
     *
     * @param inputStream      поток данных файла
     * @param originalFilename исходное имя файла
     * @param contentType      MIME-тип содержимого
     * @return результат с URI сохранённого файла
     */
    UploadAttachmentResult store(InputStream inputStream, String originalFilename, String contentType);
}
