package xyz.hnlxl.dddim.domain.model.chat;

/**
 * The online state of the participant of chat.
 * 
 * @author hnlxl at 2022/01/26
 *
 */
public enum ChatOnlineState {
  /** Both offline. */
  NONE,
  /** Only alpha is online */
  A,
  /** Only beta is online */
  B,
  /** Both online */
  ALL
}
