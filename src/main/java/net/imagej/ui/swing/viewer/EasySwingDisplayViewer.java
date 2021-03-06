package net.imagej.ui.swing.viewer;

import org.scijava.display.Display;
import org.scijava.display.event.DisplayDeletedEvent;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.ui.UserInterface;
import org.scijava.ui.swing.SwingUI;
import org.scijava.ui.viewer.AbstractDisplayViewer;
import org.scijava.ui.viewer.DisplayPanel;
import org.scijava.ui.viewer.DisplayViewer;
import org.scijava.ui.viewer.DisplayWindow;

import javax.swing.JPanel;
import java.awt.BorderLayout;

abstract public class EasySwingDisplayViewer<T> extends
        AbstractDisplayViewer<T> implements DisplayViewer<T>
{
    private final Class<T> classOfObject;

    @Parameter
    ObjectService objectService;

    protected EasySwingDisplayViewer( Class< T > classOfObject )
    {
        this.classOfObject = classOfObject;
    }

    @Override
    public boolean isCompatible(final UserInterface ui) {
        return ui instanceof SwingUI;
    }

    @Override
    public boolean canView(final Display<?> d) {
        Object object = d.get( 0 );
        if(! classOfObject.isInstance( object ) )
            return false;
        T value = ( T ) object;
        return canView( value );
    }

    protected abstract boolean canView( T value );
    protected abstract void redoLayout();
    protected abstract void setLabel(final String s);
    protected abstract void redraw();
    protected abstract JPanel createDisplayPanel(T value);

    @Override
    public void onDisplayDeletedEvent( DisplayDeletedEvent e )
    {
        super.onDisplayDeletedEvent( e );
        objectService.removeObject( getDisplay().get( 0 ) );
    }

    @Override
    public void view(final DisplayWindow w, final Display<?> d) {
        objectService.addObject( d.get( 0 ) );
        super.view(w, d);
        final JPanel content = createDisplayPanel( getDisplay().get(0) );
        setPanel( new SwingDisplayPanel(w, d, this, content) );
    }


    public static class SwingDisplayPanel extends JPanel implements DisplayPanel
    {

        // -- instance variables --

        private final EasySwingDisplayViewer< ? > viewer;
        private final DisplayWindow window;
        private final Display< ? > display;

        // -- PlotDisplayPanel methods --

        public SwingDisplayPanel( DisplayWindow window, Display< ? > display, EasySwingDisplayViewer< ? > viewer, JPanel panel )
        {
            this.window = window;
            this.display = display;
            this.viewer = viewer;
            window.setContent(this);
            setLayout( new BorderLayout() );
            add(panel);
        }

        @Override
        public Display< ? > getDisplay() {
            return display;
        }

        // -- DisplayPanel methods --

        @Override
        public DisplayWindow getWindow() {
            return window;
        }

        @Override
        public void redoLayout()
        {
            viewer.redoLayout();
        }

        @Override
        public void setLabel( String s )
        {
            viewer.setLabel( s );
        }

        @Override
        public void redraw()
        {
            viewer.redraw();
        }
    }
}