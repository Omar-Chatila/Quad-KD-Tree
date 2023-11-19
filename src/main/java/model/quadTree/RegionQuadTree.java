package model.quadTree;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.List;

public class RegionQuadTree extends QuadTree<Pixel> {

    public RegionQuadTree(Area square, List<Pixel> elements) {
        super(square, elements);
    }

    public RegionQuadTree(Image image) {
        super(new Area(0, image.getWidth(), 0, image.getHeight()));

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
        if (isMixedNode()) {
            super.partition();
            this.northEast.buildTree();
            this.northWest.buildTree();
            this.southWest.buildTree();
            this.southEast.buildTree();
        }
    }

    // sub Image contains more than one color ("grey node")
    private boolean isMixedNode() {
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
}
