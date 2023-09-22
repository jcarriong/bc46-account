package com.nttdata.bc46account.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Ntt Data - Top Employer 2023.
 * Todos los derechos Reservados.
 */
@Getter
@Setter
public class BaseAuditDto implements Serializable {
  private static final long serialVersionUID = -8970945631899794627L;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",
      timezone = "America/Lima")
  @CreatedDate
  private LocalDateTime creationDatetime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",
      timezone = "America/Lima")
  @LastModifiedDate
  private LocalDateTime updateDatetime;
}
