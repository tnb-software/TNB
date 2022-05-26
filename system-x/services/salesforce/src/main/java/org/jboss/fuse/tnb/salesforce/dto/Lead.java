package org.jboss.fuse.tnb.salesforce.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SF lead DTO.
 * <p>
 * Nov 2, 2017 Red Hat
 *
 * @author tplevko@redhat.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lead {

    @JsonProperty(value = "Id")
    private String id;
    @JsonProperty(value = "FirstName")
    private String firstName;
    @JsonProperty(value = "LastName")
    private String lastName;
    @JsonProperty(value = "Email")
    private String email;
    @JsonProperty(value = "Company")
    private String company;

    public Lead(String firstName, String lastName, String email, String company) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.company = company;
    }

    public Lead() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "Lead{"
            + "id='" + id + '\''
            + ", firstName='" + firstName + '\''
            + ", lastName='" + lastName + '\''
            + ", email='" + email + '\''
            + ", company='" + company + '\''
            + '}';
    }

    public String toStringJSON() {
        return "{"
            + "\"firstName\":\"" + firstName + "\""
            + ", \"lastName\":\"" + lastName + "\""
            + ", \"email\":\"" + email + "\""
            + ", \"company\":\"" + company + "\""
            + "}";
    }

    public String toStringXml() {
        return "<Lead>"
            + "<FirstName>" + firstName + "</FirstName>"
            + "<LastName>" + lastName + "</LastName>"
            + "<Email>" + email + "</Email>"
            + "<Company>" + company + "</Company>"
            + "</Lead>";
    }
}
