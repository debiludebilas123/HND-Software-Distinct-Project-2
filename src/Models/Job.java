package Models;

import java.time.LocalDate;

public class Job implements Insertable {
    private String jobID;
    private String regNo;
    private String garageID;
    private LocalDate dateIn;
    private LocalDate dateOut;
    private double cost;
    private String paymentStatus;

    public Job(String jobID, String regNo, String garageID, LocalDate dateIn, LocalDate dateOut, double cost, String paymentStatus) {
        this.jobID = jobID;
        this.regNo = regNo;
        this.garageID = garageID;
        this.dateIn = dateIn;
        this.dateOut = dateOut;
        this.cost = cost;
        this.paymentStatus = paymentStatus;
    }

    public String getGarageID() {
        return garageID;
    }

    public String getRegNo() {
        return regNo;
    }

    public double getCost() {
        return cost;
    }

    public LocalDate getDateIn() {
        return dateIn;
    }

    public LocalDate getDateOut() {
        return dateOut;
    }

    public String getJobID() {
        return jobID;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    @Override
    public String[] getColumns() {
        return new String[]{"jobID", "regNo", "garageID", "dateIn", "dateOut", "cost", "paymentStatus"};
    }

    @Override
    public Object[] getValues() {
        return new Object[]{getJobID(), getRegNo(), getGarageID(), getDateIn(), getDateOut(), getCost(), getPaymentStatus()};
    }

    @Override
    public String getDatabaseTableName() {
        return "job";
    }

    @Override
    public String getId() {
        return getJobID();
    }
}
