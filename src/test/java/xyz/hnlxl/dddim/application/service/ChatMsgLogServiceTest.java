package xyz.hnlxl.dddim.application.service;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.transaction.Transactional;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.test.context.transaction.TestTransaction;
import xyz.hnlxl.dddim.domain.model.ChatId;
import xyz.hnlxl.dddim.domain.model.ChatParticipant;
import xyz.hnlxl.dddim.domain.model.UserId;
import xyz.hnlxl.dddim.domain.model.chat.ChatMsgSent;
import xyz.hnlxl.dddim.domain.model.chatmsglog.ChatMsgLog;
import xyz.hnlxl.dddim.domain.model.chatmsglog.ChatMsgLogRepo;

@SpringBootTest
class ChatMsgLogServiceTest {
  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;
  @Autowired
  private ChatMsgLogRepo chatMsgLogRepo;
  @Autowired
  private ChatMsgLogService chatMsgLogService;

  private ChatId refChatId = null;
  private LocalDateTime refSendOn = null;
  private String refContentFirst = null;
  private String refContentSecond = null;
  private String refContentThird = null;
  private String refContentFour = null;
  private String refContentFive = null;
  private ChatParticipant refSender = null;
  private ChatParticipant refReceiver = null;

  @Test
  @Transactional
  void testListenChatMsgSent() {
    final ChatId dummyChatId = new ChatId(UUID.randomUUID());
    final LocalDateTime dummySendon = LocalDateTime.now().minusSeconds(5L);
    final ChatParticipant dummySender = new ChatParticipant(new UserId(UUID.randomUUID()), "A");
    final ChatParticipant dummyReceiver = new ChatParticipant(new UserId(UUID.randomUUID()), "B");
    final String dummyContent = "A call B";
    final Example<ChatMsgLog> refExample = Example.of(
        new ChatMsgLog(dummyChatId, dummySendon, dummyContent, dummySender, dummyReceiver));

    applicationEventPublisher.publishEvent(
        new ChatMsgSent(dummyChatId, dummySendon, dummyContent, dummySender, dummyReceiver));
    TestTransaction.flagForCommit();
    TestTransaction.end();

    Awaitility.await().atMost(Duration.ofSeconds(1L))
        .until(() -> chatMsgLogRepo.findOne(refExample).isPresent());

    chatMsgLogRepo.findOne(refExample).ifPresent(savedEntity -> chatMsgLogRepo.delete(savedEntity));
  }

  @Test
  @DisplayName("testListenChatMsgSent - transaction is not over")
  @Transactional
  void testListenChatMsgSent_notover() throws InterruptedException {
    final ChatId dummyChatId = new ChatId(UUID.randomUUID());
    final LocalDateTime dummySendon = LocalDateTime.now().minusSeconds(15L);
    final ChatParticipant dummySender = new ChatParticipant(new UserId(UUID.randomUUID()), "Aa");
    final ChatParticipant dummyReceiver = new ChatParticipant(new UserId(UUID.randomUUID()), "Bb");
    final String dummyContent = "Aa call Bb";
    final Example<ChatMsgLog> refExample = Example.of(
        new ChatMsgLog(dummyChatId, dummySendon, dummyContent, dummySender, dummyReceiver));

    applicationEventPublisher.publishEvent(
        new ChatMsgSent(dummyChatId, dummySendon, dummyContent, dummySender, dummyReceiver));

    Thread.sleep(2000);
    assertFalse(chatMsgLogRepo.findOne(refExample).isPresent());
  }

  @Test
  @DisplayName("testListenChatMsgSent - not transactional")
  void testListenChatMsgSent_notTransactional() throws InterruptedException {
    final ChatId dummyChatId = new ChatId(UUID.randomUUID());
    final LocalDateTime dummySendon = LocalDateTime.now().minusSeconds(15L);
    final ChatParticipant dummySender = new ChatParticipant(new UserId(UUID.randomUUID()), "Aa");
    final ChatParticipant dummyReceiver = new ChatParticipant(new UserId(UUID.randomUUID()), "Bb");
    final String dummyContent = "Aa call Bb";
    final Example<ChatMsgLog> refExample = Example.of(
        new ChatMsgLog(dummyChatId, dummySendon, dummyContent, dummySender, dummyReceiver));

    applicationEventPublisher.publishEvent(
        new ChatMsgSent(dummyChatId, dummySendon, dummyContent, dummySender, dummyReceiver));

    Thread.sleep(2000);
    assertFalse(chatMsgLogRepo.findOne(refExample).isPresent());
  }

  @Test
  @DisplayName("testListenChatMsgSent - transaction end with rollback")
  @Transactional
  void testListenChatMsgSent_roolback() throws InterruptedException {
    final ChatId dummyChatId = new ChatId(UUID.randomUUID());
    final LocalDateTime dummySendon = LocalDateTime.now().minusSeconds(15L);
    final ChatParticipant dummySender = new ChatParticipant(new UserId(UUID.randomUUID()), "Aa");
    final ChatParticipant dummyReceiver = new ChatParticipant(new UserId(UUID.randomUUID()), "Bb");
    final String dummyContent = "Aa call Bb";
    final Example<ChatMsgLog> refExample = Example.of(
        new ChatMsgLog(dummyChatId, dummySendon, dummyContent, dummySender, dummyReceiver));

    applicationEventPublisher.publishEvent(
        new ChatMsgSent(dummyChatId, dummySendon, dummyContent, dummySender, dummyReceiver));
    TestTransaction.flagForRollback();
    TestTransaction.end();

    Thread.sleep(2000);
    assertFalse(chatMsgLogRepo.findOne(refExample).isPresent());
  }

  @Test
  void testFindNextMsgs() {
    prepareDataForQuery();
    final AtomicBoolean finded = new AtomicBoolean(false);

    chatMsgLogService.findNextMsgs(refChatId, refSendOn)
        .thenApplyAsync(list -> {
          if (list.size() != 2) {
            return false;
          }
          ChatMsgLog log1 = list.get(0);
          if (!Objects.equals(refChatId, log1.getChatId())
              || !Objects.equals(refSendOn.plusSeconds(2), log1.getSendOn())
              || !Objects.equals(refContentFour, log1.getContent())
              || !Objects.equals(refSender, log1.getSender())
              || !Objects.equals(refReceiver, log1.getReceiver())) {
            return false;
          }
          ChatMsgLog log2 = list.get(1);
          if (!Objects.equals(refChatId, log2.getChatId())
              || !Objects.equals(refSendOn.plusSeconds(4), log2.getSendOn())
              || !Objects.equals(refContentFive, log2.getContent())
              || !Objects.equals(refSender, log2.getSender())
              || !Objects.equals(refReceiver, log2.getReceiver())) {
            return false;
          }
          return true;
        })
        .thenApply(isExpected -> {
          if (isExpected) {
            finded.set(true);
          }
          return null;
        });

    Awaitility.await().atMost(Duration.ofSeconds(1L)).untilTrue(finded);
  }

  @Test
  void testFindPreviousMsgs() {
    prepareDataForQuery();
    final AtomicBoolean finded = new AtomicBoolean(false);

    chatMsgLogService.findPreviousMsgs(refChatId, refSendOn)
        .thenApplyAsync(list -> {
          if (list.size() != 2) {
            return false;
          }
          ChatMsgLog log1 = list.get(0);
          if (!Objects.equals(refChatId, log1.getChatId())
              || !Objects.equals(refSendOn.minusSeconds(4), log1.getSendOn())
              || !Objects.equals(refContentFirst, log1.getContent())
              || !Objects.equals(refSender, log1.getSender())
              || !Objects.equals(refReceiver, log1.getReceiver())) {
            return false;
          }
          ChatMsgLog log2 = list.get(1);
          if (!Objects.equals(refChatId, log2.getChatId())
              || !Objects.equals(refSendOn.minusSeconds(2), log2.getSendOn())
              || !Objects.equals(refContentSecond, log2.getContent())
              || !Objects.equals(refSender, log2.getSender())
              || !Objects.equals(refReceiver, log2.getReceiver())) {
            return false;
          }
          return true;
        })
        .thenApply(isExpected -> {
          if (isExpected) {
            finded.set(true);
          }
          return null;
        });
    Awaitility.await().atMost(Duration.ofSeconds(1L)).untilTrue(finded);
  }

  private final void prepareDataForQuery() {
    refChatId = new ChatId(UUID.randomUUID());
    refSendOn = LocalDateTime.now().plusSeconds(100);
    refContentFirst = "-4 sended";
    refContentSecond = "-2 sended";
    refContentThird = "0 sended";
    refContentFour = "+2 sended";
    refContentFive = "+4 sended";
    refSender = new ChatParticipant(new UserId(UUID.randomUUID()), "refSender");
    refReceiver = new ChatParticipant(new UserId(UUID.randomUUID()), "refReceiver");
    final ChatId disturbingChatId = new ChatId(UUID.randomUUID());
    List<ChatMsgLog> listToSave = new ArrayList<>();
    listToSave.add(new ChatMsgLog(refChatId, refSendOn.minusSeconds(4), refContentFirst, refSender,
        refReceiver));
    listToSave.add(new ChatMsgLog(refChatId, refSendOn.minusSeconds(2), refContentSecond, refSender,
        refReceiver));
    listToSave.add(new ChatMsgLog(refChatId, refSendOn, refContentThird, refSender,
        refReceiver));
    listToSave.add(new ChatMsgLog(refChatId, refSendOn.plusSeconds(2), refContentFour, refSender,
        refReceiver));
    listToSave.add(new ChatMsgLog(refChatId, refSendOn.plusSeconds(4), refContentFive, refSender,
        refReceiver));
    listToSave
        .add(new ChatMsgLog(disturbingChatId, refSendOn.minusSeconds(4), refContentFirst, refSender,
            refReceiver));
    listToSave
        .add(new ChatMsgLog(disturbingChatId, refSendOn.plusSeconds(4), refContentFirst, refSender,
            refReceiver));
    chatMsgLogRepo.saveAll(listToSave);
  }
}
