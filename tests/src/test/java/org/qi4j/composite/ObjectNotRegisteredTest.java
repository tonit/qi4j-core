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

package org.qi4j.composite;

import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.test.AbstractQi4jTest;

/**
 * Test if it is
 */
public class ObjectNotRegisteredTest extends AbstractQi4jTest
{
    public void assemble( ModuleAssembly module )
    {
    }

    public void testWhenObjectNotRegisteredThenThrowException()
        throws Exception
    {
        try
        {
            ObjectBuilder<Object1> builder = objectBuilderFactory.newObjectBuilder( Object1.class );
            fail( "Could create builder for unregistered object type" );
        }
        catch( Exception e )
        {
            // Ok!
        }
    }

    public static final class Object1
    {

    }
}
