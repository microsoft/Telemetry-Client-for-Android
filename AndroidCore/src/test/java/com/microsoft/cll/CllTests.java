package com.microsoft.cll;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

public class CllTests {
    @Test
    public void testIsSingleton() {
        String filePath = "";
        try {
            filePath = new File(".").getCanonicalPath() + File.separator + "cllEvents";
        }catch (IOException e) {
            fail("Could not create files in specified directory");
        }

        ICll cll = SingletonCll.getInstance("iKey", new CustomLogger(), "", filePath, null);
        ICll cll2 = SingletonCll.getInstance("iKey", new CustomLogger(), "", filePath, null);
        assert(cll == cll2);
    }
}
