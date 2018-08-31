package icst.experiments.json;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 30/08/18
 */
public class CommitJSON {

    public final String sha;
    public final String parent;
    public final String concernedModule;

    public CommitJSON(String sha, String parent, String concernedModule) {
        this.sha = sha;
        this.parent = parent;
        this.concernedModule = concernedModule;
    }
}
