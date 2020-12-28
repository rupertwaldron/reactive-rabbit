package chunky.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto implements Serializable {
    private String name;
    private int age;
    private String city;
    private String creation;

    public PersonDto(int i) {
        this.name = "name_" + i;
        this.age = i;
        this.city = "city_" + i;
    }
}
