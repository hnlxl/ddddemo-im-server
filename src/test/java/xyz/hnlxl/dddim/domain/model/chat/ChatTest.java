package xyz.hnlxl.dddim.domain.model.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.hnlxl.dddim.domain.model.ChatId;
import xyz.hnlxl.dddim.domain.model.ChatParticipant;
import xyz.hnlxl.dddim.domain.model.UserId;

@SpringBootTest
class ChatTest {

  @Autowired
  private ChatRepo chatRepo;

  @Test
  void testNewAggregate() {
    final UUID dummyInitiatorIdVal = UUID.randomUUID();
    final UUID dummyTargetIdVal = UUID.randomUUID();
    final ChatParticipant dummyInitiator =
        new ChatParticipant(new UserId(dummyInitiatorIdVal), "dummyAlpha");
    final ChatParticipant dummyTarget =
        new ChatParticipant(new UserId(dummyTargetIdVal), "dummyBeta");
    final String dummyMessage = "call your for test";

    final ChatId chatId = new ChatId(UUID.randomUUID());
    final String chatIdVal = chatId.getVal().toString().replace("-", "");
    Chat chat = Chat.startOne(chatId, dummyInitiator, dummyTarget, dummyMessage);
    chatRepo.save(chat);

    assertEquals(chatId, chat.getChatId());
    assertEquals("CHAT-" + chatIdVal, chat.eventStreamIdentification().get());
    assertEquals(dummyInitiatorIdVal, chat.getAlpha().getUserId().getVal());
    assertEquals("dummyAlpha", chat.getAlpha().getName());
    assertEquals(dummyTargetIdVal, chat.getBeta().getUserId().getVal());
    assertEquals("dummyBeta", chat.getBeta().getName());
    assertEquals(ChatOnlineState.NONE, chat.getOnlineState());
    assertEquals(BigDecimal.ZERO, chat.getCumulativeBothOnMins());
    assertEquals(0, chat.getCumulativeBothOnCount());
    assertEquals(1, chat.getUnreceivedMsgs().size());
    ChatMsgToBeReceived msg = chat.getUnreceivedMsgs().get(0);
    assertTrue(msg.getSendOn().until(LocalDateTime.now(), ChronoUnit.SECONDS) < 3);
    assertEquals("call your for test", msg.getContent());
    assertEquals(dummyInitiatorIdVal, msg.getSender().getUserId().getVal());
    assertEquals("dummyAlpha", msg.getSender().getName());
    assertEquals(dummyTargetIdVal, msg.getReceiver().getUserId().getVal());
    assertEquals("dummyBeta", msg.getReceiver().getName());
  }

}
