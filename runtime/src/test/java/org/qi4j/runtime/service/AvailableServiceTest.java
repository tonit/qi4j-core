/*
 * Copyright (c) 2008, Rickard Öberg. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.qi4j.runtime.service;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.qi4j.api.common.Optional;
import org.qi4j.api.configuration.Configuration;
import org.qi4j.api.configuration.ConfigurationComposite;
import org.qi4j.api.configuration.Enabled;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.Availability;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.service.ServiceReference;
import org.qi4j.api.service.qualifier.Available;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.bootstrap.SingletonAssembler;
import org.qi4j.test.EntityTestAssembler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * JAVADOC
 */
public class AvailableServiceTest
{
    @Test
    public void givenAvailableServiceWhenCheckServiceReferenceThenReturnTrue()
    {
        SingletonAssembler assembler = new SingletonAssembler()
        {
            public void assemble( ModuleAssembly module )
                    throws AssemblyException
            {
                module.addServices( TestServiceComposite1.class);
            }
        };

        ServiceReference<TestServiceComposite1> serviceRef = assembler.serviceFinder().findService( TestServiceComposite1.class );

        assertThat( "service is available", serviceRef.isAvailable(), equalTo( true ) );
    }

    @Test
    public void givenEnablableServiceWhenCheckAvailableThenReturnEnabledStatus()
    {
        SingletonAssembler assembler = new SingletonAssembler()
        {
            public void assemble( ModuleAssembly module )
                    throws AssemblyException
            {
                module.addServices( TestServiceComposite2.class );
                module.addEntities( TestServiceConfiguration.class );

                new EntityTestAssembler().assemble( module );
            }
        };

        ServiceReference<TestServiceComposite2> serviceRef = assembler.serviceFinder().findService( TestServiceComposite2.class );

        assertThat( "service is unavailable", serviceRef.isAvailable(), equalTo( false ) );

        serviceRef.get().configuration().enabled().set( true );
        serviceRef.get().save();

        assertThat( "service is available", serviceRef.isAvailable(), equalTo( true ) );
    }

    @Test
    public void givenEnablableServiceWhenInjectWithAvailableQualifierThenInjectCorrectly()
    {
        SingletonAssembler assembler = new SingletonAssembler()
        {
            public void assemble( ModuleAssembly module )
                    throws AssemblyException
            {
                module.addObjects( TestObject.class );
                module.addServices( TestServiceComposite2.class );
                module.addEntities( TestServiceConfiguration.class );

                new EntityTestAssembler().assemble( module );
            }
        };

        TestObject object = assembler.objectBuilderFactory().newObject( TestObject.class );

        assertThat( "service is unavailable", object.getService(), CoreMatchers.<Object>nullValue() );

        ServiceReference<TestServiceComposite2> serviceRef = assembler.serviceFinder().findService( TestServiceComposite2.class );
        serviceRef.get().configuration().enabled().set( true );
        serviceRef.get().save();

        object = assembler.objectBuilderFactory().newObject( TestObject.class );
        assertThat( "service is available", object.getService(), CoreMatchers.<Object>notNullValue() );
    }

    // This service has to be asked for availability
    @Mixins(TestMixin1.class)
    public interface TestServiceComposite1
            extends TestService, Availability, ServiceComposite
    {
    }

    // This service has availability set through configuration
    @Mixins(TestMixin2.class)
    public interface TestServiceComposite2
            extends TestService, Configuration<TestServiceConfiguration>, ServiceComposite
    {
    }

    public interface TestServiceConfiguration
            extends Enabled, ConfigurationComposite
    {
    }

    public interface TestService
    {
        String test();
    }

    public static class TestMixin1
            implements TestService, Availability
    {
        public String test()
        {
            return "mixin1";
        }

        public boolean isAvailable()
        {
            return true;
        }
    }

    public static class TestMixin2
            implements TestService
    {
        @This
        Configuration<TestServiceConfiguration> config;

        public String test()
        {
            return "mixin2";
        }
    }

    public static class TestObject
    {
        @Service
        @Optional
        @Available TestService service;

        public TestService getService()
        {
            return service;
        }
    }
}