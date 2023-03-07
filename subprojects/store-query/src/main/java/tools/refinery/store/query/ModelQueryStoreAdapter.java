package tools.refinery.store.query;

import tools.refinery.store.adapter.ModelStoreAdapter;
import tools.refinery.store.model.Model;
import tools.refinery.store.query.dnf.AnyQuery;
import tools.refinery.store.query.view.AnyRelationView;

import java.util.Collection;

public interface ModelQueryStoreAdapter extends ModelStoreAdapter {
	Collection<AnyRelationView> getRelationViews();

	Collection<AnyQuery> getQueries();

	@Override
	ModelQueryAdapter createModelAdapter(Model model);
}
