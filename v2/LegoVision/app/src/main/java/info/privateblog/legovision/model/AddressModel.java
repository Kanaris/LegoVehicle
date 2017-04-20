package info.privateblog.legovision.model;

/**
 * Created by Merl1n on 01.03.2017.
 */

public class AddressModel {
    private String name;
    private String address;

    public AddressModel(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return name;
    }
}
