package com.sap.prd.mobile.ios.maven.plugins.pomresolver.stub;

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

import java.util.Properties;

import org.apache.maven.plugin.testing.stubs.MavenProjectStub;

/**
 * Allows the MavenProject to have non empty Properties which is not possible in the
 * {@link MavenProjectStub}.
 */
public class MavenProjectBasicStub extends MavenProjectStub
{
  protected Properties properties = new Properties();

    public void addProperty( String key, String value )
    {
        properties.put( key, value );
    }

    public Properties getProperties()
    {
        return properties;
    }
}
