package xyz.hnlxl.dddim.domain.model.chat;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import xyz.hnlxl.cao.domainbase.AbstractDomainEvent;
import xyz.hnlxl.dddim.domain.model.ChatId;
import xyz.hnlxl.dddim.domain.model.ChatParticipant;

/**
 * 
 * @author hnlxl at 2022/02/15
 *
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString()
@EqualsAndHashCode(callSuper = false)
public class ChatMsgSent extends AbstractDomainEvent {
  ChatId chatId;
  LocalDateTime sendOn;
  String content;
  ChatParticipant sender;
  ChatParticipant receiver;
}
