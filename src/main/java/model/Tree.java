package model;

import model.quadTree.Area;

import java.util.HashSet;

public abstract class Tree {
    public abstract HashSet<Point> query(Area queryRectangle);

    public abstract void buildTree();
}
