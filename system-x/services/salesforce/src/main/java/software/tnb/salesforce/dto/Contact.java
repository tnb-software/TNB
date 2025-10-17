package software.tnb.salesforce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Contact {
    @JsonProperty("LastName")
    private String lastName;
    @JsonProperty("FirstName")
    private String firstName;
    @JsonProperty("Salutation")
    private String salutation;
    @JsonProperty("Email")
    private String email;
    @JsonProperty("Description")
    private String description;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Contact{"
            + "lastName='" + lastName + '\''
            + ", firstName='" + firstName + '\''
            + ", salutation='" + salutation + '\''
            + ", email='" + email + '\''
            + ", description='" + description + '\''
            + '}';
    }
}
