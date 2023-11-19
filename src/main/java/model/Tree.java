package model;

import model.quadTree.Area;

import java.util.HashSet;

public abstract class Tree<T> {
    public abstract HashSet<T> query(Area queryRectangle);

    public abstract void buildTree();
}
