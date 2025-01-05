package keyhub.gitkit.core.entity;

public interface CommitHistory<ID> extends Entity<ID> {
	<R extends CommitDiff<?>> R commitDiff();
}
