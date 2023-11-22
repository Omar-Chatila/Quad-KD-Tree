package model.quadTree;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RegionQuadTree extends QuadTree<Pixel> {
    private Color blendedColor;

    public RegionQuadTree(Area square, List<Pixel> elements) {
        super(square, elements);
    }

    public RegionQuadTree(Image image) {
        super(new Area(0, image.getWidth(), 0, image.getHeight()));
        PixelReader pixelReader = image.getPixelReader();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < (image.getHeight()); y++) {
                this.elements.add(new Pixel(x, y, pixelReader.getColor(x, y)));
            }
        }
    }

    @Override
    protected QuadTree<Pixel> createSubtree(List<Pixel> elements, Area quadrant) {
        return new RegionQuadTree(quadrant, elements);
    }

    @Override
    public HashSet<Pixel> query(Area queryRectangle) {
        return null;
    }

    @Override
    public void buildTree() {
        if (this.isMixedNode()) {
            this.blendedColor = getBlendedColor(this.elements);
            super.partition();
            if (this.northEast != null)
                this.northEast.buildTree();
            if (this.northWest != null)
                this.northWest.buildTree();
            if (this.southWest != null)
                this.southWest.buildTree();
            if (this.southEast != null)
                this.southEast.buildTree();
        } else if (!this.elements.isEmpty()) {
            this.blendedColor = elements.get(0).color();
            this.elements.subList(1, elements.size()).clear();
        }
    }

    public List<RegionQuadTree> getCropped(Area queryRectangle) {
        List<RegionQuadTree> result = new ArrayList<>();
        if (this.isNodeLeaf()) {
            if (queryRectangle.containsArea(this.square)) {
                result.add(this);
            } else if (this.square.intersects(queryRectangle)) {
                RegionQuadTree toAdd = new RegionQuadTree(this.square.intersection(queryRectangle), this.elements);
                toAdd.blendedColor = this.blendedColor;
                result.add(toAdd);
            }
        }
        if (this.northEast != null && queryRectangle.intersects(this.northEast.square)) {
            result.addAll(this.northEast.getCropped(queryRectangle));
        }

        if (this.northWest != null && queryRectangle.intersects(this.northWest.square)) {
            result.addAll(this.northWest.getCropped(queryRectangle));
        }
        if (this.southEast != null && queryRectangle.intersects(this.southEast.square)) {
            result.addAll(this.southEast.getCropped(queryRectangle));
        }
        if (this.southWest != null && queryRectangle.intersects(this.southWest.square)) {
            result.addAll(this.southWest.getCropped(queryRectangle));
        }
        return result;
    }

    public void rotate(RegionQuadTree node) {
        if (node != null) {
            RegionQuadTree temp = (RegionQuadTree) node.northEast;
            node.northEast = node.northWest;
            node.northWest = node.southWest;
            node.southWest = node.southEast;
            node.southEast = temp;

            if (node.northEast != null)
                node.northEast.square = node.northEast.square.rotate90degrees();
            if (node.northWest != null)
                node.northWest.square = node.northWest.square.rotate90degrees();
            if (node.southWest != null)
                node.southWest.square = node.southWest.square.rotate90degrees();
            if (node.southEast != null)
                node.southEast.square = node.southEast.square.rotate90degrees();

            rotate((RegionQuadTree) node.northEast);
            rotate((RegionQuadTree) node.northWest);
            rotate((RegionQuadTree) node.southWest);
            rotate((RegionQuadTree) node.southEast);
        }
    }


    // sub Image contains more than one color ("grey node")
    public boolean isMixedNode() {
        for (Pixel element : elements) {
            if (!elements.get(0).equals(element)) {
                return true;
            }
        }
        return false;
    }

    private Color getBlendedColor(List<Pixel> colors) {
        int n = colors.size();
        double red = 0.0, green = 0.0, blue = 0.0, opacity = 0.0;
        for (Pixel pixel : colors) {
            Color color = pixel.color();
            red += color.getRed();
            green += color.getGreen();
            blue += color.getBlue();
            opacity += color.getOpacity();
        }
        return new Color(red / n, green / n, blue / n, opacity / n);
    }

    public List<RegionQuadTree> gatherLeaves() {
        List<RegionQuadTree> leaves = new ArrayList<>();
        gatherLeavesHelper(this, leaves);
        return leaves;
    }

    public List<RegionQuadTree> getNodesAtLevel(int level) {
        List<RegionQuadTree> leaves = new ArrayList<>();
        nodesAtLevelHelper(this, leaves, level);
        return leaves;
    }

    private void gatherLeavesHelper(RegionQuadTree node, List<RegionQuadTree> leaves) {
        if (node != null) {
            if (node.isNodeLeaf() && !node.getElements().isEmpty()) {
                leaves.add(node);
            } else {
                gatherLeavesHelper((RegionQuadTree) node.southEast, leaves);
                gatherLeavesHelper((RegionQuadTree) node.southWest, leaves);
                gatherLeavesHelper((RegionQuadTree) node.northWest, leaves);
                gatherLeavesHelper((RegionQuadTree) node.northEast, leaves);
            }
        }
    }

    private void nodesAtLevelHelper(RegionQuadTree node, List<RegionQuadTree> nodes, int level) {
        if (node != null && level >= 0) {
            nodes.add(node);
            nodesAtLevelHelper((RegionQuadTree) node.northEast, nodes, level - 1);
            nodesAtLevelHelper((RegionQuadTree) node.northWest, nodes, level - 1);
            nodesAtLevelHelper((RegionQuadTree) node.southWest, nodes, level - 1);
            nodesAtLevelHelper((RegionQuadTree) node.southEast, nodes, level - 1);
        }
    }

    public int countLeaves(RegionQuadTree node) {
        if (node != null) {
            int count = node.isNodeLeaf() && !node.elements.isEmpty() ? 1 : 0;
            return count + countLeaves((RegionQuadTree) node.northEast) +
                    countLeaves((RegionQuadTree) node.northWest) +
                    countLeaves((RegionQuadTree) node.southEast) +
                    countLeaves((RegionQuadTree) node.southWest);

        }
        return 0;
    }

    public Color getBlendedColor() {
        return blendedColor;
    }
}
