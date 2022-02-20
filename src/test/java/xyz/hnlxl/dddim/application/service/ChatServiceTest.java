package xyz.hnlxl.dddim.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import xyz.hnlxl.dddim.application.command.SendChatMessage;
import xyz.hnlxl.dddim.domain.model.ChatId;
import xyz.hnlxl.dddim.domain.model.ChatParticipant;
import xyz.hnlxl.dddim.domain.model.UserId;
import xyz.hnlxl.dddim.domain.model.chat.Chat;
import xyz.hnlxl.dddim.domain.model.chat.ChatMsgSent;
import xyz.hnlxl.dddim.domain.model.chat.ChatRepo;


@SpringBootTest
@RecordApplicationEvents
class ChatServiceTest {
  // Because using asynchronous commands for communicating from user interface to core area,
  // usually the application service is the highest level of one function.
  // Therefore, make full layer test for application service.

  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;
  @Autowired
  private ApplicationEvents applicationEvents;
  @Autowired
  private ChatRepo chatRepo;

  @Test
  @DisplayName("testAnswerSendChatMessage - Chat exists (sender is alpha)")
  void testAnswerSendChatMessage_1() {
    final UUID dummyAlphaUserIdVal = UUID.randomUUID();
    final UUID dummyBetaUserIdVal = UUID.randomUUID();
    final String dummyMsg = "testAnswerSendChatMessage - Chat exists (sender is alpha)";
    final ChatParticipant referencedAlpha =
        new ChatParticipant(new UserId(dummyAlphaUserIdVal), dummyAlphaUserIdVal.toString());
    final ChatParticipant referencedBeta =
        new ChatParticipant(new UserId(dummyBetaUserIdVal), dummyBetaUserIdVal.toString());
    final ChatId dummyChatId =
        chatRepo.save(Chat.startOne(referencedAlpha, referencedBeta, "dummy chat")).getChatId();

    applicationEventPublisher
        .publishEvent(new SendChatMessage(dummyAlphaUserIdVal, dummyBetaUserIdVal, dummyMsg));
    Awaitility.await().atMost(Duration.ofSeconds(1L)).until(() -> {
      return chatRepo.findById(dummyChatId)
          .filter((chat) -> chat.getCumulativeMsgCount() > 1).isPresent();
    });

    Chat chat = chatRepo.findById(dummyChatId).get();
    assertEquals(2, chat.getCumulativeMsgCount());
    assertEquals(1, applicationEvents.stream(ChatMsgSent.class)
        .filter((event) -> {
          return Objects.equals(dummyChatId, event.getChatId())
              && event.getSendOn().until(LocalDateTime.now(), ChronoUnit.SECONDS) < 2
              && Objects.equals(dummyMsg, event.getContent())
              && Objects.equals(referencedAlpha, event.getSender())
              && Objects.equals(referencedBeta, event.getReceiver());
        })
        .count());
  }

  @Test
  @DisplayName("testAnswerSendChatMessage - Chat not exists")
  void testAnswerSendChatMessage_2() {
    final UUID dummySenderUserIdVal = UUID.randomUUID();
    final UUID dummyTargetUserIdVal = UUID.randomUUID();
    final String dummyMsg = "testAnswerSendChatMessage - Chat not exists";
    final ChatParticipant referencedSender =
        new ChatParticipant(new UserId(dummySenderUserIdVal), dummySenderUserIdVal.toString());
    final ChatParticipant referencedTarget =
        new ChatParticipant(new UserId(dummyTargetUserIdVal), dummyTargetUserIdVal.toString());

    applicationEventPublisher
        .publishEvent(new SendChatMessage(dummySenderUserIdVal, dummyTargetUserIdVal, dummyMsg));
    Awaitility.await().atMost(Duration.ofSeconds(1L))
        .until(() -> chatRepo.findbyParticipant(referencedSender, referencedTarget).isPresent());

    Optional<Chat> optionalChat = chatRepo.findbyParticipant(referencedSender, referencedTarget);
    assertTrue(optionalChat.isPresent());
    assertEquals(referencedSender, optionalChat.get().getAlpha());
    assertEquals(referencedTarget, optionalChat.get().getBeta());
    assertEquals(1, optionalChat.get().getCumulativeMsgCount());
    assertEquals(1, applicationEvents.stream(ChatMsgSent.class)
        .filter((event) -> {
          return Objects.equals(optionalChat.get().getChatId(), event.getChatId())
              && event.getSendOn().until(LocalDateTime.now(), ChronoUnit.SECONDS) < 2
              && Objects.equals(dummyMsg, event.getContent())
              && Objects.equals(referencedSender, event.getSender())
              && Objects.equals(referencedTarget, event.getReceiver());
        })
        .count());
  }

  @Test
  @DisplayName("testAnswerSendChatMessage - sender is beta")
  void testAnswerSendChatMessage_3() {
    final UUID dummyAlphaUserIdVal = UUID.randomUUID();
    final UUID dummyBetaUserIdVal = UUID.randomUUID();
    final String dummyMsg = "testAnswerSendChatMessage - sender is beta";
    final ChatParticipant referencedAlpha =
        new ChatParticipant(new UserId(dummyAlphaUserIdVal), dummyAlphaUserIdVal.toString());
    final ChatParticipant referencedBeta =
        new ChatParticipant(new UserId(dummyBetaUserIdVal), dummyBetaUserIdVal.toString());
    final ChatId dummyChatId =
        chatRepo.save(Chat.startOne(referencedAlpha, referencedBeta, "dummy chat")).getChatId();

    applicationEventPublisher
        .publishEvent(new SendChatMessage(dummyBetaUserIdVal, dummyAlphaUserIdVal, dummyMsg));
    Awaitility.await().atMost(Duration.ofSeconds(1L)).until(() -> {
      return chatRepo.findById(dummyChatId)
          .filter((chat) -> chat.getCumulativeMsgCount() > 1).isPresent();
    });

    Chat chat = chatRepo.findById(dummyChatId).get();
    assertEquals(2, chat.getCumulativeMsgCount());
    assertEquals(1, applicationEvents.stream(ChatMsgSent.class)
        .filter((event) -> {
          return Objects.equals(dummyChatId, event.getChatId())
              && event.getSendOn().until(LocalDateTime.now(), ChronoUnit.SECONDS) < 2
              && Objects.equals(dummyMsg, event.getContent())
              && Objects.equals(referencedBeta, event.getSender())
              && Objects.equals(referencedAlpha, event.getReceiver());
        })
        .count());
  }
}
