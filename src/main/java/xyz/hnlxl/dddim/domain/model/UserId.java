package xyz.hnlxl.dddim.domain.model;

import java.io.Serializable;
import java.util.UUID;
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
 * User's Identification.
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
public class UserId implements Serializable {
  private static final long serialVersionUID = 1L;

  @Column(name = "user_id")
  protected UUID val;
}
