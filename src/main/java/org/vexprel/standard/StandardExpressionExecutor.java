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

import org.vexprel.Expression;
import org.vexprel.context.ExpressionContext;
import org.vexprel.exptarget.ExpressionTarget;
import org.vexprel.standard.action.StandardExpressionAction;
import org.vexprel.standard.action.StandardExpressionActionFactory;
import org.vexprel.standard.step.StandardExpressionStep;

class StandardExpressionExecutor {

    private final StandardExpressionActionFactory expressionActionFactory;


    StandardExpressionExecutor(final StandardExpressionActionFactory expressionActionFactory) {
        super();
        this.expressionActionFactory = expressionActionFactory;
    }


    private static void validateArguments(
            final ExpressionContext context, final Expression expression, final ExpressionTarget target) {

        if (context == null) {
            throw new IllegalArgumentException("Cannot execute on null context");
        }
        if (expression == null) {
            throw new IllegalArgumentException("Cannot execute null expression");
        }
        if (target == null) {
            throw new IllegalArgumentException("Cannot execute on null target");
        }

    }


    Object execute(final ExpressionContext context, final StandardExpression expression, final ExpressionTarget target) {

        validateArguments(context, expression, target);

        final StandardExpressionStep[] steps = expression.getSteps();

        Object current = target.getTargetObject();

        for (int i = 0; i < steps.length; i++) {
            final StandardExpressionAction action = this.expressionActionFactory.build(steps[i], current.getClass());
            current = action.execute(current);
        }

        return current;
    }

}
