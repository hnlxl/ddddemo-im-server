package xyz.hnlxl.dddim.application.command;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The command to send chat message.
 * 
 * @author hnlxl at 2022/02/15
 *
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString()
public class SendChatMessage {
  UUID senderUserId;
  UUID targetUserId;
  String msg;
}
