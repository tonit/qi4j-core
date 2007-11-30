/*
 * Copyright 2007 Rickard Öberg
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.spi.composite;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.qi4j.spi.dependency.InjectionModel;

/**
 * Composite Models define what a particular Composite type declares through annotations and method declarations.
 */
public final class CompositeModel
{
    private Class compositeClass;
    private Class proxyClass;
    private Collection<CompositeMethodModel> compositeMethodModels;
    private Iterable<ConstraintModel> constraintModels;
    private Iterable<MixinModel> mixinModels;
    private Iterable<ConcernModel> concernModels;
    private Iterable<SideEffectModel> sideEffectModels;
    private Iterable<CompositeMethodModel> thisAsModels;
    private Iterable<PropertyModel> propertyModels;

    private Map<Class<? extends Annotation>, List<ConstraintModel>> constraintModelMappings;

    public CompositeModel( Class compositeClass, Class proxyClass, Collection<CompositeMethodModel> methodModels, Iterable<MixinModel> mixinModels, Iterable<ConstraintModel> constraintModels, Iterable<ConcernModel> concernModels, Iterable<SideEffectModel> sideEffectModels, Iterable<CompositeMethodModel> thisAsModels, Map<Class<? extends Annotation>, List<ConstraintModel>> constraintModelMappings, Iterable<PropertyModel> propertyModels )
    {
        this.propertyModels = propertyModels;
        this.proxyClass = proxyClass;
        this.constraintModelMappings = constraintModelMappings;
        this.constraintModels = constraintModels;
        this.thisAsModels = thisAsModels;
        this.compositeMethodModels = methodModels;
        this.compositeClass = compositeClass;
        this.mixinModels = mixinModels;
        this.concernModels = concernModels;
        this.sideEffectModels = sideEffectModels;
    }

    public Class getCompositeClass()
    {
        return compositeClass;
    }

    public Class getProxyClass()
    {
        return proxyClass;
    }

    public Collection<CompositeMethodModel> getCompositeMethodModels()
    {
        return compositeMethodModels;
    }

    public Iterable<MixinModel> getMixinModels()
    {
        return mixinModels;
    }

    public Iterable<ConstraintModel> getConstraintModels()
    {
        return constraintModels;
    }

    public Iterable<ConcernModel> getConcernModels()
    {
        return concernModels;
    }

    public Iterable<SideEffectModel> getSideEffectModels()
    {
        return sideEffectModels;
    }

    public Iterable<CompositeMethodModel> getThisCompositeAsModels()
    {
        return thisAsModels;
    }

    public Iterable<PropertyModel> getPropertyModels()
    {
        return propertyModels;
    }

    public List<MixinModel> getImplementations( Class aType )
    {
        List<MixinModel> impls = new ArrayList<MixinModel>();

        // Check non-generic impls first
        for( MixinModel implementation : mixinModels )
        {
            if( !implementation.isGeneric() )
            {
                Class fragmentClass = implementation.getModelClass();
                if( aType.isAssignableFrom( fragmentClass ) )
                {
                    impls.add( implementation );
                }
            }
        }

        // Check generic impls
        for( MixinModel implementation : mixinModels )
        {
            if( implementation.isGeneric() )
            {
                // Check AppliesTo
                Collection<Class> appliesTo = implementation.getAppliesTo();
                if( appliesTo == null )
                {
                    impls.add( implementation ); // This generic mixin can handle the given type
                }
                else
                {
                    for( Class appliesToClass : appliesTo )
                    {
                        if( appliesToClass.isAssignableFrom( aType ) )
                        {
                            impls.add( implementation );
                        }
                    }
                }
            }
        }

        return impls;
    }

    public ConstraintModel getConstraintModel( Class<? extends Annotation> annotationType, Type parameterType )
    {
        Iterable<ConstraintModel> possibleConstraintModels = constraintModelMappings.get( annotationType );
        while( true )
        {
            for( ConstraintModel possibleConstraintModel : possibleConstraintModels )
            {
                if( possibleConstraintModel.getParameterType().equals( parameterType ) )
                {
                    return possibleConstraintModel;
                }
            }

            if( parameterType.equals( Object.class ) )
            {
                return null; // No suitable constraint implementation found for this annotation
            }

            if( parameterType instanceof Class )
            {
                parameterType = ( (Class) parameterType ).getSuperclass(); // Try super-class
            }
            else
            {
                return null;
            }
        }
    }

    public String toString()
    {
        StringWriter str = new StringWriter();
        PrintWriter out = new PrintWriter( str );
        out.println( compositeClass.getName() );

        out.println( "  implementations available" );
        for( MixinModel implementation : mixinModels )
        {
            out.println( "    " + implementation.getModelClass().getName() );
        }

        out.println( "  concerns available" );
        for( ConcernModel concernModel : concernModels )
        {
            out.println( "    " + concernModel.getModelClass().getName() );
        }

        out.println( "  side-effects available" );
        for( SideEffectModel sideEffectModel : sideEffectModels )
        {
            out.println( "    " + sideEffectModel.getModelClass().getName() );
        }
        out.close();
        return str.toString();
    }


    public boolean equals( Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        CompositeModel composite1 = (CompositeModel) o;

        return compositeClass.equals( composite1.compositeClass );

    }

    public int hashCode()
    {
        return compositeClass.hashCode();
    }

    public Iterable<InjectionModel> getInjectionsByScope( Class<? extends Annotation> aClass )
    {
        List<InjectionModel> injectionModels = new ArrayList<InjectionModel>();
        for( MixinModel mixinModel : mixinModels )
        {
            Iterable<InjectionModel> scope = mixinModel.getInjectionsByScope( aClass );
            for( InjectionModel injectionModel : scope )
            {
                injectionModels.add( injectionModel );
            }
        }
        for( ConcernModel concernModel : concernModels )
        {
            Iterable<InjectionModel> scope = concernModel.getInjectionsByScope( aClass );
            for( InjectionModel injectionModel : scope )
            {
                injectionModels.add( injectionModel );
            }
        }
        for( SideEffectModel sideEffectModel : sideEffectModels )
        {
            Iterable<InjectionModel> scope = sideEffectModel.getInjectionsByScope( aClass );
            for( InjectionModel injectionModel : scope )
            {
                injectionModels.add( injectionModel );
            }
        }

        return injectionModels;
    }
}