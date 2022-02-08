package xyz.hnlxl.dddim.domain.model.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.hnlxl.dddim.domain.model.ChatId;

/**
 * The repository of Chat aggregate.
 * 
 * @author hnlxl at 2022/02/08
 *
 */
public interface ChatRepo extends JpaRepository<Chat, ChatId> {

}
