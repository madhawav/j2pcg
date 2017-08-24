package org.renovelabz.j2pcodegen;

import java.util.HashMap;

/**
 * Created by madhawa on 8/24/17.
 */

/**
 * Generates Python Code wrapping a Java Classes provided
 */
public class CodeGen {
    private HashMap<String, Class> wrapTypes = new HashMap<>();
    private String rootPath;

    /**
     * Instantiate CodeGen
     * @param rootPath output directory to generate python wrapping
     */
    public CodeGen(String rootPath)
    {
        this.rootPath = rootPath;
    }

    /**
     * Register a Java Type so it would be included in Python Wrapper
     * @param type
     */
    public void addType(Class type)
    {
        wrapTypes.put(type.getCanonicalName(),type);
    }


    /**
     * Generate Python wrapper
     * @throws Exception
     */
    public void generateCode() throws Exception {
        JPClassMapper classMapper = new JPClassMapper();

        for(Class type: wrapTypes.values())
        {
            classMapper.registerType(type);
        }

        for(Class type: wrapTypes.values())
        {
            ClassCodeGenerator classCodeGenerator = new ClassCodeGenerator(type,classMapper);
            classCodeGenerator.savePy(rootPath);
        }

    }
}
