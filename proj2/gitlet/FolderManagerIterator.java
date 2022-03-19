package gitlet;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class FolderManagerIterator<T extends Serializable> implements Iterator<T> {

    final FolderManager<T> folderManager;
    final List<String> files;
    int index;

    public FolderManagerIterator(FolderManager<T> folderManager) {
        this.folderManager = folderManager;
        files = Utils.plainFilenamesIn(folderManager.getFolder());
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < files.size();
    }

    @Override
    public T next() {
        T obj = folderManager.readObject(files.get(index));
        index++;
        return obj;
    }
}
