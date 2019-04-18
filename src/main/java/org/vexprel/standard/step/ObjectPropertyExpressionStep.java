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
package org.vexprel.standard.step;

public class ObjectPropertyExpressionStep implements StandardExpressionStep {

    private final String propertyName;
    private final String getterMethodName;


    public ObjectPropertyExpressionStep(final String propertyName) {
        super();
        if (propertyName == null || propertyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Property name cannot be null or empty");
        }
        this.propertyName = propertyName;
        this.getterMethodName = "get" + (Character.toUpperCase(propertyName.charAt(0))) + propertyName.substring(1);
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public String getGetterMethodName() {
        return this.getterMethodName;
    }

    @Override
    public String getStringRepresentation() {
        return this.propertyName;
    }


    @Override
    public String toString() {
        return String.format("(ObjectProperty: '%s')",this.propertyName);
    }

}
