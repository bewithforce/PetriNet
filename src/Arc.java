import javax.xml.bind.annotation.*;
import java.awt.geom.Line2D;


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
 *         &lt;element ref="{}type"/&gt;
 *         &lt;element ref="{}sourceId"/&gt;
 *         &lt;element ref="{}destinationId"/&gt;
 *         &lt;element ref="{}multiplicity"/&gt;
 *         &lt;element ref="{}breakPoint" minOccurs="0"/&gt;
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
    "type",
    "sourceId",
    "destinationId",
    "multiplicity",
    "breakPoint"
})
@XmlRootElement(name = "arc")
public class Arc extends Element{

    private short id;
    @XmlElement(required = true)
    private String type;
    private short sourceId;
    private short destinationId;
    private short multiplicity;
    private BreakPoint breakPoint;

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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the sourceId property.
     * 
     */
    public short getSourceId() {
        return sourceId;
    }

    /**
     * Sets the value of the sourceId property.
     * 
     */
    public void setSourceId(short value) {
        this.sourceId = value;
    }

    /**
     * Gets the value of the destinationId property.
     * 
     */
    public short getDestinationId() {
        return destinationId;
    }

    /**
     * Sets the value of the destinationId property.
     * 
     */
    public void setDestinationId(short value) {
        this.destinationId = value;
    }

    /**
     * Gets the value of the multiplicity property.
     * 
     */
    public short getMultiplicity() {
        return multiplicity;
    }

    /**
     * Sets the value of the multiplicity property.
     * 
     */
    public void setMultiplicity(short value) {
        this.multiplicity = value;
    }

    /**
     * Gets the value of the breakPoint property.
     * 
     * @return
     *     possible object is
     *     {@link BreakPoint }
     *     
     */
    public BreakPoint getBreakPoint() {
        return breakPoint;
    }

    /**
     * Sets the value of the breakPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link BreakPoint }
     *     
     */
    public void setBreakPoint(BreakPoint value) {
        this.breakPoint = value;
    }

    public Line2D getLine(PetriNet petriNet){
        Line2D.Double line = new Line2D.Double();
        if(petriNet.findById(getSourceId()) instanceof Place){
            line.x1 = ((Place) petriNet.findById(getSourceId())).getX();
            line.y1 = ((Place) petriNet.findById(getSourceId())).getY();
            line.x2 = ((Transition) petriNet.findById(getDestinationId())).getX();
            line.y2 = ((Transition) petriNet.findById(getDestinationId())).getY();
        }else{
            line.x1 = ((Transition) petriNet.findById(getSourceId())).getX();
            line.y1 = ((Transition) petriNet.findById(getSourceId())).getY();
            line.x2 = ((Place) petriNet.findById(getDestinationId())).getX();
            line.y2 = ((Place) petriNet.findById(getDestinationId())).getY();
        }
        return line;
    }

}
