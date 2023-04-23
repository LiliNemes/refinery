/*
 * SPDX-FileCopyrightText: 2021-2023 The Refinery Authors <https://refinery.tools/>
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package tools.refinery.store.query.term.real;

import tools.refinery.store.query.substitution.Substitution;
import tools.refinery.store.query.term.Term;

public class RealAddTerm extends RealBinaryTerm {
	public RealAddTerm(Term<Double> left, Term<Double> right) {
		super(left, right);
	}

	@Override
	public Term<Double> doSubstitute(Substitution substitution, Term<Double> substitutedLeft,
									 Term<Double> substitutedRight) {
		return new RealAddTerm(substitutedLeft, substitutedRight);
	}

	@Override
	protected Double doEvaluate(Double leftValue, Double rightValue) {
		return leftValue + rightValue;
	}

	@Override
	public String toString() {
		return "(%s + %s)".formatted(getLeft(), getRight());
	}
}
