package Models;

public class Car implements Insertable {
    private String regNo;
    private String make;
    private String model;
    private int year;
    private String customerID;

    public Car(String regNo, String make, String model, Integer year, String customerID) {
        this.regNo = regNo;
        this.make = make;
        this.model = model;
        this.year = (int) year;
        this.customerID = customerID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getRegNo() {
        return regNo;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String[] getColumns() {
        return new String[]{"regNo", "make", "model", "year", "customerID"};
    }

    @Override
    public Object[] getValues() {
        return new Object[]{getRegNo(), getMake(), getModel(), getYear(), getCustomerID()};
    }

    @Override
    public String getDatabaseTableName() {
        return "car";
    }

    @Override
    public String getId() {
        return getRegNo();
    }

    @Override
    public String toString() {
        return getMake() + " " + getModel() + " - " + getRegNo();
    }
}
