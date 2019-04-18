/*
 * =============================================================================
 *
 *   Copyright (c) 2019, The VEXPREL team (http://www.vexprel.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package org.vexprel.standard.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import org.vexprel.exceptions.ExpressionExecutionException;

final class ByteCodeGenUtils {

    private static final AtomicInteger index = new AtomicInteger(0);

    private static final String OBJECT_PROPERTY_ACTION_CLASS_NAME_FORMAT =
            StandardExpressionAction.class.getPackage().getName() + ".%s_%s_%d";

    private static final Method actionExecuteMethod;
    private static final DynamicType.Builder.MethodDefinition.ImplementationDefinition<StandardExpressionAction> actionImplementationDef;



    static {

        try {
            actionExecuteMethod =
                    StandardExpressionAction.class.getMethod("execute", Object.class);
        } catch (final NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }

        //noinspection unchecked
        actionImplementationDef =
                (DynamicType.Builder.MethodDefinition.ImplementationDefinition<StandardExpressionAction>)
                (DynamicType.Builder.MethodDefinition.ImplementationDefinition<?>)
                new ByteBuddy()
                        .subclass(Object.class)
                        .implement(StandardExpressionAction.class)
                        .method(ElementMatchers.is(actionExecuteMethod));

    }



    private static int nextIndex() {
        int value,next;
        do {
            value = index.get();
            next = (value < Integer.MAX_VALUE? value + 1 : 0);
        } while (!index.compareAndSet(value, next));
        return value;
    }



    private static String computeObjectPropertyActionClassName(final Class<?> targetClass, final String getterMethodName) {
        return String.format(OBJECT_PROPERTY_ACTION_CLASS_NAME_FORMAT, targetClass.getName(), getterMethodName, nextIndex());
    }



    static DynamicType.Unloaded<StandardExpressionAction> buildObjectPropertyType(
            final Class<?> targetClass, final String getterMethodName) {

        final Method getterMethod;
        try {
            getterMethod = targetClass.getMethod(getterMethodName);
        } catch (final NoSuchMethodException e) {
            throw new ExpressionExecutionException(
                    String.format("Could not find method %s in class %s", getterMethodName, targetClass.getName()), e);
        }

        return actionImplementationDef
                        .intercept(
                                MethodCall.invoke(getterMethod)
                                        .onArgument(0)
                                        .withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC))
                        .name(computeObjectPropertyActionClassName(targetClass, getterMethodName))
                        .make();

    }


    static Class<? extends StandardExpressionAction> loadIntoNewWrapperClassLoader(
            final DynamicType.Unloaded<StandardExpressionAction> type,
            final ClassLoader baseClassLoader, final boolean leaveOpen) {

        // We will create a new class loader, and leave it open so that we can add new classes
        final ClassLoadingStrategy<? super ClassLoader> classLoadingStrategy =
                leaveOpen?
                        ClassLoadingStrategy.Default.WRAPPER.opened() :
                        ClassLoadingStrategy.Default.WRAPPER;

        return type
                .load(baseClassLoader, classLoadingStrategy)
                .getLoaded();

    }


    static Class<? extends StandardExpressionAction> loadIntoExistingClassLoader(
            final DynamicType.Unloaded<StandardExpressionAction> type, final InjectionClassLoader classLoader) {
        // We will inject the class into a previously existing (and open) class loader
        return type
                .load(classLoader, InjectionClassLoader.Strategy.INSTANCE)
                .getLoaded();
    }




    static StandardExpressionAction buildActionInstance(final Class<? extends StandardExpressionAction> actionClass) {

        try {
            return actionClass.getConstructor((Class<?>[])null).newInstance((Object[])null);
        } catch (final NoSuchMethodException e) {
            throw new ExpressionExecutionException(
                    String.format("Generated bytecode for class %s does not contain an empty constructor", actionClass.getName()), e);
        } catch (final IllegalAccessException|InstantiationException|InvocationTargetException e) {
            throw new ExpressionExecutionException(
                    String.format("Exception thrown while executing constructor for bytecode-generated class %s", actionClass.getName()), e);
        }

    }



    private ByteCodeGenUtils() {
        super();
    }


}
