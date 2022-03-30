package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Function;
import static gitlet.Utils.*;

/**
 * Utility class for easily reading, persisting and looping over serializable objects
 * within a folder that contains them.
 * @param <T> The type of object serialized in the folder
 */
public class FolderManager<T extends Serializable> implements Iterable<T> {

    final File folder;
    final Class<T> type;
    final Function<T, String> getFileName;

    public FolderManager(File folder, Class<T> type) {
        this(folder, type, Object::toString);
    }

    public FolderManager(File folder, Class<T> type, Function<T, String> getFileName) {
        this.folder = folder;
        this.type = type;
        this.getFileName = getFileName;
    }

    public File getFolder() {
        return folder;
    }

    public void persist(T obj) {
        persist(obj, getFileName.apply(obj));
    }

    public void persist(T obj, String fileName) {
        if (obj == null) return; // do not write null values
        writeObject(join(folder, fileName), obj);
    }

    public T read(String fileName) {
        if ("".equals(fileName) || !contains(fileName)) return null;
        return readObject(join(folder, fileName), type);
    }

    public boolean contains(String fileName) {
        return join(folder, fileName).exists();
    }

    /** Deletes the file from the folder */
    public void clear(String filename) {
        File file = join(folder, filename);
        if (file.exists() && !file.isDirectory()) {
            file.delete();
        }
    }

    public void clearAll() {
        for (String filename : plainFilenamesIn(folder)) {
            clear(filename);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new FolderManagerIterator<>(this);
    }
}
