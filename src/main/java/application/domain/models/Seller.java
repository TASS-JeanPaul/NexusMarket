package application.domain.models;

import lombok.Getter;
import lombok.Setter;

import application.domain.enums.Role;

@Getter
@Setter
public class Seller extends User {
    private String businessName;
    private String taxId;
    private Long registeredById;

    public Seller() {
        setRole(Role.SELLER);
    }
}
