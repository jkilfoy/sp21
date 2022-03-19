package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;

public class FolderManager<T extends Serializable> implements Iterable<T> {

    final File folder;
    final Class<T> type;

    public FolderManager(File folder, Class<T> type) {
        this.folder = folder;
        this.type = type;

        if (!folder.exists()) {
            try {
                folder.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public File getFolder() {
        return folder;
    }

    public void persist(T obj, String fileName) {
        if (obj == null) return; // do not write null values
        Utils.writeObject(Utils.join(folder, fileName), obj);
    }

    public T read(String fileName) {
        if (!contains(fileName)) return null;
        return Utils.readObject(Utils.join(folder, fileName), type);
    }

    public boolean contains(String fileName) {
        return Utils.join(folder, fileName).exists();
    }

    @Override
    public Iterator<T> iterator() {
        return new FolderManagerIterator<>(this);
    }
}
