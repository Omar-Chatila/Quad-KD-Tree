package model;

import model.quadTree.Area;

import java.util.List;

public abstract class Tree<T> {
    public abstract List<T> query(Area queryRectangle);

    public abstract void buildTree();
}
