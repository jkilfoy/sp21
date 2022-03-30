package gitlet;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Interface for classes that can be digested into SHA-1 hash.
 * All fields are used in the digestion in their order of declaration,
 * but only some types are reliably supported.
 * Unreliable field types use their toString method for digest.
 */
public interface Digestable {

    /** Returns the SHA-1 digest of the object */
    default String digest() {
        List<String> stringsToHash = new ArrayList<>();

        // Loop through each member of the object and add its String representation to the list
        // Sort the fields by name beforehand to ensure the same order is used each time
        Field[] fields = this.getClass().getDeclaredFields();
        Arrays.sort(fields, Comparator.comparing(Field::getName));
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.getType().equals(byte[].class)) {
                    byte[] value = (byte[]) field.get(this);
                    if (value == null) {
                        stringsToHash.add("");
                    } else {
                        stringsToHash.add(new String(value, StandardCharsets.UTF_8));
                    }
                } else {
                    Object value = field.get(this);
                    if (value == null) {
                        stringsToHash.add("");
                    } else {
                        stringsToHash.add(value.toString());
                    }
                }
            } catch (IllegalAccessException e) {
                // ignore a field that cannot be accessed
            }
        }
        return Utils.sha1(stringsToHash.toArray(new String[0]));
    }
}
