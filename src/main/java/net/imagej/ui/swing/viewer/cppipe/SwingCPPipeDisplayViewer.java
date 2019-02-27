package net.imagej.ui.swing.viewer.cppipe;

import ch.epfl.biop.lima.cppipe.CPParam;
import ch.epfl.biop.lima.cppipe.CPPipe;
import net.imagej.ui.swing.viewer.EasySwingDisplayViewer;
import org.scijava.plugin.Plugin;
import org.scijava.ui.viewer.DisplayViewer;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

@Plugin(type = DisplayViewer.class)
public class SwingCPPipeDisplayViewer extends
        EasySwingDisplayViewer<CPPipe> implements TreeSelectionListener {

    public SwingCPPipeDisplayViewer()
    {
        super( CPPipe.class );
    }

    @Override
    protected boolean canView( CPPipe cppipe )
    {
        return true;
    }

    /**
     * Maintains a reference to the object being displayed
     */

    CPPipe pipe = null;
    JLabel labelName;


    public static JTextField input;
    JTextArea display;

    @Override
    protected JPanel createDisplayPanel(CPPipe pipe )
    {
        this.pipe = pipe;
        final JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout());
        labelName = new JLabel( pipe.toString() );
        labelName.setFont( new Font( Font.SERIF, Font.BOLD, 20 ) );
        panel.add( labelName, BorderLayout.NORTH );

        display = new JTextArea(16, 58);
        display.setEditable(false); // set textArea non-editable
        display.setText(pipe.toReadableString());
        JScrollPane scroll = new JScrollPane(display);
        //scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //panel.add(scroll, BorderLayout.CENTER);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Text", scroll);
        panel.add(tabbedPane, BorderLayout.CENTER);

        DefaultMutableTreeNode top =
                new DefaultMutableTreeNode("pipe");
        createNodes(top);

        tree = new JTree(top);

        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);


        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        JScrollPane treeView = new JScrollPane(tree);

        JPanel panelTree = new JPanel();
        panelTree.setLayout(new BorderLayout());
        panelTree.add(treeView, BorderLayout.CENTER);

        input = new JTextField();

        input.addActionListener(e -> {
            System.out.println("ActionInputPerformed");

            System.out.println("Input contains "+input.getText());
            if (selectedParam!=null) {
                System.out.println("Updating param");
                selectedParam.setReadableValue(input.getText());
                this.redraw();
            }

        });


        panelTree.add(input, BorderLayout.SOUTH);
        tabbedPane.addTab("Tree", panelTree);




        return panel;
    }

    private void createNodes(DefaultMutableTreeNode top) {
        //int rootIndex = atlas.ontology.getRootIndex();
        //addNodes(top, rootIndex);
        DefaultMutableTreeNode headernode = new DefaultMutableTreeNode("Header");
        top.add(headernode);
        pipe.header.forEach(param -> {
            headernode.add(new DefaultMutableTreeNode(param));
        });
        pipe.modules.forEach(module -> {
            DefaultMutableTreeNode moduleNode = new DefaultMutableTreeNode(module.params.get(0).getName());
            top.add(moduleNode);
            module.params.forEach(param -> {
                moduleNode.add(new DefaultMutableTreeNode(param));
            });
        });
    }


    private JTree tree;

    @Override
    public void redoLayout()
    {

    }

    @Override
    public void setLabel(final String s) {

    }

    /**
     * Called each time the Bird is declared as an ItemIO.OUTPUT or ItemIO.BOTH parameter
     */

    @Override
    public void redraw()
    {
        labelName.setText(pipe.name);

        display.setText(pipe.toReadableString());
    }

    CPParam selectedParam = null;

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();

        if (node == null)
            //Nothing is selected.
            return;

        Object nodeInfo = node.getUserObject();
        if (nodeInfo instanceof CPParam) {
            selectedParam = (CPParam) nodeInfo;
            input.setText(selectedParam.getReadableValue());
        } else {
            selectedParam = null;
        }
        /*if (node.isLeaf()) {
            BookInfo book = (BookInfo)nodeInfo;
            displayURL(book.bookURL);
        } else {
            displayURL(helpURL);
        }*/
    }
}
