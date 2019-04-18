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
package org.vexprel.standard;

import java.util.ArrayList;

import org.vexprel.Expression;
import org.vexprel.standard.step.ObjectPropertyExpressionStep;
import org.vexprel.standard.step.StandardExpressionStep;

class StandardExpressionParser {


    StandardExpressionParser() {
        super();
    }


    private static void validateArguments(final String expression) {
        if (expression == null) {
            throw new IllegalArgumentException("Cannot parse null expression");
        }
        if (expression.isEmpty()) {
            throw new IllegalArgumentException("Cannot parse empty expression");
        }
    }


    Expression parse(final String expression) {

        validateArguments(expression);

        final ArrayList<StandardExpressionStep> steps = new ArrayList<>(3);

        int offset = 0;
        int dotpos = -1;
        while ((dotpos = expression.indexOf('.', offset)) != -1) {
            steps.add(new ObjectPropertyExpressionStep(expression.substring(offset, dotpos)));
            offset = dotpos + 1;
        }
        steps.add(new ObjectPropertyExpressionStep(expression.substring(offset)));

        return new StandardExpression(steps.toArray(new StandardExpressionStep[steps.size()]));

    }

}
