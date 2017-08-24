/**
 * Created by madhawa on 8/13/17.
 */
package org.renovelabz.j2pcodegen;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Generates Python files wrapping a Java Class
 */
public class ClassCodeGenerator {
    private Class _target = null;
    private JPClassMapper classMapper;

    /**
     * Constructor
     * @param target Type of class to be wrapped
     * @param classMapper Class mapper which holds Java Python class correspondence
     */
    public ClassCodeGenerator(Class target, JPClassMapper classMapper)
    {
        this._target = target;
        this.classMapper = classMapper;
    }

    /**
     * Retrieve ClassMapper
     * @return
     */
    public JPClassMapper getClassMapper()
    {
        return classMapper;
    }


    /**
     * Stores generated python file in sub-directory, relative to root path
     * @param rootPath output directory for all python classes
     * @throws Exception
     */
    public void savePy(String rootPath) throws Exception {
        String[] parts = _target.getCanonicalName().split("\\.");
        String[] preParts = new String[parts.length-1];
        for(int i = 0; i < parts.length-1; i++)
            preParts[i] = parts[i];
        String tailPath = String.join("/",preParts);
        String path = rootPath + "/" + tailPath;
        Files.createDirectories(Paths.get(path));

        FileWriter fileWriter = new FileWriter(path + "/" + parts[parts.length-1] +".py");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.write(obtainCode());
        printWriter.close();
    }

    /**
     * Obtains python code for wrapping a given method
     * @param method method to generate wrapper code
     * @return Python Code
     * @throws Exception
     */
    private String obtainMethodCode(Method method) throws Exception {
        String functionTemplate = Util.readTemplate(ClassCodeGenerator.class,"function_template.py.template");
        String methodTemplate = Util.readTemplate(ClassCodeGenerator.class,"method_template.py.template");

        ClassWrapper classWrapper = new ClassWrapper(classMapper);
        String functionTemplateInst = functionTemplate;
        if(method.getReturnType().equals(Void.TYPE))
            functionTemplateInst = methodTemplate;
        functionTemplateInst = Util.replaceBlock(functionTemplateInst,"<<<FunctionName>>>", method.getName());

        Parameter[] parameters = method.getParameters();
        String[] parameterNames = new String[parameters.length];
        String[] rawParameterNames = new String[parameters.length];

        String[] parameterUnwraps = new String[parameters.length];

        int pIndex = 0;
        for(Parameter parameter: parameters)
        {
            parameterUnwraps[pIndex] = classWrapper.unwrapClassObject(parameter.getType(),parameter.getName(), "raw_" + parameter.getName());
            rawParameterNames[pIndex] = "raw_"+parameter.getName();
            parameterNames[pIndex++] = parameter.getName();
        }

        String parametersText = String.join(", ", parameterNames);
        String rawParametersText = String.join(", ", rawParameterNames);
        String parameterUnwrapsText = String.join("\n",parameterUnwraps);

        functionTemplateInst = Util.replaceBlock(functionTemplateInst,"<<<parameters>>>", parametersText);
        functionTemplateInst = Util.replaceBlock(functionTemplateInst,"<<<parameter_unwrap>>>",parameterUnwrapsText);
        functionTemplateInst = Util.replaceBlock(functionTemplateInst,"<<<unwrapped_parameters>>>",rawParametersText);

        if(!(method.getReturnType().equals(Void.TYPE)))
        {
            String resultsConversion = classWrapper.wrapClassObject(method.getReturnType(),"raw_result","result");
            functionTemplateInst = Util.replaceBlock(functionTemplateInst,"<<<result_wrap>>>", resultsConversion);
        }
        return functionTemplateInst;
    }

    /**
     * Obtains Python code for wrapping constructors
     * @param constructors constructors of class
     * @return Python Code
     * @throws Exception
     */
    private String obtainConstructorCode(Constructor[] constructors) throws Exception {
        ClassWrapper classWrapper = new ClassWrapper(classMapper);
        String constructorTemplate = Util.readTemplate(ClassCodeGenerator.class,"constructor_template.py.template");

        if(constructors.length > 1) {
            Logger.getLogger(ClassCodeGenerator.class.getName()).info("Multiple constructors are not supported");
        }

        Constructor constructor = constructors[0];
        String functionTemplateInst = constructorTemplate;

        Parameter[] parameters = constructor.getParameters();
        String[] parameterNames = new String[parameters.length];
        String[] rawParameterNames = new String[parameters.length];

        String[] parameterUnwraps = new String[parameters.length];

        int pIndex = 0;
        for(Parameter parameter: parameters)
        {
            parameterUnwraps[pIndex] = classWrapper.unwrapClassObject(parameter.getType(),parameter.getName(), "raw_" + parameter.getName());
            rawParameterNames[pIndex] = "raw_"+parameter.getName();
            parameterNames[pIndex++] = parameter.getName();
        }

        String parametersText = String.join(", ", parameterNames);
        String rawParametersText = String.join(", ", rawParameterNames);
        String parameterUnwrapsText = String.join("\n",parameterUnwraps);

        functionTemplateInst = Util.replaceBlock(functionTemplateInst,"<<<parameters>>>", parametersText);
        functionTemplateInst = Util.replaceBlock(functionTemplateInst,"<<<parameter_unwrap>>>",parameterUnwrapsText);
        functionTemplateInst = Util.replaceBlock(functionTemplateInst,"<<<unwrapped_parameters>>>",rawParametersText);
        functionTemplateInst = Util.replaceBlock(functionTemplateInst,"<<<CanonicalClassName>>>", _target.getCanonicalName());
        return functionTemplateInst;

    }

    /**
     * Obtains python code wrapping Java Class
     * @return
     * @throws Exception
     */
    public String obtainCode() throws Exception {
        String template = Util.readTemplate(ClassCodeGenerator.class,"class_template.py.template");
        template = Util.replaceBlock (template,"<<<SimpleClassName>>>",_target.getSimpleName());
        template = Util.replaceBlock(template,"<<<CanonicalClassName>>>", _target.getCanonicalName());

        Method[] methods = _target.getMethods();
        Constructor[] constructors = _target.getConstructors();
        String[] functionSignatures;
        if(constructors.length == 0)
            functionSignatures = new String[methods.length];
        else
            functionSignatures = new String[methods.length + 1];

        boolean first = true;
        int index = 0;
        for(Method method: methods)
        {
            functionSignatures[index++] = obtainMethodCode(method);
        }

        if(constructors.length > 0)
            functionSignatures[index++] = obtainConstructorCode(constructors);

        String bodyText = String.join("\n",functionSignatures);


        template = Util.replaceBlock(template,"<<<ClassBody>>>",bodyText);

        return template;
    }

}
