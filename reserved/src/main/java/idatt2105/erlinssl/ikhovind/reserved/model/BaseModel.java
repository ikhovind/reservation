package idatt2105.erlinssl.ikhovind.reserved.model;

import com.fasterxml.uuid.Generators;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@SuperBuilder
public class BaseModel {
    @Id
    private UUID uid = Generators.timeBasedGenerator().generate();

    @CreatedDate
    @Column(name = "created_date")
    private Timestamp createdDate;
}
