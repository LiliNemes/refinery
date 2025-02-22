/*
 * SPDX-FileCopyrightText: 2023 The Refinery Authors <https://refinery.tools/>
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package tools.refinery.store.reasoning.translator.crossreference;

import tools.refinery.logic.term.truthvalue.TruthValue;
import tools.refinery.store.reasoning.ReasoningAdapter;
import tools.refinery.store.reasoning.refinement.ConcreteSymbolRefiner;
import tools.refinery.store.reasoning.refinement.PartialInterpretationRefiner;
import tools.refinery.store.reasoning.representation.PartialRelation;
import tools.refinery.store.reasoning.representation.PartialSymbol;
import tools.refinery.store.reasoning.seed.ModelSeed;
import tools.refinery.store.representation.Symbol;
import tools.refinery.store.tuple.Tuple;

import java.util.Objects;
import java.util.Set;

class UndirectedCrossReferenceRefiner extends ConcreteSymbolRefiner<TruthValue, Boolean> {
	private final PartialRelation sourceType;
	private final Set<PartialRelation> supersets;
	private PartialInterpretationRefiner<TruthValue, Boolean> sourceRefiner;
	private PartialInterpretationRefiner<TruthValue, Boolean>[] supersetRefiners;

	protected UndirectedCrossReferenceRefiner(ReasoningAdapter adapter,
											  PartialSymbol<TruthValue, Boolean> partialSymbol,
											  Symbol<TruthValue> concreteSymbol, PartialRelation sourceType,
											  Set<PartialRelation> supersets) {
		super(adapter, partialSymbol, concreteSymbol);
		this.sourceType = sourceType;
		this.supersets = supersets;
	}

	@Override
	public void afterCreate() {
		sourceRefiner = getAdapter().getRefiner(sourceType);
		supersetRefiners = getSupersetRefiners(supersets);
	}

	private PartialInterpretationRefiner<TruthValue, Boolean>[] getSupersetRefiners(Set<PartialRelation> relations) {
		// Creation of array with generic member type.
		@SuppressWarnings("unchecked")
		var refiners = (PartialInterpretationRefiner<TruthValue, Boolean>[])
				new PartialInterpretationRefiner<?, ?>[relations.size()];
		var i = 0;
		for (var relation : relations) {
			refiners[i] = getAdapter().getRefiner(relation);
			i++;
		}
		return refiners;
	}

	@Override
	public boolean merge(Tuple key, TruthValue value) {
		int source = key.get(0);
		int target = key.get(1);
		var currentValue = get(key);
		var mergedValue = currentValue.meet(value);
		if (!Objects.equals(currentValue, mergedValue)) {
			var oldValue = put(key, mergedValue);
			if (source != target) {
				var inverseOldValue = put(Tuple.of(target, source), mergedValue);
				if (!Objects.equals(oldValue, inverseOldValue)) {
					return false;
				}
			}
		}
		if (value.must()) {
			return sourceRefiner.merge(Tuple.of(source), TruthValue.TRUE) &&
					sourceRefiner.merge(Tuple.of(target), TruthValue.TRUE) && mergeSupersets(key);
		}
		return true;
	}

	private boolean mergeSupersets(Tuple key) {
		// Use classic for loop to avoid allocating an iterator.
		//noinspection ForLoopReplaceableByForEach
		for (int i = 0; i < supersetRefiners.length; i++) {
			if (!supersetRefiners[i].merge(key, TruthValue.TRUE)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void afterInitialize(ModelSeed modelSeed) {
		var linkType = getPartialSymbol();
		var cursor = modelSeed.getCursor(linkType);
		while (cursor.move()) {
			var value = cursor.getValue();
			if (value.must()) {
				var key = cursor.getKey();
				boolean mergedTypes = sourceRefiner.merge(Tuple.of(key.get(0)), TruthValue.TRUE) &&
						sourceRefiner.merge(Tuple.of(key.get(1)), TruthValue.TRUE);
				if (!mergedTypes) {
					throw new IllegalArgumentException("Failed to merge end types of reference %s for key %s"
							.formatted(linkType, key));
				}
				if (!mergeSupersets(key)) {
					throw new IllegalArgumentException("Failed to merge supersets of reference %s for key %s"
							.formatted(linkType, key));
				}
			}
		}
	}

	public static Factory<TruthValue, Boolean> of(Symbol<TruthValue> concreteSymbol, PartialRelation sourceType,
												  Set<PartialRelation> supersets) {
		return (adapter, partialSymbol) -> new UndirectedCrossReferenceRefiner(adapter, partialSymbol, concreteSymbol,
				sourceType, supersets);
	}
}
