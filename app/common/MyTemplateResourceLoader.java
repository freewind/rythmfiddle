package common;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.resource.ITemplateResource;
import com.greenlaw110.rythm.resource.ITemplateResourceLoader;
import com.greenlaw110.rythm.resource.TemplateResourceManager;
import models.Code;
import models.CodeFile;

/**
 * User: freewind
 * Date: 13-3-20
 * Time: 下午1:38
 */
public class MyTemplateResourceLoader implements ITemplateResourceLoader {


    /**
     * Load template resource by path
     *
     * @param path
     * @return Loaded template resource
     */
    @Override
    public ITemplateResource load(String path) {
        Code code = (Code) Code.rythm.renderSettings.userContext().get("code");
        return new MyTemplateResource(code.findCodeFile(path));
    }

    /**
     * Try to load a template tag with tag name.
     *
     * @param tmplName
     * @param engine
     * @param callerTemplateClass
     * @return template class if found, or <tt>null</tt> if not found
     */
    @Override
    public TemplateClass tryLoadTemplate(String tmplName, RythmEngine engine, TemplateClass callerTemplateClass) {
        Code code = (Code) engine.renderSettings.userContext().get("code");
        CodeFile codeFile = code.findCodeFile(tmplName);
        if (codeFile == null) return null;
        return new TemplateClass(new MyTemplateResource(codeFile), engine, true);
    }

    /**
     * Return a template's tag name in full notation
     *
     * @param tc
     * @param engine
     * @return the tag name
     */
    @Override
    public String getFullName(TemplateClass tc, RythmEngine engine) {
        return tc.getFullName();
    }

    /**
     * Scan the folder and try to load all template files under the folder.
     * Once a resource is located, it should be passed to the
     * {@link com.greenlaw110.rythm.resource.TemplateResourceManager resource manager} by
     * {@link com.greenlaw110.rythm.resource.TemplateResourceManager#resourceLoaded(com.greenlaw110.rythm.resource.ITemplateResource)} call
     *
     * @param root the root folder
     */
    @Override
    public void scan(String root, TemplateResourceManager manager) {
        for (Code code : CodeManager.getAll().values()) {
            for (CodeFile file : code.files) {
                manager.resourceLoaded(new MyTemplateResource(file));
            }
        }
    }

}
