package Models;

public class Garage implements Insertable {
    private String garageID;
    private String garageName;
    private String address;
    private String town;
    private String postcode;
    private String phoneNo;

    public Garage(String garageID,  String garageName, String address, String town, String postcode, String phoneNo) {
        this.garageID = garageID;
        this.garageName = garageName;
        this.address = address;
        this.town = town;
        this.postcode = postcode;
        this.phoneNo = phoneNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getTown() {
        return town;
    }

    public String getAddress() {
        return address;
    }

    public String getGarageID() {
        return garageID;
    }

    public String getGarageName() {
        return garageName;
    }

    @Override
    public String[] getColumns() {
        return new String[]{"garageID", "garageName", "address", "town", "postcode", "phoneNo"};
    }

    @Override
    public Object[] getValues() {
        return new Object[]{getGarageID(), getGarageName(), getAddress(), getTown(), getPostcode(), getPhoneNo()};
    }

    @Override
    public String getDatabaseTableName() {
        return "garage";
    }

    @Override
    public String getId() {
        return getGarageID();
    }

    @Override
    public String toString() {
        return getGarageName();
    }
}
