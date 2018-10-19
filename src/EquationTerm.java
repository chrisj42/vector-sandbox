public interface EquationTerm<T> {
	void updateInput();
	
	T setEquation(Equation eq);
	Equation getEquation(); // initially, this is going to be null, but that's only because objects implementing this interface are mainly constructed as parameters in the Equation constructor, so the Equation can't simply be passed in during instantiation. However, the Equation constructor will immediately use the above method passing itself, so provided this system, this shouldn't ever get called before the Equation is set. Unless subclasses of this try and call it in the constructor... but I'll just have to make sure not to do that.
	
	String getLabel();
	
	/*@FunctionalInterface
	interface TermFetcher<T> {
		T get(Equation eq, String label);
	}
	
	@FunctionalInterface
	interface TermChangeApplicator<T> {
		void applyChange(T obj, float value);
	}
	
	default FloatChangeApplicator applicatorTemplate(TermFetcher<T> fetcher, TermChangeApplicator<T> customAction) {
		return applicatorTemplate(value -> customAction.applyChange(fetcher.get(getEquation(), getLabel()), value));
	}*/
	default FloatChangeApplicator applicatorTemplate(FloatChangeApplicator customAction) {
		return value -> {
			customAction.applyChange(value);
			getEquation().recalcOutputs();
			updateInput();
		};
	}
}
