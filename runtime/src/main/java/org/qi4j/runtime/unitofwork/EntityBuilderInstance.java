/*
 * Copyright (c) 2007, Rickard Öberg. All Rights Reserved.
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

package org.qi4j.runtime.unitofwork;

import org.qi4j.api.common.QualifiedName;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.entity.EntityReference;
import org.qi4j.api.entity.Identity;
import org.qi4j.api.entity.LifecycleException;
import org.qi4j.runtime.entity.EntityInstance;
import org.qi4j.runtime.entity.EntityModel;
import org.qi4j.runtime.structure.ModuleInstance;
import org.qi4j.runtime.structure.ModuleUnitOfWork;
import org.qi4j.spi.entity.EntityState;
import org.qi4j.spi.entitystore.EntityStoreUnitOfWork;

/**
 * Implementation of EntityBuilder. Maintains an instance of the entity which
 * will not have its state validated until it is created by calling newInstance().
 */
public final class EntityBuilderInstance<T>
    implements EntityBuilder<T>
{
    private static final QualifiedName identityStateName;

    private final ModuleInstance moduleInstance;
    private final EntityModel entityModel;
    private final ModuleUnitOfWork uow;
    private final EntityStoreUnitOfWork store;
    private String identity;

    private final BuilderEntityState entityState;
    private final EntityInstance prototypeInstance;

    static
    {
        try
        {
            identityStateName = QualifiedName.fromMethod( Identity.class.getMethod( "identity" ) );
        }
        catch( NoSuchMethodException e )
        {
            throw new InternalError( "Qi4j Core Runtime codebase is corrupted. Contact Qi4j team: EntityBuilderInstance" );
        }
    }

    public EntityBuilderInstance(
        ModuleInstance moduleInstance,
        EntityModel entityModel,
        ModuleUnitOfWork uow,
        EntityStoreUnitOfWork store,
        String identity
    )
    {
        this.moduleInstance = moduleInstance;
        this.entityModel = entityModel;
        this.uow = uow;
        this.store = store;
        this.identity = identity;
        EntityReference reference = new EntityReference( identity );
        entityState = new BuilderEntityState( entityModel, reference );
        entityModel.initState( entityState );
        entityState.setProperty( identityStateName, identity );
        prototypeInstance = entityModel.newInstance( uow, moduleInstance, entityState );
    }

    @SuppressWarnings( "unchecked" )
    public T instance()
    {
        checkValid();
        return prototypeInstance.<T>proxy();
    }

    public <K> K instanceFor( Class<K> mixinType )
    {
        checkValid();
        return prototypeInstance.newProxy( mixinType );
    }

    public T newInstance()
        throws LifecycleException
    {
        checkValid();

        String identity;

        // Figure out whether to use given or generated identity
        identity = (String) entityState.getProperty( identityStateName );
        EntityState newEntityState = entityModel.newEntityState( store, EntityReference.parseEntityReference( identity ) );

        prototypeInstance.invokeCreate();

        // Check constraints
        prototypeInstance.checkConstraints();

        entityState.copyTo( newEntityState );

        EntityInstance instance = entityModel.newInstance( uow, moduleInstance, newEntityState );

        Object proxy = instance.proxy();

        // Add entity in UOW
        uow.addEntity( instance );

        // Invalidate builder
        this.identity = null;

        return (T) proxy;
    }

    private void checkValid()
        throws IllegalStateException
    {
        if( identity == null )
        {
            throw new IllegalStateException( "EntityBuilder is not valid after call to newInstance()" );
        }
    }
}
