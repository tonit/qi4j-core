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

package org.qi4j.runtime;

import org.junit.Test;
import org.qi4j.api.composite.Composite;
import org.qi4j.api.composite.TransientComposite;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.value.ValueComposite;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.test.AbstractQi4jTest;
import org.qi4j.test.EntityTestAssembler;

/**
 * JAVADOC
 */
public class Qi4jAPITest
        extends AbstractQi4jTest
{
    public void assemble( ModuleAssembly module )
            throws AssemblyException
    {
        new EntityTestAssembler().assemble( module );
        module.addTransients( TestTransient.class );
        module.addEntities( TestEntity.class );
        module.addValues( TestValue.class );
        module.addServices( TestService.class );
    }

    @Test
    public void testGetModuleOfComposite()
            throws Exception
    {
        UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
        TestEntity testEntity = unitOfWork.newEntity( TestEntity.class );

        api.getModule( testEntity );

        unitOfWork.discard();

        api.getModule( valueBuilderFactory.newValue( TestValue.class ) );

        api.getModule( transientBuilderFactory.newTransient( TestTransient.class ) );

        api.getModule( (Composite) serviceLocator.<Object>findService( TestService.class ).get() );
    }

    public interface TestTransient
            extends TransientComposite
    {
    }

    public interface TestEntity
            extends EntityComposite
    {
    }

    public interface TestValue
            extends ValueComposite
    {
    }

    public interface TestService
            extends ServiceComposite
    {
    }
}