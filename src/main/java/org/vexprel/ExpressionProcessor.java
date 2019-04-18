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
package org.vexprel;

import org.vexprel.context.EmptyExpressionContext;
import org.vexprel.context.ExpressionContext;
import org.vexprel.exptarget.DefaultExpressionTarget;
import org.vexprel.exptarget.ExpressionTarget;

public interface ExpressionProcessor {

    Expression parse(final String expression);

    default Object execute(final String expression, final Object target) {
        return execute(parse(expression), target);
    }

    default Object execute(final Expression expression, final Object target) {
        return execute(EmptyExpressionContext.INSTANCE, expression, target);
    }

    default Object execute(final ExpressionContext context, final Expression expression, final Object target) {
        return execute(context, expression, new DefaultExpressionTarget(target));
    }

    Object execute(final ExpressionContext context, final Expression expression, final ExpressionTarget target);

}
