import java.util.List;

abstract public class NodeElement extends Element {
    abstract public short getX();
    abstract public void setX(short i);
    abstract public short getY();
    abstract public void setY(short i);
    abstract public short getId();
    abstract public void setId(short i);
    abstract public List<Arc> getInArcs();
    abstract public List<Arc> getOutArcs();
}
