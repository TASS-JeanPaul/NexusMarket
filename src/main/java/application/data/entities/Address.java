package application.data.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    private Long id;
    private Long buyerId;
    private String street;
    private String city;
    private String state;
    private String notes;
    private boolean primary;
}
