import java.util.Comparator;

public class PointComparator implements Comparator<Point>
{
    @Override
    //Compare points based on f value
    public int compare(Point x, Point y)
    {
        if(x == null || y == null){
            System.out.println ("Error: PointComparator");
            return 0;
        }
        if (x.f < y.f)
        {
            return -1;
        }
        if (x.f > y.f)
        {
            return 1;
        }
        return 0;
    }
}
