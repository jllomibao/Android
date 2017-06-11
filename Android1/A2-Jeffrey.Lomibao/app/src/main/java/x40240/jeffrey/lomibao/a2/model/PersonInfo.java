package x40240.jeffrey.lomibao.a2.model;

import java.io.Serializable;
import java.util.ArrayList;

public final class PersonInfo
    implements Serializable
{
    private static final long serialVersionUID = 3202578332355446282L;

    public enum Gender { UNKNOWN, MALE, FEMALE }
    public enum Education { HIGHSCHOOL, ASSOCIATE, BACHELOR, MASTER, DOCTORATE }
    public enum Pets { DOG, CAT, BIRD, FISH }

    private Gender gender;
    private String firstname;
    private String lastname;
    private Education education;
    public ArrayList<Pets> pets = new ArrayList<>();

    /**
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname
     *            the firstname to set
     */
    public void setFirstname (String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return the lastname
     */
    public String getLastname () {
        return lastname;
    }

    /**
     * @param lastname
     *            the lastname to set
     */
    public void setLastname (String lastname) {
        this.lastname = lastname;
    }

    /**
     * @return the gender
     */
    public Gender getGender () {
        return gender;
    }

    /**
     * @param gender
     *            the gender to set
     */
    public void setGender (Gender gender) {
        this.gender = gender;
    }

    public Education getEducation () {
        return education;
    }

    public void setEducation (Education education) {
        this.education = education;
    }

    public void addPet(Pets pet) {
        pets.add(pet);
    }

    public void removePet(Pets pet) {
        pets.remove(pet);
    }

    public void clearPets() {
        pets.clear();
    }

}
