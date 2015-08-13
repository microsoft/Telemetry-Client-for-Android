package com.microsoft.cll.android;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

public class CllTests {
    String filePath;

    @Before
    public void setup() {
        try {
            filePath = new File(".").getCanonicalPath() + File.separator + "cllEvents" + File.separator;
        }catch (IOException e) {
            fail("Could not create files in specified directory");
        }
    }

    @After
    public void cleanup() {
        File dir = new File(filePath);
        if(dir.exists()) {
            dir.delete();
        }
    }

    @Test
    public void testIsSingleton() {
        ICll cll = SingletonCll.getInstance("iKey", new CustomLogger(), "", filePath, null);
        ICll cll2 = SingletonCll.getInstance("iKey", new CustomLogger(), "", filePath, null);
        assert(cll == cll2);
        cll.stop();
    }
}
