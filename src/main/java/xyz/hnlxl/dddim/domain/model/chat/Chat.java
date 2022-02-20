package xyz.hnlxl.dddim.domain.model.chat;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
 * One to one chat's aggregate root.
 * 
 * @author hnlxl at 2022/01/26
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
    @AttributeOverride(name = "alpha.userId.val", column = @Column(name = "alpha_user_id")),
    @AttributeOverride(name = "alpha.name", column = @Column(name = "alpha_name")),
    @AttributeOverride(name = "beta.userId.val", column = @Column(name = "beta_user_id")),
    @AttributeOverride(name = "beta.name", column = @Column(name = "beta_name"))
})
public class Chat extends AbstractAggregateRoot<Chat> {

  @EmbeddedId
  ChatId chatId;

  @Override
  protected Optional<String> eventStreamIdentification() {
    return Optional.of("CHAT-" + chatId.getVal().toString().replace("-", ""));
  }

  /**
   * Participant A.
   * 
   * <p>A、B双方对等，为区分将聊条最初的发起者定位A方，另外一个定位B方。
   */
  ChatParticipant alpha;

  /** Participant B. */
  ChatParticipant beta;

  /** Cumulative number of chat messages. */
  Integer cumulativeMsgCount = 0;

  /**
   * Start a new chat.
   * 
   * @param initiator the initiator of this chat, who will be alpha
   * @param target the target of first message, who will be beta
   * @param firstMsg the first message
   * @return a new chat
   */
  public static Chat startOne(ChatParticipant initiator, ChatParticipant target, String firstMsg) {
    ChatId chatId = new ChatId(UUID.randomUUID());
    Chat chat = new Chat().setChatId(chatId).setAlpha(initiator).setBeta(target);
    chat.commonSend(initiator, firstMsg);
    return chat;
  }

  /**
   * Send message by any participant of this chat.
   * 
   * <p>Note: Message sending is always asynchronous throughout the background. Instant doesn't mean
   * synchronous
   */
  public void send(ChatParticipant sender, String msg) {
    commonSend(sender, msg);
  }

  private void commonSend(ChatParticipant sender, String msg) {
    ChatParticipant receiver = sender.equals(getAlpha()) ? getBeta() : getAlpha();
    cumulativeMsgCount++;
    registerEvent(new ChatMsgSent(getChatId(), LocalDateTime.now(), msg, sender, receiver));
  }
}
