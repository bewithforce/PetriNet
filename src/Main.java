import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ConcurrentModificationException;

public class Main extends JFrame{
    private final int diam = 15;
    private PetriNet net = new PetriNet();
    private Document document = new Document();
    private File openedFile;

    private enum WhatButtonPressed {AddTokens, RemoveTokens, AddPlace, AddTransition, AddArc, ReplaceItem, RemoveItem, Nothing}
    private WhatButtonPressed buttonFlag = WhatButtonPressed.Nothing;
    private boolean drawingArc = false;
    private boolean replacingItem = false;
    private Element beginningElement;
    private int x2,y2;

    private Canvas canvas;
    private JButton addingTokens, removingTokens, addPlace, addTransition, addArc, removeItem, replaceItem;
    private JMenuItem saveNetwork, exportNetwork, importNetwork;
    private JMenuBar jMenuBar;
    private JMenu jMenu;

    private short idCounter = 0;


    private Main(){
        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setResizable(false);
        setTitle("Program");

        canvas = new Canvas();
        jMenuBar = new JMenuBar();
        jMenu = new JMenu("Menu");
        saveNetwork = new JMenuItem("save");
        exportNetwork = new JMenuItem("save as...");
        importNetwork = new JMenuItem("import network  ");

        canvas.setSize(getSize());
        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                switch (buttonFlag) {
                    case Nothing:
                        for (Transition t : net.getTransitions()) {
                            if ((Math.abs(x - t.getX()) <= diam) && (Math.abs(y - t.getY()) <= diam) && (t.canFire(net))) {
                                t.fire(net);
                                net.draw(canvas);
                                break;
                            }
                        }
                        break;
                    case AddPlace:
                        Place p = new Place();
                        p.setX((short) x);
                        p.setY((short) y);
                        p.setTokens((short) 0);
                        p.setStatic(false);
                        if(net.findById(idCounter) == null){
                            p.setId(idCounter++);
                        }
                        else {
                            while (net.findById(idCounter) != null) {
                                idCounter++;
                            }
                            p.setId(idCounter);
                        }
                        try {
                            net.add(p);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        net.draw(canvas);
                        break;
                    case AddTransition:
                        Transition t = new Transition();
                        t.setX((short) x);
                        t.setY((short) y);
                        if(net.findById(idCounter) == null){
                            t.setId(idCounter++);
                        }
                        else {
                            while (net.findById(idCounter) != null) {
                                idCounter++;
                            }
                            t.setId(idCounter);
                        }
                        try {
                            net.add(t);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        net.draw(canvas);
                        break;
                    case AddArc:
                        if (!drawingArc) {
                            for (Place place : net.getPlaces()) {
                                if ((Math.abs(x - place.getX()) <= diam) && (Math.abs(y - place.getY()) <= diam)) {
                                    if (e.getButton() == MouseEvent.BUTTON1) {
                                        drawingArc = true;
                                        beginningElement = place;
                                    }
                                    break;
                                }
                            }
                            for (Transition transition : net.getTransitions()) {
                                if ((Math.abs(x - transition.getX()) <= diam) && (Math.abs(y - transition.getY()) <= diam)) {
                                    if (e.getButton() == MouseEvent.BUTTON1) {
                                        drawingArc = true;
                                        beginningElement = transition;
                                    }
                                    break;
                                }
                            }
                        } else {
                            if (beginningElement instanceof Transition) {
                                for (Place place : net.getPlaces()) {
                                    if ((Math.abs(x - place.getX()) <= diam) && (Math.abs(y - place.getY()) <= diam)) {
                                        if (e.getButton() == MouseEvent.BUTTON1) {
                                            Arc arc = new Arc();
                                            arc.setMultiplicity((short) 1);
                                            arc.setSourceId(((Transition) beginningElement).getId());
                                            arc.setDestinationId(place.getId());
                                            try {
                                                net.add(arc);
                                            } catch (Exception exception) {
                                                exception.printStackTrace();
                                            }
                                            drawingArc = false;
                                            beginningElement = null;
                                            buttonFlag = WhatButtonPressed.Nothing;
                                            addArc.setBackground(Color.WHITE);
                                            net.draw(canvas);
                                        }
                                        break;
                                    }
                                }
                            } else {
                                for (Transition transition : net.getTransitions()) {
                                    if ((Math.abs(x - transition.getX()) <= diam) && (Math.abs(y - transition.getY()) <= diam)) {
                                        if (e.getButton() == MouseEvent.BUTTON1) {
                                            Arc arc = new Arc();
                                            arc.setMultiplicity((short) 1);
                                            arc.setSourceId(((Place) beginningElement).getId());
                                            arc.setDestinationId(transition.getId());
                                            try {
                                                net.add(arc);
                                            } catch (Exception exception) {
                                                exception.printStackTrace();
                                            }
                                            drawingArc = false;
                                            beginningElement = null;
                                            buttonFlag = WhatButtonPressed.Nothing;
                                            addArc.setBackground(Color.WHITE);
                                            net.draw(canvas);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case RemoveItem:
                        for (Place place : net.getPlaces()) {
                            if ((Math.abs(x - place.getX()) <= diam) && (Math.abs(y - place.getY()) <= diam)) {
                                if (e.getButton() == MouseEvent.BUTTON1) {
                                    for (Arc arc : place.getInArcs()) {
                                        ((Transition) net.findById(arc.getSourceId())).getOutArcs().remove(arc);
                                        net.getArcs().remove(arc);
                                    }
                                    for (Arc arc : place.getOutArcs()) {
                                        ((Transition) net.findById(arc.getDestinationId())).getInArcs().remove(arc);
                                        net.getArcs().remove(arc);
                                    }
                                    net.getPlaces().remove(place);
                                }
                                net.draw(canvas);
                                break;
                            }
                        }
                        for (Transition transition : net.getTransitions()) {
                            if ((Math.abs(x - transition.getX()) <= diam) && (Math.abs(y - transition.getY()) <= diam)) {
                                if (e.getButton() == MouseEvent.BUTTON1) {
                                    for (Arc arc : transition.getInArcs()) {
                                        ((Place) net.findById(arc.getSourceId())).getOutArcs().remove(arc);
                                        net.getArcs().remove(arc);
                                    }
                                    for (Arc arc : transition.getOutArcs()) {
                                        ((Place) net.findById(arc.getDestinationId())).getInArcs().remove(arc);
                                        net.getArcs().remove(arc);
                                    }
                                    net.getTransitions().remove(transition);
                                }
                                net.draw(canvas);
                                break;
                            }
                        }
                        try {
                            for (Arc arc : net.getArcs()) {
                                if (arc.getLine(net).contains(e.getX(), e.getY())) {
                                    ((NodeElement) net.findById(arc.getSourceId())).getOutArcs().remove(arc);
                                    ((NodeElement) net.findById(arc.getDestinationId())).getOutArcs().remove(arc);
                                }
                                net.getArcs().remove(arc);
                            }
                        }catch (ConcurrentModificationException exception){}


                        break;
                    case AddTokens:
                        for (Place place : net.getPlaces()) {
                            if ((Math.abs(x - place.getX()) <= diam) && (Math.abs(y - place.getY()) <= diam)) {
                                if(e.getButton() == MouseEvent.BUTTON1) {
                                    place.addTokens((short)1);
                                }
                                net.draw(canvas);
                                break;
                            }
                        }
                        break;
                    case RemoveTokens:
                        for (Place place : net.getPlaces()) {
                            if ((Math.abs(x - place.getX()) <= diam) && (Math.abs(y - place.getY()) <= diam)) {
                                if(e.getButton() == MouseEvent.BUTTON1) {
                                    place.removeTokens((short)1);
                                }
                                net.draw(canvas);
                                break;
                            }
                        }
                        break;
                    case ReplaceItem:
                        if(!replacingItem){
                            for (Place place : net.getPlaces()) {
                                if ((Math.abs(e.getX() - place.getX()) <= diam) && (Math.abs(e.getY() - place.getY()) <= diam)) {
                                    if (e.getButton() == MouseEvent.BUTTON1) {
                                        beginningElement = place;
                                        replacingItem = true;
                                        setCursor(Cursor.MOVE_CURSOR);
                                    }
                                    break;
                                }
                            }
                            for (Transition transition : net.getTransitions()) {
                                if ((Math.abs(e.getX() - transition.getX()) <= 15) && (Math.abs(e.getY() - transition.getY()) <= 15)) {
                                    if (e.getButton() == MouseEvent.BUTTON1) {
                                        beginningElement = transition;
                                        replacingItem = true;
                                        setCursor(Cursor.MOVE_CURSOR);
                                    }
                                    break;
                                }
                            }
                        }
                        else{
                            beginningElement = null;
                            replacingItem = false;
                            setCursor(Cursor.DEFAULT_CURSOR);
                        }
                        break;
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (buttonFlag == WhatButtonPressed.AddArc && drawingArc) {
                    int x1, y1;
                    x1 = ((NodeElement) beginningElement).getX();
                    y1 = ((NodeElement) beginningElement).getY();
                    Graphics g = canvas.getGraphics();
                    g.setColor(new Color(0, 0, 0, 0));
                    PetriNet.drawArrowWithoutArc(g, x1, y1, x2, y2);
                    g.setColor(new Color(0, 0, 255));
                    x2 = e.getX();
                    y2 = e.getY();
                    net.draw(canvas);
                    PetriNet.drawArrowWithoutArc(g, x1, y1, x2, y2);
                } else if (buttonFlag == WhatButtonPressed.ReplaceItem && replacingItem) {
                    if(beginningElement != null) {
                        ((NodeElement)beginningElement).setX((short)e.getX());
                        ((NodeElement)beginningElement).setY((short)e.getY());
                        net.draw(canvas);
                    }
                }
            }
        });


        add(canvas);



        jMenu.setMnemonic(KeyEvent.VK_A);
        jMenuBar.add(jMenu);

        importNetwork.setBounds(5, 5, 90, 30);
        importNetwork.addActionListener((ActionEvent e) -> {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    openedFile = chooser.getSelectedFile();
                    JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                    document = (Document) jaxbUnmarshaller.unmarshal(openedFile);
                    net = new PetriNet();
                    net.makeNet(document);
                    net.draw(canvas);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        exportNetwork.setBounds(5, 5, 90, 30);
        exportNetwork.addActionListener((ActionEvent e) -> {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(this);
                File file;
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = chooser.getSelectedFile();
                    JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
                    Marshaller marshaller = jaxbContext.createMarshaller();
                    marshaller.marshal(net.makeDocument(),file);
                }
            } catch (JAXBException exception) {
                exception.printStackTrace();
            }
        });
        saveNetwork.setBounds(5, 5, 90, 30);
        saveNetwork.addActionListener((ActionEvent e) -> {
            try {
                if (openedFile != null) {
                    JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
                    Marshaller marshaller = jaxbContext.createMarshaller();
                    marshaller.marshal(net.makeDocument(),openedFile);
                }
            } catch (JAXBException exception) {
                exception.printStackTrace();
            }
        });
        jMenu.add(importNetwork);
        jMenu.add(saveNetwork);
        jMenu.add(exportNetwork);

        addingTokens = new JButton(new ImageIcon(Main.class.getResource("Resources/addTokens.png")));
        removingTokens = new JButton(new ImageIcon(Main.class.getResource("Resources/removeTokens.png")));
        addPlace = new JButton(new ImageIcon(Main.class.getResource("Resources/addPlace.png")));
        addTransition = new JButton(new ImageIcon(Main.class.getResource("Resources/addTransition.png")));

        addArc = new JButton(new ImageIcon(Main.class.getResource("Resources/addArc.png")));
        removeItem = new JButton(new ImageIcon(Main.class.getResource("Resources/removeItem.png")));
        replaceItem = new JButton(new ImageIcon(Main.class.getResource("Resources/replaceItem.png")));


        addingTokens.setFocusable(false);
        removingTokens.setFocusable(false);
        addPlace.setFocusable(false);
        addTransition.setFocusable(false);
        addArc.setFocusable(false);
        removeItem.setFocusable(false);
        replaceItem.setFocusable(false);

        addingTokens.setBackground(Color.WHITE);
        removingTokens.setBackground(Color.WHITE);
        addPlace.setBackground(Color.WHITE);
        addTransition.setBackground(Color.WHITE);
        addArc.setBackground(Color.WHITE);
        removeItem.setBackground(Color.WHITE);
        replaceItem.setBackground(Color.WHITE);

        addingTokens.addActionListener((ActionEvent e) -> buttonClickListener(WhatButtonPressed.AddTokens));
        removingTokens.addActionListener((ActionEvent e) -> buttonClickListener(WhatButtonPressed.RemoveTokens));
        addPlace.addActionListener((ActionEvent e) -> buttonClickListener(WhatButtonPressed.AddPlace));
        addTransition.addActionListener((ActionEvent e) -> buttonClickListener(WhatButtonPressed.AddTransition));
        addArc.addActionListener((ActionEvent e) -> buttonClickListener(WhatButtonPressed.AddArc));
        removeItem.addActionListener((ActionEvent e) -> buttonClickListener(WhatButtonPressed.RemoveItem));
        replaceItem.addActionListener((ActionEvent e) -> buttonClickListener(WhatButtonPressed.ReplaceItem));

        jMenuBar.add(addingTokens);
        jMenuBar.add(removingTokens);
        jMenuBar.add(addArc);
        jMenuBar.add(addPlace);
        jMenuBar.add(addTransition);
        jMenuBar.add(removeItem);
        jMenuBar.add(replaceItem);

        setJMenuBar(jMenuBar);
        setVisible(true);
    }
    public static void main(String[] args) {
        Main jFrame = new Main();
    }

    private void buttonClickListener(WhatButtonPressed thisButtonFlag){
        switch (buttonFlag) {
            case AddPlace:
                addPlace.setBackground(Color.WHITE);
                break;
            case AddTransition:
                addTransition.setBackground(Color.WHITE);
                break;
            case AddArc:
                addArc.setBackground(Color.WHITE);
                break;
            case RemoveItem:
                removeItem.setBackground(Color.WHITE);
                break;
            case AddTokens:
                addingTokens.setBackground(Color.WHITE);
                break;
            case RemoveTokens:
                removingTokens.setBackground(Color.WHITE);
                break;
            case ReplaceItem:
                replaceItem.setBackground(Color.WHITE);
                break;
        }
        switch (thisButtonFlag) {
            case AddPlace:
                if(thisButtonFlag != buttonFlag ) {
                    addPlace.setBackground(Color.GRAY);
                }
                break;
            case AddTransition:
                if(thisButtonFlag != buttonFlag ) {
                    addTransition.setBackground(Color.GRAY);
                }
                break;
            case AddArc:
                if(thisButtonFlag != buttonFlag ) {
                    addArc.setBackground(Color.GRAY);
                }
                break;
            case RemoveItem:
                if(thisButtonFlag != buttonFlag ) {
                    removeItem.setBackground(Color.GRAY);
                }
                break;
            case AddTokens:
                if(thisButtonFlag != buttonFlag ) {
                    addingTokens.setBackground(Color.GRAY);
                }
                break;
            case RemoveTokens:
                if(thisButtonFlag != buttonFlag ) {
                    removingTokens.setBackground(Color.GRAY);
                }
                break;
            case ReplaceItem:
                if(thisButtonFlag != buttonFlag ) {
                    replaceItem.setBackground(Color.GRAY);
                }
                break;
        }
        if(thisButtonFlag != buttonFlag) {
            buttonFlag = thisButtonFlag;
        }
        else {
            buttonFlag = WhatButtonPressed.Nothing;
        }
    }
}