package model.quadTree;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RegionQuadTree extends QuadTree<Pixel> {

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
            super.partition();
            if (this.northEast != null)
                this.northEast.buildTree();
            if (this.northWest != null)
                this.northWest.buildTree();
            if (this.southWest != null)
                this.southWest.buildTree();
            if (this.southEast != null)
                this.southEast.buildTree();
        } else if (this.elements.size() > 1) {
            this.elements.subList(1, elements.size()).clear();
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

    private void gatherLeavesHelper(RegionQuadTree node, List<RegionQuadTree> leaves) {
        if (node != null) {
            if (node.isNodeLeaf() && !node.getElements().isEmpty()) {
                leaves.add(node);
            } else {
                gatherLeavesHelper((RegionQuadTree) node.northEast, leaves);
                gatherLeavesHelper((RegionQuadTree) node.northWest, leaves);
                gatherLeavesHelper((RegionQuadTree) node.southWest, leaves);
                gatherLeavesHelper((RegionQuadTree) node.southEast, leaves);
            }
        }
    }

    public int countLeaves(RegionQuadTree node) {
        if (node != null) {
            if (node.isNodeLeaf()) {
                return 1 + countLeaves((RegionQuadTree) node.northEast) +
                        countLeaves((RegionQuadTree) node.northWest) +
                        countLeaves((RegionQuadTree) node.southEast) +
                        countLeaves((RegionQuadTree) node.southWest);
            } else {
                return countLeaves((RegionQuadTree) node.northEast) +
                        countLeaves((RegionQuadTree) node.northWest) +
                        countLeaves((RegionQuadTree) node.southEast) +
                        countLeaves((RegionQuadTree) node.southWest);
            }
        }
        return 0;
    }
}
