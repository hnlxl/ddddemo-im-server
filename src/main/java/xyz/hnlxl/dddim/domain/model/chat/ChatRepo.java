package xyz.hnlxl.dddim.domain.model.chat;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xyz.hnlxl.dddim.domain.model.ChatId;
import xyz.hnlxl.dddim.domain.model.ChatParticipant;

/**
 * The repository of Chat aggregate.
 * 
 * @author hnlxl at 2022/02/08
 *
 */
public interface ChatRepo extends JpaRepository<Chat, ChatId> {

  @Query("select c from Chat c "
      + "where (c.alpha = ?1 and c.beta = ?2) or (c.beta = ?1 and c.alpha = ?2)")
  Optional<Chat> findbyParticipant(ChatParticipant one, ChatParticipant other);
}
