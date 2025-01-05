package keyhub.gitkit.core.entity;

public interface CommitDiff<ID> extends Entity<ID> {
    <R extends CommitHistory<?>> R commitHistory();
    String recentContents();
    String commitContents();
}