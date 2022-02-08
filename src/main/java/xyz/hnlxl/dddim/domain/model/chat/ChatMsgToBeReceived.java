package xyz.hnlxl.dddim.domain.model.chat;

import java.time.LocalDateTime;
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
import xyz.hnlxl.dddim.domain.model.ChatParticipant;

/**
 * One to one chat's message to be received.
 * 
 * @author hnlxl at 2022/02/06
 *
 */
@Accessors(chain = true)
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
@Entity
@AttributeOverrides({
    @AttributeOverride(name = "sender.userId.val", column = @Column(name = "sender_user_id")),
    @AttributeOverride(name = "sender.name", column = @Column(name = "sender_name")),
    @AttributeOverride(name = "receiver.userId.val", column = @Column(name = "receiver_user_id")),
    @AttributeOverride(name = "receiver.name", column = @Column(name = "receiver_name"))
})
public class ChatMsgToBeReceived {
  /** Surrogate key. */
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @Id
  @GeneratedValue
  private Integer sk;

  /** The time when it is send on. */
  LocalDateTime sendOn;

  /** Text content. */
  @Column(length = 500)
  String content;

  /** The sender. */
  ChatParticipant sender;

  /** The receiver. */
  ChatParticipant receiver;

  /** Constructor on all required fields. */
  public ChatMsgToBeReceived(LocalDateTime sendOn, String content, ChatParticipant sender,
      ChatParticipant receiver) {
    super();
    this.sendOn = sendOn;
    this.content = content;
    this.sender = sender;
    this.receiver = receiver;
  }


}
