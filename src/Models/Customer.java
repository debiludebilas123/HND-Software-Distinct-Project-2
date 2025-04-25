package Models;

public class Customer implements Insertable {
    private final String customerID;
    private final String forename;
    private final String surname;
    private final String address;
    private final String postcode;
    private String phoneNo;

    public Customer(String customerID, String forename, String surname, String address, String postcode, String phoneNo) {
        this.customerID = customerID;
        this.forename = forename;
        this.surname = surname;
        this.address = address;
        this.postcode = postcode;
        this.phoneNo = phoneNo;
    }

    public String getAddress() {
        return address;
    }

    public String getForename() {
        return forename;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getSurname() {
        return surname;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    @Override
    public String[] getColumns() {
        return new String[]{"customerID", "forename", "surname", "address", "postcode", "phoneNo"};
    }

    @Override
    public Object[] getValues() {
        return new Object[]{getCustomerID(), getForename(), getSurname(), getAddress(), getPostcode(), getPhoneNo()};
    }

    @Override
    public String getDatabaseTableName() {
        return "customer";
    }

    @Override
    public String getId() {
        return getCustomerID();
    }

    @Override
    public String toString() {
        return getForename() + " " + getSurname();
    }
}
