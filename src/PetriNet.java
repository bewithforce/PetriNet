import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class PetriNet {
    /*Класс сети Петри

     */
    private List<Place> places              = new ArrayList<>();
    private List<Transition> transitions    = new ArrayList<>();
    private List<Arc> arcs                  = new ArrayList<>();



    public List<Place> getPlaces() {
        return places;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public List<Arc> getArcs() {
        return arcs;
    }

    //добавляем элементы в сеть. Если элемент дуга, то смотрим чтобы её вершины тоже были в сети
    public void add(Element o) throws Exception{
        if (o instanceof Arc) {
            if((findById(((Arc)o).getSourceId()) instanceof Transition || findById(((Arc)o).getDestinationId()) instanceof Transition ) &&
                    (findById(((Arc)o).getSourceId()) instanceof Place  || findById(((Arc)o).getDestinationId()) instanceof Place )){
                if(!checkMultiplicity(((Arc) o).getSourceId(), ((Arc) o).getDestinationId())) {
                    arcs.add((Arc) o);
                    if(findById(((Arc)o).getSourceId()) instanceof Transition){
                        ((Transition)findById(((Arc)o).getSourceId())).addArcToTransition((Arc)o);
                        ((Place)findById(((Arc)o).getDestinationId())).addArcToPlace((Arc)o);
                    }else{
                        ((Transition)findById(((Arc)o).getDestinationId())).addArcToTransition((Arc)o);
                        ((Place)findById(((Arc)o).getSourceId())).addArcToPlace((Arc)o);
                    }

                }
            }
            else {
                System.err.println(((Arc) o).getSourceId());
                System.err.println(((Arc) o).getDestinationId());
                throw new Exception("Bad arc");
            }
        } else if (o instanceof Place) {
            places.add((Place) o);
        } else if (o instanceof Transition) {
            transitions.add((Transition) o);
        }
    }



    //поиск элемента по его id
    protected Element findById(int id){
        for (Transition t: transitions) {
            if(t.getId() == id)
                return t;
        }
        for (Place p: places) {
            if(p.getId() == id)
                return p;
        }
        return null;
    }

    private boolean checkMultiplicity(int sourceId, int destinationId){
        for (Arc a: arcs) {
            if(a.getDestinationId() == destinationId && a.getSourceId() == sourceId){
                a.setMultiplicity((short)(a.getMultiplicity() + 1));
                return true;
            }
        }
        return false;
    }

    public String tokensInPlaces(){
        String s = "[";
        for (Place p : places) {
            s += p.getTokens() + ", ";
        }
        s = s.substring(0, s.length() - 2);
        s += "]";
        return s;
    }

    public void makeNet(Document document)throws Exception{
        for(Place p: document.getPlace()){
            add(p);
        }
        for (Transition t : document.getTransition()){
            add(t);
        }
        for(Arc a : document.getArc()){
            add(a);
        }
    }

    public void drawArrow(Graphics g1, int x1, int y1, int x2, int y2, Arc arc) {
        int ARR_SIZE = 4;
        Graphics2D g = (Graphics2D) g1.create();
        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy) - 15;
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(15, 0, len, 0);
        g.drawString(Integer.toString(arc.getMultiplicity()), len/2, 0);
        g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
    }

    public void draw(Canvas canvas){
        Graphics g = canvas.getGraphics();
        g.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        for(Transition t: getTransitions()){
            if(t.canFire(this)) {
                g.setColor(new Color(0,255,0));
                g.drawRect(t.getX() - 15, t.getY() - 15, 30, 30);
                g.fillRect(t.getX() - 15, t.getY() - 15, 30, 30);
            }
            else {
                g.setColor(new Color(255,255,0));
                g.drawRect(t.getX() - 15, t.getY() - 15, 30, 30);
                g.fillRect(t.getX() - 15, t.getY() - 15, 30, 30);
            }
        }
        g.setColor(new Color(0,0,255));
        for(Arc a: getArcs()) {
            int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
                x1 = ((NodeElement) findById(a.getSourceId())).getX();
                y1 = ((NodeElement) findById(a.getSourceId())).getY();
                x2 = ((NodeElement) findById(a.getDestinationId())).getX();
                y2 = ((NodeElement) findById(a.getDestinationId())).getY();

            for (Place p : getPlaces()) {
                if (p.getId() == a.getSourceId()) {
                    x1 = p.getX();
                    y1 = p.getY();
                }
                if (p.getId() == a.getDestinationId()) {
                    x2 = p.getX();
                    y2 = p.getY();
                }
            }
            for (Transition t : getTransitions()) {
                if (t.getId() == a.getSourceId()) {
                    x1 = t.getX();
                    y1 = t.getY();
                }
                if (t.getId() == a.getDestinationId()) {
                    x2 = t.getX();
                    y2 = t.getY();
                }
            }
            drawArrow(g, x1, y1, x2, y2, a);
        }
        for(Place p: getPlaces()){
            g.setColor(new Color(255,0,0));
            g.drawOval(p.getX() - 15,p.getY() - 15,30,30);
            g.fillOval(p.getX() - 15,p.getY() - 15,30,30);
            g.setColor(new Color(0,0,0));
            int tokens = p.getTokens();
            switch (tokens){
                case 0:
                    break;
                case 1:
                    g.fillOval(p.getX() - 1,p.getY() - 1,4,4);
                    break;
                case 2:
                    g.fillOval(p.getX() - 7,p.getY() + 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() - 7,4,4);
                    break;
                case 3:
                    g.fillOval(p.getX() - 7,p.getY() + 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() - 7,4,4);
                    g.fillOval(p.getX() - 1,p.getY() - 1,4,4);
                    break;
                case 4:
                    g.fillOval(p.getX() - 7,p.getY() + 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() - 7,4,4);
                    g.fillOval(p.getX() - 7,p.getY() - 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() + 7,4,4);
                    break;
                case 5:
                    g.fillOval(p.getX() - 7,p.getY() + 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() - 7,4,4);
                    g.fillOval(p.getX() - 7,p.getY() - 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() + 7,4,4);
                    g.fillOval(p.getX() - 1,p.getY() - 1,4,4);
                    break;
                case 6:
                    g.fillOval(p.getX() - 7,p.getY() + 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() - 7,4,4);
                    g.fillOval(p.getX() - 7,p.getY() - 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() + 7,4,4);
                    g.fillOval(p.getX() - 7,p.getY() - 1,4,4);
                    g.fillOval(p.getX() + 7,p.getY() - 1,4,4);
                    break;
                case 7:
                    g.fillOval(p.getX() - 7,p.getY() + 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() - 7,4,4);
                    g.fillOval(p.getX() - 7,p.getY() - 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() + 7,4,4);
                    g.fillOval(p.getX() - 7,p.getY() - 1,4,4);
                    g.fillOval(p.getX() + 7,p.getY() - 1,4,4);
                    g.fillOval(p.getX() - 1,p.getY() - 1,4,4);
                    break;
                case 8:
                    g.fillOval(p.getX() - 7,p.getY() + 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() - 7,4,4);
                    g.fillOval(p.getX() - 7,p.getY() - 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() + 7,4,4);
                    g.fillOval(p.getX() - 7,p.getY() - 1,4,4);
                    g.fillOval(p.getX() + 7,p.getY() - 1,4,4);
                    g.fillOval(p.getX() - 1,p.getY() - 7,4,4);
                    g.fillOval(p.getX() - 1,p.getY() + 7,4,4);
                    break;
                case 9:
                    g.fillOval(p.getX() - 7,p.getY() + 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() - 7,4,4);
                    g.fillOval(p.getX() - 7,p.getY() - 7,4,4);
                    g.fillOval(p.getX() + 7,p.getY() + 7,4,4);
                    g.fillOval(p.getX() - 7,p.getY() - 1,4,4);
                    g.fillOval(p.getX() + 7,p.getY() - 1,4,4);
                    g.fillOval(p.getX() - 1,p.getY() - 7,4,4);
                    g.fillOval(p.getX() - 1,p.getY() + 7,4,4);
                    g.fillOval(p.getX() - 1,p.getY() - 1,4,4);
                    break;
                default:
                    g.drawString(Integer.toString(tokens), p.getX() - 6, p.getY() + 2);
            }
        }
    }

    public static void drawArrowWithoutArc(Graphics g1, int x1, int y1, int x2, int y2) {
        int ARR_SIZE = 4;
        Graphics2D g = (Graphics2D) g1.create();
        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy) - 15;
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(15, 0, len, 0);
        g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
    }


    public Document makeDocument(){
        Document doc = new Document();
        doc.setArc(arcs);
        doc.setPlace(places);
        doc.setTransition(transitions);
        return doc;
    }

}
