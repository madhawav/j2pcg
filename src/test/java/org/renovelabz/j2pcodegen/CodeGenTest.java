package org.renovelabz.j2pcodegen;



import java.util.ArrayList;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Created by madhawa on 8/24/17.
 */
public class CodeGenTest {
    @org.junit.Test
    public void generateCode() throws Exception {
        Logger.getLogger(CodeGenTest.class.getName()).info("Simulation: This is a simulation. Not a test!");
        CodeGen cg = new CodeGen("src/test/output");
        cg.addType(ArrayList.class);
        cg.generateCode();
    }

}