import javax.xml.bind.annotation.*;
import java.util.ArrayList;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}id"/&gt;
 *         &lt;element ref="{}x"/&gt;
 *         &lt;element ref="{}y"/&gt;
 *         &lt;element ref="{}label"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "id",
    "x",
    "y",
    "label"
})
@XmlRootElement(name = "transition")
public class Transition extends NodeElement {

    private short id;
    private short x;
    private short y;
    @XmlElement(required = true)
    private String label;
    @XmlTransient
    private ArrayList<Arc> inArcs = new ArrayList<>();
    @XmlTransient
    private ArrayList<Arc> outArcs = new ArrayList<>();

    /**
     * Gets the value of the id property.
     * 
     */


    public short getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(short value) {
        this.id = value;
    }

    /**
     * Gets the value of the x property.
     * 
     */
    public short getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     * 
     */
    public void setX(short value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     * 
     */
    public short getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     * 
     */
    public void setY(short value) {
        this.y = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    public boolean canFire(PetriNet petriNet){
        for(Arc a: inArcs){
            if(a.getMultiplicity() > ((Place)petriNet.findById(a.getSourceId())).getTokens()){
                return false;
            }
        }
        return true;
    }
    public void fire(PetriNet petriNet){
        for(Arc a: inArcs){
            ((Place)petriNet.findById(a.getSourceId())).removeTokens(a.getMultiplicity());
        }
        for(Arc a: outArcs){
            ((Place)petriNet.findById(a.getDestinationId())).addTokens(a.getMultiplicity());
        }
    }

    public void addArcToTransition(Arc a){
        if(a.getDestinationId() == this.getId()){
            inArcs.add(a);
        }else {
            outArcs.add(a);
        }
    }

    public ArrayList<Arc> getInArcs() {
        return inArcs;
    }

    public ArrayList<Arc> getOutArcs() {
        return outArcs;
    }
}
