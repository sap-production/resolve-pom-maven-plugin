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
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;
import org.codehaus.plexus.util.FileUtils;

/**
 * Creates a copy of the original pom where all properties get resolved by its actual values. This
 * substituted POM is used in the former processing. Especially it will be used when an upload to
 * the local or remote repository takes place.
 */
@Mojo(name = "resolve-pom-props", defaultPhase = LifecyclePhase.INITIALIZE)
public class ResolvePomPropsMojo
      extends AbstractMojo
{

  /**
   * The character encoding scheme to be applied when filtering the POM.
   */
  @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
  protected String encoding;

  /**
   * The name of the POM that gets created in parallel to the original POM where all properties are
   * resolved
   */
  @Parameter(property = "pomresolver.resolvedPomName", defaultValue = "resolvedPom.xml")
  protected String resolvedPomName;

  /**
   * Injected by Maven.
   */
  @Component(role = MavenResourcesFiltering.class, hint = "default")
  protected MavenResourcesFiltering mavenResourcesFiltering;

  /**
   * The current MavenProject
   */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  protected MavenProject project;

  @Parameter(defaultValue = "${session}", required = true, readonly = true)
  protected MavenSession session;

  public void execute() throws MojoExecutionException
  {
    try {
      File origPomFile = project.getFile();
      File intermediatePomFile = new File(project.getBuild().getDirectory() + File.separator + origPomFile.getName());
      File resolvedPomFile = new File(origPomFile.getParentFile(), resolvedPomName);

      Resource pomResource = new Resource();
      pomResource.setDirectory(origPomFile.getParent());
      pomResource.setFiltering(true);
      pomResource.addInclude(origPomFile.getName());
      pomResource.setTargetPath(intermediatePomFile.getParent());

      List<Resource> resources = new ArrayList<Resource>();
      resources.add(pomResource);

      MavenResourcesExecution resourceExecution = new MavenResourcesExecution(resources,
            intermediatePomFile.getParentFile(), project, encoding, null, null, session);
      resourceExecution.setOverwrite(true);

      mavenResourcesFiltering.filterResources(resourceExecution);

      // copy the filtered POM to the source directory if any replacement occurred
      String origPomContent = FileUtils.fileRead(origPomFile);
      String newPomContent = FileUtils.fileRead(intermediatePomFile);
      if (resolvedPomFile.isFile()) {
        resolvedPomFile.delete();
      }

      if (!origPomContent.equals(newPomContent)) {
        FileUtils.copyFile(intermediatePomFile, resolvedPomFile);
        project.setFile(resolvedPomFile);
        getLog()
          .info("Created a POM file '" + resolvedPomFile.getName()
                + "' where all variables are resolved and that is used for further processing."
                + " Especially this POM file will get uploaded into the local and remote repository.");
      }
      else {
        getLog().info("No properties got replaced in the POM. Keeping the original file.");
      }

    }
    catch (Exception ex) {
      throw new MojoExecutionException("Failed to create resolved POM", ex);
    }
  }

}
