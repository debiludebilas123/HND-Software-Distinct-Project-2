package Services;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class CSVExport {
    public static <T> void exportToCSV(List<T> items, String filePath) throws IOException {
        if (items == null || items.isEmpty()) {
            return;
        }

        Class<?> itemClass = items.getFirst().getClass();
        Field[] fields = itemClass.getDeclaredFields();

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header row
            for (int i = 0; i < fields.length; i++) {
                writer.write(fields[i].getName());
                if (i < fields.length - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");

            // Write data rows
            for (T item : items) {
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    Object value = fields[i].get(item);
                    writer.write(value != null ? value.toString() : "");
                    if (i < fields.length - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
            }
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to access object fields", e);
        }
    }
}
