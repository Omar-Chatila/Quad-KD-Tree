package model.quadTree;

public interface HasCoordinates {
    double x();

    double y();

    double distance(HasCoordinates searchPoint);
}
