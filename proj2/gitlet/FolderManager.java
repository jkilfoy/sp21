package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Function;

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
