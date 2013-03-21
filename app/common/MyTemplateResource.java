package common;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.resource.TemplateResourceBase;
import models.CodeFile;

/**
 * User: freewind
 * Date: 13-3-20
 * Time: 下午4:28
 */
public class MyTemplateResource extends TemplateResourceBase {

    private final CodeFile file;

    public MyTemplateResource(CodeFile file) {
        if (file == null) throw new IllegalArgumentException("file should not be null");
        this.file = file;
    }

    @Override
    protected long defCheckInterval() {
        return -1; // never check
    }

    @Override
    protected long lastModified() {
        return 0;
    }

    @Override
    protected String reload() {
        return file.source;
    }

    /**
     * The unique identifier used to fetch this resource from ResourceManager
     *
     * @return the key of the resource
     */
    @Override
    public Object getKey() {
        return file.filename;
    }

    /**
     * Whether this resource is a valid resource
     *
     * @return true if it's a valid resource
     */
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String tagName() {
        return this.file.filename;
    }

}
