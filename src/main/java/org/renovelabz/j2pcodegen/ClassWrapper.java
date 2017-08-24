package org.renovelabz.j2pcodegen;

import java.lang.reflect.Type;

/**
 * Created by madhawa on 8/24/17.
 */
public class ClassWrapper {
    private JPClassMapper classMapper = null;
    public ClassWrapper(JPClassMapper classMapper)
    {
        this.classMapper = classMapper;
    }

    /**
     * Generate Python Code for wrapping value at source variable and assigning result to destination variable
     * @param sourceType type of source variable
     * @param sourceName name of source variable
     * @param destinationName name of destination variable
     * @return
     * @throws Exception
     */
    public String wrapClassObject(Type sourceType, String sourceName, String destinationName) throws Exception {

        if(classMapper.isRegistered(sourceType))
        {
            String template = Util.readTemplate(ClassCodeGenerator.class,"object_wrap_template.py.template");
            template = Util.replaceBlock (template,"<<<import>>>",classMapper.getPythonImport(sourceType));
            template = Util.replaceBlock (template,"<<<source_name>>>",sourceName);
            template = Util.replaceBlock (template,"<<<destination_name>>>",destinationName);
            template = Util.replaceBlock (template,"<<<python_type>>>",classMapper.getPythonClassName(sourceType));
            return template;
        }
        else{
            return destinationName + " = " + sourceName + "\n";
        }
    }

    /**
     * Generate Python code for unwraping value in source variable and assigning value to destination variable
     * @param sourceType type of source variable
     * @param sourceName name of source variable
     * @param destinationName name of destination variable
     * @return
     */
    public String unwrapClassObject(Type sourceType, String sourceName, String destinationName)
    {
        if(classMapper.isRegistered(sourceType))
        {
            String template = Util.readTemplate(ClassCodeGenerator.class,"object_unwrap_template.py.template");
            template = Util.replaceBlock (template,"<<<source_name>>>",sourceName);
            template = Util.replaceBlock (template,"<<<destination_name>>>",destinationName);
            return template;
        }
        else{
            return destinationName + " = " + sourceName + "\n";
        }
    }


}
