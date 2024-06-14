package br.com.jjconsulting.mobile.dansales.util;

import com.unnamed.b.atv.model.TreeNode;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.TreeNodePage;
import br.com.jjconsulting.mobile.dansales.model.TreeNodePageConfiguration;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.jjlib.base.Tree;

public class TreeNodeUtils {

    public static final int DEFAULT_PAGE_SIZE = 100;
    public static final int DEFAULT_LEVEL_USAGE = -1;

    private TreeNodeUtils() {

    }

    /**
     * Helper to build a TreeNode (for AndroidTreeView) using a generic Tree.
     */
    public static <T> void buildTreeNode(TreeNode parentTreeNode, Tree<T> parentTree) {
        for (Tree<T> tree : parentTree.getChildren()) {
            TreeNode treeNode = new TreeNode(tree.getData());
            parentTreeNode.addChild(treeNode);
            buildTreeNode(treeNode, tree);
        }
    }

    /**
     * Helper to build a TreeNode (for AndroidTreeView) using a generic Tree.
     */
    public static <T> void buildTreeNode(TreeNode parentTreeNode, Tree<T> parentTree, int level,
                                         int currentIndex, TreeNodePageConfiguration pageConfig) {
        // if there's no children, return
        if (parentTree.getChildren().size() == 0) {
            return;
        }

        // check if the level is requested to use pagination
        boolean usePagination = pageConfig.getLevelUsage() == -1
                || pageConfig.getLevelUsage() >= level;

        // if paging is requested but there's no data to make more than
        // one page, then there's no need to page
        if (usePagination && currentIndex == 0
                && parentTree.getChildren().size() <= pageConfig.getPageSize()) {
            usePagination = false;
        }

        // if there's no need for pagination, load the TreeNode as usual
        if (!usePagination) {
            for (int i = 0; i < parentTree.getChildren().size(); i++) {
                Tree<T> tree = parentTree.getChildren().get(i);
                TreeNode treeNode = new TreeNode(tree.getData());
                parentTreeNode.addChild(treeNode);
                buildTreeNode(treeNode, tree, level + 1, 0, pageConfig);
            }
            return;
        }

        // otherwise:
        boolean needsMorePage = parentTree.getChildren().size() - currentIndex - 1 > pageConfig.getPageSize();
        int finalIndex = needsMorePage ? currentIndex + pageConfig.getPageSize() - 1
                : parentTree.getChildren().size() - 1;

        TreeNodePage page = new TreeNodePage(currentIndex, finalIndex, level, pageConfig.getLabel());
        TreeNode pageAsTreeNode = new TreeNode(page);

        for (int i = currentIndex; i <= finalIndex; i++) {
            Tree<T> tree = parentTree.getChildren().get(i);
            TreeNode treeNode = new TreeNode(tree.getData());
            pageAsTreeNode.addChild(treeNode);
            buildTreeNode(treeNode, tree, level + 1, 0, pageConfig);
        }

        parentTreeNode.addChild(pageAsTreeNode);

        if (needsMorePage) {
            buildTreeNode(parentTreeNode, parentTree, level,
                    currentIndex + pageConfig.getPageSize(), pageConfig);
        }
    }

    /**
     * Helper to select the nodes (as Usuario) based on the given list.
     */
    public static void selectUsuariosInTreeNode(TreeNode parentTreeNode, List<Usuario> usuarios) {
        if (usuarios == null || usuarios.size() == 0) {
            return;
        }

        for (TreeNode treeNode : parentTreeNode.getChildren()) {
            if (treeNode.getValue().getClass().equals(Usuario.class)) {
                // we need to get the tree node value (usuário)
                Usuario treeNodeUsuario = (Usuario)treeNode.getValue();

                // then we search for this usário in the given list of usuários (the selected ones)
                Usuario selectedUsuario = null;
                for (int i = 0; i < usuarios.size(); i++) {
                    if (treeNodeUsuario.getCodigo().equals(usuarios.get(i).getCodigo())) {
                        selectedUsuario = usuarios.get(i);
                        break;
                    }
                }

                if (selectedUsuario != null) {
                    treeNode.setSelected(true);
                    usuarios.remove(selectedUsuario);
                }
            }

            selectUsuariosInTreeNode(treeNode, usuarios);
        }
    }

}
