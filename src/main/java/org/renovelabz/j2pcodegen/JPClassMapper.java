package org.renovelabz.j2pcodegen;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by madhawa on 8/24/17.
 */

/**
 * Mapper to store relationship between Java Class Types and Python Class Types. This relationship is required to map
 * imports and class names between two domains.
 */
public class JPClassMapper {
    private HashMap<String, Mapping> J2PMappings = new HashMap<>();

    /**
     * Obtain Python Domain Class Name for a Java Type
     * @param javaType
     * @return
     * @throws Exception
     */
    public String getPythonClassName(Type javaType) throws Exception {
        if(!(javaType instanceof Class<?>))
            throw new Exception("Only class types can be imported");
        Class<?> c = (Class<?>)javaType;
        return J2PMappings.get(c.getCanonicalName()).getClassName();

    }

    /**
     * Check whether the Java Type is wrapped by API.
     * @param javaType
     * @return
     */
    public boolean isRegistered(Type javaType)
    {
        if(!(javaType instanceof Class<?>))
            return false;
        Class<?> c = (Class<?>)javaType;
        if(J2PMappings.containsKey(c.getCanonicalName()))
            return true;
        return false;
    }


    /**
     * Obtain Python domain import string for a Java Type
     * @param javaType
     * @return
     * @throws Exception
     */
    public String getPythonImport(Type javaType) throws Exception {
        if(!(javaType instanceof Class<?>))
            throw new Exception("Only class types can be imported");
        Class<?> c = (Class<?>)javaType;
        return J2PMappings.get(c.getCanonicalName()).getImportString();
    }

    /**
     * Registers a Java Type with ClassMapper
     * @param type
     * @throws Exception
     */
    public void registerType(Type type) throws Exception {
        if(!(type instanceof Class<?>))
            throw new Exception("Only class types can be registered");
        Class<?> c = (Class<?>)type;
        Mapping mapping = new Mapping(c.getCanonicalName(),c.getSimpleName());
        J2PMappings.put(c.getCanonicalName(),mapping);
    }

    class Mapping{
        private String importString;
        private String className;
        public Mapping(String importString, String className)
        {
            this.importString = importString;
            this.className = className;
        }

        public String getImportString() {
            return importString;
        }

        public String getClassName() {
            return className;
        }
    }
}
