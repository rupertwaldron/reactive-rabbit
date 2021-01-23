package rabbit.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto implements Serializable {
    @Id
    private String id;
    private String name;
    private int age;
    private String city;
    private String creation;
    private String savation;

    public PersonDto(String name, int age, String city, String creation, String savation) {
        this.name = name;
        this.age = age;
        this.city = city;
        this.creation = creation;
        this.savation = savation;
    }

    public PersonDto(int i) {
        this.name = "name_" + i;
        this.age = i;
        this.city = "city_" + i;
    }
}
