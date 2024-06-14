package br.com.jjconsulting.mobile.jjlib.base;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {

    private T data;
    private Tree<T> parent;
    private List<Tree<T>> children;

    public static Tree root() {
        return new Tree<>();
    }

    private Tree() {
        this.children = new ArrayList<>();
    }

    public Tree(T data) {
        this.data = data;
        this.children = new ArrayList<>();
    }

    public T getData() {
        return data;
    }

    public Tree<T> getParent() {
        return parent;
    }

    public List<Tree<T>> getChildren() {
        return children;
    }

    public boolean isRoot() {
        return parent == null && data == null;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    public Tree<T> addChild(T child) {
        Tree<T> childNode = new Tree<T>(child);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

    public int size() {
        Tree<T> root = getRoot(this);
        return countChildrenNodes(root, 0);
    }

    public int countChildrenNodes(Tree<T> tree, int total) {
        int newTotal = total;

        for (Tree<T> t : tree.getChildren()) {
            newTotal = countChildrenNodes(t, ++newTotal);
        }

        return newTotal;
    }




    private Tree<T> getRoot(Tree<T> tree) {
        return tree.isRoot() ? tree : getRoot(tree.parent);
    }
}
