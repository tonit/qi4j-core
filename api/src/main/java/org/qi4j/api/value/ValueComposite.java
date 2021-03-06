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

package org.qi4j.api.value;

import org.qi4j.api.composite.Composite;
import org.qi4j.api.property.Immutable;

/**
 * ValueComposites are Composites that only has value, and equality is defined from its values and not any identity
 * nor instance references.
 *
 * <ul>
 * <li>No Identity</li>
 * <li>No Lifecycle</li>
 * <li>Immutable</li>
 * <li>equals()/hashCode() operates on the Properties</li>
 * <li>Can only have property methods.</li>
 * <li>Can not reference Services</li>
 * <li>Can not have @Uses</li>
 */
@Immutable
public interface ValueComposite
    extends Value, Composite
{
}
