package xyz.hnlxl.dddim.domain.model.chatmsglog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import xyz.hnlxl.dddim.domain.model.ChatId;

/**
 * The repository of ChatMsgLogRepo aggregate.
 * 
 * @author hnlxl at 2022/02/08
 *
 */
public interface ChatMsgLogRepo extends JpaRepository<ChatMsgLog, UUID> {

  List<ChatMsgLog> findTop100ByChatIdAndSendOnAfter(ChatId chatId, LocalDateTime sendOn, Sort sort);

  List<ChatMsgLog> findTop100ByChatIdAndSendOnBefore(ChatId chatId, LocalDateTime sendOn,
      Sort sort);
}
