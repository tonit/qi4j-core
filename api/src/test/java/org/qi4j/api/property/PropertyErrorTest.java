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

package org.qi4j.api.property;

import org.junit.Test;
import org.qi4j.api.composite.TransientBuilder;
import org.qi4j.api.composite.TransientComposite;
import org.qi4j.api.constraint.ConstraintViolationException;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.library.constraints.annotation.MaxLength;
import org.qi4j.library.constraints.annotation.NotEmpty;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;
import org.qi4j.test.AbstractQi4jTest;

/**
 * Error messages for Properties
 */
public class PropertyErrorTest
        extends AbstractQi4jTest
{
    public void assemble( ModuleAssembly module )
            throws AssemblyException
    {
        module.addServices( MemoryEntityStoreService.class, UuidIdentityGeneratorService.class );
        module.addEntities( PersonEntity.class );
    }

    @Test(expected = ConstraintViolationException.class)
    public void givenEntityWithNonOptionPropertyWhenInstantiatedThenException()
            throws Exception
    {
        UnitOfWork unitOfWork = unitOfWorkFactory.newUnitOfWork();
        try
        {
            PersonEntity person = unitOfWork.newEntity( PersonEntity.class );

            unitOfWork.complete();
        }
        catch (Exception e)
        {
            unitOfWork.discard();
            throw e;
        }
    }

    interface PersonEntity
            extends EntityComposite
    {
        Property<String> foo();
    }
}