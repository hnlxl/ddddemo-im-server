package xyz.hnlxl.dddim.domain.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Immutable;

/**
 * Participant of a chat.
 * 
 * <p>This is a Value Object whose data comes from a user, not a user.
 * 
 * <p>这是一个数据来自于用户的值对象，不是用户。
 * 
 * <p>备注：聊天聚合中的用户，与用户模型，数据之间没有直接关联，而是通过事件做同步，遵循最终一致性。像这样作为值对象的用户还会有很多，为表示区分，他们通常改名为参与者。
 * 
 * @author hnlxl at 2022/01/26
 *
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString()
@EqualsAndHashCode
@Embeddable
@Immutable
public class ChatParticipant implements Serializable {
  private static final long serialVersionUID = 1L;

  protected UserId userId;

  /** The front name, not user name. */
  @Column(name = "participant_name")
  protected String name;
}
