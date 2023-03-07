package tools.refinery.store.query.term;

import org.jetbrains.annotations.Nullable;
import tools.refinery.store.query.equality.LiteralEqualityHelper;

import java.util.Set;

public abstract sealed class AnyDataVariable extends Variable implements AnyTerm permits DataVariable {
	protected AnyDataVariable(String name) {
		super(name);
	}

	@Override
	public NodeVariable asNodeVariable() {
		throw new IllegalStateException("%s is a data variable".formatted(this));
	}

	@Override
	public boolean equalsWithSubstitution(LiteralEqualityHelper helper, AnyTerm other) {
		return other instanceof AnyDataVariable dataVariable && helper.variableEqual(this, dataVariable);
	}

	@Override
	public Set<AnyDataVariable> getInputVariables() {
		return Set.of(this);
	}

	@Override
	public abstract AnyDataVariable renew(@Nullable String name);

	@Override
	public abstract AnyDataVariable renew();
}
