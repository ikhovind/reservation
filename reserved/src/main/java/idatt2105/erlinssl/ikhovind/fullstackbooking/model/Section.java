package idatt2105.erlinssl.ikhovind.fullstackbooking.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor
public class Section extends BaseModel {
    private String sectionName;
    private String sectionDesc;

    public Section(String sectionName, String sectionDesc) {
        if(sectionName == null || sectionDesc == null || sectionName.isBlank()) {
            throw new IllegalArgumentException("name or description cannot be null, name cannot be blank");
        }
        this.sectionName = sectionName.trim();
        this.sectionDesc = sectionDesc.trim();
    }

    public void setSectionName(String sectionName) {
        if(sectionName == null || sectionName.isBlank()) {
            throw new IllegalArgumentException("section name cannot be null or blank");
        }
        this.sectionName = sectionName.trim();
    }

    public void setSectionDesc(String sectionDesc) {
        if(sectionName == null) {
            throw new IllegalArgumentException("section desc cannot be null or blank");
        }

        this.sectionDesc = sectionDesc.trim();
    }

    //todo get nullpointer when using contains on the sections of a room?
    @Override
    public boolean equals(Object o) {
        if (o instanceof Section) {
            Section section = (Section) o;
            return section.sectionName.equalsIgnoreCase(this.sectionName) || section.getId().equals(this.getId());
        }
        return false;
    }

    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        json.put("sectionId", getId());
        json.put("sectionName", sectionName);
        json.put("sectionDesc", sectionDesc);
        return json;
    }
}
