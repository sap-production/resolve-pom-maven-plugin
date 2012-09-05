package com.sap.prd.mobile.ios.maven.plugins.pomresolver;

/*
 * #%L
 * resolve-pom-maven-plugin
 * %%
 * Copyright (C) 2012 SAP AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;

import org.apache.maven.model.Build;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;

import com.sap.prd.mobile.ios.maven.plugins.pomresolver.stub.MavenProjectBasicStub;

public class ResolvePomPropsMojoTest extends AbstractMojoTestCase
{

  /** {@inheritDoc} */
  protected void setUp()
        throws Exception
  {
    // required
    super.setUp();
  }

  /** {@inheritDoc} */
  protected void tearDown()
        throws Exception
  {
    // required
    super.tearDown();
  }

  /**
   * The pom does not contain any placeholders that have to be replaced
   */
  public void testNothingToReplace() throws Exception
  {
    File pom = getTestFile("target/test-classes/testproject/pom.xml");
    assertNotNull(pom);
    assertTrue(pom.exists());

    ResolvePomPropsMojo myMojo = (ResolvePomPropsMojo) lookupMojo("resolve-pom-props", pom);
    assertNotNull(myMojo);

    MavenProjectBasicStub project = new MavenProjectBasicStub();
    Build build = new Build();
    build.setDirectory(pom.getParent() + "/target");
    project.setBuild(build);
    project.setFile(pom);
    myMojo.project = project;

    myMojo.execute();
    assertFalse("There should not exist a resolved POM file",
          new File("target/test-classes/testproject/resolvedPom.xml").exists());
  }

  /**
   * The placeholder in the POM has to be replaced
   */
  public void testReplacePomProperty() throws Exception
  {
    File pom = getTestFile("target/test-classes/testproject/pom.xml");
    assertNotNull(pom);
    assertTrue(pom.exists());

    ResolvePomPropsMojo mojo = (ResolvePomPropsMojo) lookupMojo("resolve-pom-props", pom);
    assertNotNull(mojo);

    MavenProjectBasicStub project = new MavenProjectBasicStub();
    Build build = new Build();
    build.setDirectory(pom.getParent() + "/target");
    project.setBuild(build);
    project.setFile(pom);
    // Add the property to be replaced
    project.addProperty("injectedSubversion", ".1-SNAPSHOT");
    mojo.project = project;

    mojo.execute();
    File resolvedPomFile = new File("target/test-classes/testproject/resolvedPom.xml");
    assertTrue("There should exist a resolved POM file", resolvedPomFile.exists());
    String pomContent = FileUtils.fileRead(resolvedPomFile);
    assertTrue("The propeorty in the version element was not replaced",
          pomContent.contains("<version>1.0.1-SNAPSHOT</version>"));
  }

}
