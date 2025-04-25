package Models;

public interface Insertable {
    // Get database columns from each "Entity" like patient or doctor, so I can input data into the database
    String[] getColumns();
    // Get values from each "Entity" like patient or doctor to add into the database
    Object[] getValues();
    // Get the database table name from each "Entity" to specify what database it's going into
    String getDatabaseTableName();
    // Returns the primary key of an entity like doctor or patient
    String getId();
}
