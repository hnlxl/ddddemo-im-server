package xyz.hnlxl.dddim.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import xyz.hnlxl.dddim.domain.model.ChatId;
import xyz.hnlxl.dddim.domain.model.chat.ChatMsgSent;
import xyz.hnlxl.dddim.domain.model.chatmsglog.ChatMsgLog;
import xyz.hnlxl.dddim.domain.model.chatmsglog.ChatMsgLogRepo;
import xyz.hnlxl.dddim.domain.model.chatmsglog.ChatMsgLog_;

/**
 * The application services about chat message log model.
 * 
 * @author hnlxl at 2022/02/15
 *
 */
@Component
@Transactional
public class ChatMsgLogService {
  @Autowired
  private ChatMsgLogRepo chatMsgLogRepo;

  /** Javadoc omitted, see it's Annotation. */
  @TransactionalEventListener
  @Async
  public void listenChatMsgSent(ChatMsgSent event) {
    chatMsgLogRepo.save(
        new ChatMsgLog(
            event.getChatId(),
            event.getSendOn(),
            event.getContent(),
            event.getSender(),
            event.getReceiver()));
  }

  /**
   * Find up to 100 messages after the base time.
   */
  @Async
  public CompletableFuture<List<ChatMsgLog>> findNextMsgs(ChatId chatId, LocalDateTime baseTime) {
    return CompletableFuture.completedFuture(
        chatMsgLogRepo.findTop100ByChatIdAndSendOnAfter(chatId, baseTime,
            Sort.by(Order.asc(ChatMsgLog_.SEND_ON))));
  }

  /**
   * Find up to 100 messages before the base time.
   */
  public CompletableFuture<List<ChatMsgLog>> findPreviousMsgs(ChatId chatId,
      LocalDateTime baseTime) {
    return CompletableFuture.completedFuture(
        chatMsgLogRepo.findTop100ByChatIdAndSendOnBefore(chatId, baseTime,
            Sort.by(Order.asc(ChatMsgLog_.SEND_ON))));
  }
}
