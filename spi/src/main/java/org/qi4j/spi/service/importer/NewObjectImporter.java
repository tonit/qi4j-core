/*
 * Copyright (c) 2009, Rickard Öberg. All Rights Reserved.
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

package org.qi4j.spi.service.importer;

import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.object.ObjectBuilderFactory;
import org.qi4j.api.service.ImportedServiceDescriptor;
import org.qi4j.api.service.ServiceImporter;
import org.qi4j.api.service.ServiceImporterException;

/**
 * JAVADOC
 */
public final class NewObjectImporter<T>
    implements ServiceImporter<T>
{
    @Structure
    private ObjectBuilderFactory obf;

    public T importService( ImportedServiceDescriptor serviceDescriptor )
        throws ServiceImporterException
    {
        return (T) obf.newObject( serviceDescriptor.type() );
    }

    public boolean isActive( T instance )
    {
        return true;
    }

    public boolean isAvailable( T instance )
    {
        return true;
    }
}