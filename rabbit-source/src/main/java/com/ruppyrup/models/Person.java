package com.ruppyrup.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person implements Serializable {
    private String name;
    private int age;
    private String city;

    public Person(int i) {
        this.name = "name_" + i;
        this.age = i;
        this.city = "city_" + i;
    }
}
