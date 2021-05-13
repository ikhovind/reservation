package idatt2105.erlinssl.ikhovind.fullstackbooking.model;

import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
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
    @GeneratedValue
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id = Generators.randomBasedGenerator().generate();
    @CreatedDate
    @Column(name = "created_date")
    private Timestamp createdDate;
}
