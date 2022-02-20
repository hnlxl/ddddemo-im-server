package xyz.hnlxl.dddim.domain.model.chat;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.UUID;
import lombok.var;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import xyz.hnlxl.cao.domainbase.AbstractDomainEvent;
import xyz.hnlxl.dddim.domain.model.ChatId;
import xyz.hnlxl.dddim.domain.model.ChatParticipant;
import xyz.hnlxl.dddim.domain.model.UserId;

@SpringBootTest
class ChatTest {

  @Autowired
  private ChatRepo chatRepo;

  @Test
  void testStartOneAndSave() {
    final UUID dummyInitiatorIdVal = UUID.randomUUID();
    final UUID dummyTargetIdVal = UUID.randomUUID();
    final ChatParticipant dummyInitiator =
        new ChatParticipant(new UserId(dummyInitiatorIdVal), "dummyAlpha");
    final ChatParticipant dummyTarget =
        new ChatParticipant(new UserId(dummyTargetIdVal), "dummyBeta");
    final String dummyMessage = "call your for test";


    Chat chat = Chat.startOne(dummyInitiator, dummyTarget, dummyMessage);
    final ChatId chatId = chat.getChatId();
    assertNotNull(chatId);

    Collection<AbstractDomainEvent> capturedEvents =
        ReflectionTestUtils.invokeMethod(chat, "domainEvents");
    assertEquals(1, capturedEvents.size());
    AbstractDomainEvent theOnlyCapturedEvent = capturedEvents.iterator().next();
    assertThat(theOnlyCapturedEvent, instanceOf(ChatMsgSent.class));
    final ChatMsgSent capturedChatMsgSent = (ChatMsgSent) theOnlyCapturedEvent;

    chatRepo.save(chat);


    assertEquals("CHAT-" + chatId.getVal().toString().replace("-", ""),
        chat.eventStreamIdentification().get());
    assertEquals(1, chat.getCumulativeMsgCount());
    assertEquals(dummyInitiatorIdVal, chat.getAlpha().getUserId().getVal());
    assertEquals("dummyAlpha", chat.getAlpha().getName());
    assertEquals(dummyTargetIdVal, chat.getBeta().getUserId().getVal());
    assertEquals("dummyBeta", chat.getBeta().getName());
    assertEquals(chat.getChatId(), capturedChatMsgSent.getChatId());
    assertTrue(capturedChatMsgSent.getSendOn().until(LocalDateTime.now(), ChronoUnit.SECONDS) < 3);
    assertEquals("call your for test", capturedChatMsgSent.getContent());
    assertEquals(dummyInitiatorIdVal, capturedChatMsgSent.getSender().getUserId().getVal());
    assertEquals("dummyAlpha", capturedChatMsgSent.getSender().getName());
    assertEquals(dummyTargetIdVal, capturedChatMsgSent.getReceiver().getUserId().getVal());
    assertEquals("dummyBeta", capturedChatMsgSent.getReceiver().getName());
  }

  @Test
  void testSend() {
    final ChatId dummyChatId = new ChatId(UUID.randomUUID());
    final ChatParticipant dummyAlpha =
        new ChatParticipant(new UserId(UUID.randomUUID()), "dummyAlpha");
    final ChatParticipant dummyBeta =
        new ChatParticipant(new UserId(UUID.randomUUID()), "dummyBeta");
    final Integer dummyCumulativeMsgCount = 111;
    final String dummyMessageAlphaToBeta = "alpha call beta";
    final String dummyMessageBetaToAlpha = "beta call alpha";
    Chat chat = null;
    try {
      Constructor<Chat> chatConstructor = Chat.class.getDeclaredConstructor();
      chatConstructor.setAccessible(true);
      chat = chatConstructor.newInstance();
    } catch (Exception e) {
      fail(e);
    }
    chat.setChatId(dummyChatId).setAlpha(dummyAlpha).setBeta(dummyBeta)
        .setCumulativeMsgCount(dummyCumulativeMsgCount);


    chat.send(dummyAlpha, dummyMessageAlphaToBeta);
    chat.send(dummyBeta, dummyMessageBetaToAlpha);


    assertEquals(dummyCumulativeMsgCount + 2, chat.getCumulativeMsgCount());
    Collection<AbstractDomainEvent> capturedEvents =
        ReflectionTestUtils.invokeMethod(chat, "domainEvents");
    assertEquals(2, capturedEvents.size());
    var iterator = capturedEvents.iterator();
    AbstractDomainEvent capturedEventOne = iterator.next();
    assertThat(capturedEventOne, instanceOf(ChatMsgSent.class));
    final ChatMsgSent capturedChatMsgSentOne = (ChatMsgSent) capturedEventOne;
    AbstractDomainEvent capturedEventTwo = iterator.next();
    assertThat(capturedEventTwo, instanceOf(ChatMsgSent.class));
    final ChatMsgSent capturedChatMsgSentTwo = (ChatMsgSent) capturedEventTwo;

    assertEquals(dummyChatId, capturedChatMsgSentOne.getChatId());
    assertTrue(
        capturedChatMsgSentOne.getSendOn().until(LocalDateTime.now(), ChronoUnit.SECONDS) < 3);
    assertEquals(dummyMessageAlphaToBeta, capturedChatMsgSentOne.getContent());
    assertEquals(dummyAlpha, capturedChatMsgSentOne.getSender());
    assertEquals(dummyBeta, capturedChatMsgSentOne.getReceiver());
    assertEquals(dummyChatId, capturedChatMsgSentTwo.getChatId());
    assertTrue(
        capturedChatMsgSentTwo.getSendOn().until(LocalDateTime.now(), ChronoUnit.SECONDS) < 3);
    assertEquals(dummyMessageBetaToAlpha, capturedChatMsgSentTwo.getContent());
    assertEquals(dummyBeta, capturedChatMsgSentTwo.getSender());
    assertEquals(dummyAlpha, capturedChatMsgSentTwo.getReceiver());
    assertTrue(capturedChatMsgSentOne.getSendOn()
        .until(capturedChatMsgSentTwo.getSendOn(), ChronoUnit.MILLIS) <= 0);
  }

}
