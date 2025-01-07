package keyhub.gitkit.apt;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import keyhub.gitkit.core.annotation.GitOperation;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("keyhub.gitkit.core.annotation.GitOperationValidate")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class GitOperationValidateProcessor extends AbstractProcessor {
    private Messager messager;
    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element root : roundEnv.getRootElements()) {
            if (root.getKind() == ElementKind.CLASS) {
                for (Element enclosed : root.getEnclosedElements()) {
                    if (enclosed.getKind() == ElementKind.METHOD) {
                        TreePath methodPath = trees.getPath(enclosed);
                        if (methodPath != null) {
                            LocalGitReferenceScanner scanner = new LocalGitReferenceScanner();
                            scanner.scan(methodPath, null);
                            if (scanner.foundReference()) {
                                // @GitOperation 달려 있는지 확인
                                GitOperation gitOp = enclosed.getAnnotation(GitOperation.class);
                                if (gitOp == null) {
                                    messager.printMessage(
                                            Diagnostic.Kind.ERROR,
                                            "Method referencing localGit/originGit must be annotated with @GitOperation",
                                            enclosed
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }
        // 다른 프로세서로 넘길 수도 있으므로 false, 여기서 끝내려면 true
        return false;
    }

    static class LocalGitReferenceScanner extends TreePathScanner<Void, Void> {
        private boolean foundLocalOrOrigin = false;
        @Override
        public Void visitIdentifier(IdentifierTree node, Void unused) {
            String name = node.getName().toString();
            if ("localGit".equals(name) || "originGit".equals(name)) {
                foundLocalOrOrigin = true;
            }
            return super.visitIdentifier(node, unused);
        }
        public boolean foundReference() {
            return foundLocalOrOrigin;
        }
    }
}

