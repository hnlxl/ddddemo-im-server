package xyz.hnlxl.dddim.domain.model.chatmsglog;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import xyz.hnlxl.cao.domainbase.AbstractAggregateRoot;
import xyz.hnlxl.dddim.domain.model.ChatId;
import xyz.hnlxl.dddim.domain.model.ChatParticipant;

/**
 * Message log of one to one chat.
 * 
 * @author hnlxl at 2022/02/06
 *
 */
@Accessors(chain = true)
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode(callSuper = false)
@Entity
@AttributeOverrides({
    @AttributeOverride(name = "sender.userId.val", column = @Column(name = "sender_user_id")),
    @AttributeOverride(name = "sender.name", column = @Column(name = "sender_name")),
    @AttributeOverride(name = "receiver.userId.val", column = @Column(name = "receiver_user_id")),
    @AttributeOverride(name = "receiver.name", column = @Column(name = "receiver_name"))
})
public class ChatMsgLog extends AbstractAggregateRoot<ChatMsgLog> {
  @Override
  protected Optional<String> eventStreamIdentification() {
    return Optional.empty();
  }

  @Id
  @GeneratedValue
  UUID chatMessageId;

  /** Indicates the chat it belongs to. */
  ChatId chatId;

  /**
   * The time when it is send on.
   * 
   * <p>Together with chat/chatId, it forms one of the natural keys.
   */
  LocalDateTime sendOn;

  /** Text content. */
  @Column(length = 500)
  String content;

  /** The sender. */
  ChatParticipant sender;

  /** The receiver. */
  ChatParticipant receiver;

  /** The time when it is receive on. */
  LocalDateTime receiveOn;

  /** Constructor on all required fields. */
  public ChatMsgLog(ChatId chatId, LocalDateTime sendOn, String content, ChatParticipant sender,
      ChatParticipant receiver, LocalDateTime receiveOn) {
    super();
    this.chatId = chatId;
    this.sendOn = sendOn;
    this.content = content;
    this.sender = sender;
    this.receiver = receiver;
    this.receiveOn = receiveOn;
  }
}
