/*
 * Copyright (c) 2010, Rickard Öberg. All Rights Reserved.
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

package org.qi4j.api.common;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.qi4j.api.common.ConstructionException;
import org.qi4j.api.injection.scope.Uses;
import org.qi4j.test.AbstractQi4jTest;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;

/**
 * JAVADOC
 */
public class ObjectBuilderTest
        extends AbstractQi4jTest
{
    public void assemble( ModuleAssembly module ) throws AssemblyException
    {
        module.addObjects( A.class, B.class, C.class );
    }

    @Test(expected = ConstructionException.class)
    public void testMissingUses()
    {
        objectBuilderFactory.newObjectBuilder( A.class ).newInstance();
    }

    public static class A
    {
        @Uses
        B b;
    }

    public static class B
    {
        @Uses
        C c;
    }

    public static class C
    {
        @Uses
        D d;
    }

    public static class D
    {

    }
}
