package x40240.jeffrey.lomibao.a3.model;

import android.content.Context;
import android.content.res.Resources;

import java.io.Serializable;
import java.util.ArrayList;

import x40240.jeffrey.lomibao.a3.R;

import static x40240.jeffrey.lomibao.a3.model.PersonInfo.Gender.UNKNOWN;
import static x40240.jeffrey.lomibao.a3.model.PersonInfo.Pets.BIRD;
import static x40240.jeffrey.lomibao.a3.model.PersonInfo.Pets.CAT;
import static x40240.jeffrey.lomibao.a3.model.PersonInfo.Pets.DOG;
import static x40240.jeffrey.lomibao.a3.model.PersonInfo.Pets.FISH;

public final class PersonInfo
    implements Serializable
{
    private static final long serialVersionUID = 3202578332355446283L;

    public enum Gender { UNKNOWN, MALE, FEMALE }
    public enum Education { HIGHSCHOOL, ASSOCIATE, BACHELOR, MASTER, DOCTORATE }
    public enum Pets { DOG, CAT, BIRD, FISH }

    private Gender gender = UNKNOWN;
    private String firstname;
    private String lastname;
    private Education education;
    private ArrayList<Pets> pets = new ArrayList<>();

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
    public Gender getGender () { return gender; }

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

    public String getPets(Context context) {
        final Resources resources = context.getResources();
        StringBuilder petsString = new StringBuilder();

        if(pets.isEmpty()) {
            petsString.append(resources.getString(R.string.none_label));
        } else {
            PersonInfo.Pets[] petsArray = pets.toArray(new PersonInfo.Pets[pets.size()]);
            for(int i = 0; i < petsArray.length; i++) {
                switch(petsArray[i]) {
                    case DOG: petsString.append(resources.getString(R.string.dog_label)); break;
                    case CAT: petsString.append(resources.getString(R.string.cat_label)); break;
                    case BIRD: petsString.append(resources.getString(R.string.bird_label)); break;
                    case FISH: petsString.append(resources.getString(R.string.fish_label)); break;
                }
                if(i != (petsArray.length - 1)) {
                    petsString.append(", ");
                }
            }
        }
        return String.valueOf(petsString);
    }

    public void setPets(Context context, String petsString) {
        final Resources resources = context.getResources();
        String[] petsStringArray = petsString.split(", ");

        for(String s:petsStringArray) {
            if(s.contentEquals(resources.getString(R.string.dog_label))) pets.add(DOG);
            if(s.contentEquals(resources.getString(R.string.cat_label))) pets.add(CAT);
            if(s.contentEquals(resources.getString(R.string.bird_label))) pets.add(BIRD);
            if(s.contentEquals(resources.getString(R.string.fish_label))) pets.add(FISH);
        }
    }

}
