package xyz.hnlxl.dddim.application.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xyz.hnlxl.dddim.application.command.SendChatMessage;
import xyz.hnlxl.dddim.domain.model.ChatParticipant;
import xyz.hnlxl.dddim.domain.model.UserId;
import xyz.hnlxl.dddim.domain.model.chat.Chat;
import xyz.hnlxl.dddim.domain.model.chat.ChatRepo;

/**
 * The application services about chat model.
 * 
 * @author hnlxl at 2022/02/15
 *
 */
@Component
@Transactional
public class ChatService {
  @Autowired
  private ChatRepo chatRepo;

  // TODO declare user repo

  /** Javadoc omitted, see it's Annotation. */
  @EventListener
  @Async
  public void answerSendChatMessage(SendChatMessage command) {
    final UserId senderUserId = new UserId(command.getSenderUserId());
    final UserId targetUserId = new UserId(command.getTargetUserId());
    final String msg = command.getMsg();

    // TODO use user repo to find name
    String senderName = command.getSenderUserId().toString();
    String targetName = command.getTargetUserId().toString();
    ChatParticipant sender = new ChatParticipant(senderUserId, senderName);
    ChatParticipant target = new ChatParticipant(targetUserId, targetName);

    Optional<Chat> optionalChat = chatRepo.findbyParticipant(sender, target);
    Chat chat = null;
    if (optionalChat.isPresent()) {
      chat = optionalChat.get();
      chat.send(sender, msg);
    } else {
      chat = Chat.startOne(sender, target, msg);
    }
    chatRepo.save(chat);
  }
}
