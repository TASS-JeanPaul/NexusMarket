package application.data.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import application.data.enums.CommercialStatus;
import application.data.enums.Role;

@Getter
@Setter
public class Buyer extends User {
    private CommercialStatus commercialStatus;
    private List<Address> addresses = new ArrayList<>();

    public Buyer() {
        setRole(Role.BUYER);
    }
}
