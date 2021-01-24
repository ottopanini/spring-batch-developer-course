package io.me.flatfiles.domain;

import java.util.Date;

public class Customer {
    private final long id;
    private final String firstName;
    private final String lastName;
    private final Date birthdate;

    public Customer(long id, String firstName, String lastName, Date birthdate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getbirthdate() {
        return birthdate;
    }

    public String toString() {
        return "Customer(id=" + this.getId() + ", firstName=" + this.getFirstName() + ", lastName="
                + this.getLastName() + ", birthDate=" + this.getbirthdate() + ")";
    }
}
