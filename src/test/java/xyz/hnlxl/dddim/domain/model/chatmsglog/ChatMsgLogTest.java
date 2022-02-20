package xyz.hnlxl.dddim.domain.model.chatmsglog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.hnlxl.dddim.domain.model.ChatId;
import xyz.hnlxl.dddim.domain.model.ChatParticipant;
import xyz.hnlxl.dddim.domain.model.UserId;

@SpringBootTest
class ChatMsgLogTest {

  @Autowired
  private ChatMsgLogRepo chatMsgLogRepo;

  @Test
  void testNewAggregate() {
    final ChatId dummyChatId = new ChatId(UUID.randomUUID());
    final LocalDateTime dummySendon = LocalDateTime.now().minusSeconds(50L);
    final ChatParticipant dummySender =
        new ChatParticipant(new UserId(UUID.randomUUID()), "Oliver");
    final ChatParticipant dummyReceiver =
        new ChatParticipant(new UserId(UUID.randomUUID()), "Frank");
    final String dummyContent = "Oliver call Frank";

    ChatMsgLog chatMsgLog = new ChatMsgLog(dummyChatId, dummySendon, dummyContent, dummySender,
        dummyReceiver);
    chatMsgLogRepo.save(chatMsgLog);

    assertNotNull(chatMsgLog.getChatMessageId());
    assertFalse(chatMsgLog.eventStreamIdentification().isPresent());
    assertEquals(dummyChatId, chatMsgLog.getChatId());
    assertEquals(dummySendon, chatMsgLog.getSendOn());
    assertEquals(dummyContent, chatMsgLog.getContent());
    assertEquals(dummySender, chatMsgLog.getSender());
    assertEquals(dummyReceiver, chatMsgLog.getReceiver());
  }

}
