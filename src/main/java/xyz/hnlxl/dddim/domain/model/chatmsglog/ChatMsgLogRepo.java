package xyz.hnlxl.dddim.domain.model.chatmsglog;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The repository of ChatMsgLogRepo aggregate.
 * 
 * @author hnlxl at 2022/02/08
 *
 */
public interface ChatMsgLogRepo extends JpaRepository<ChatMsgLog, UUID> {

}
