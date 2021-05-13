package idatt2105.erlinssl.ikhovind.fullstackbooking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Section extends BaseModel {
    private String sectionName;

    public Section(String sectionName) {
        this.sectionName = sectionName;
    }
}
