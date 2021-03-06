package magic.ui.viewer;

import magic.MagicMain;
import magic.model.MagicRandom;
import magic.ui.theme.ThemeFactory;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  Landscape image viewer.
 */
public class ImageViewer extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int DELAY=500;
    private static final int VIEWER_WIDTH=300;
    private static final int ZOOM_FACTOR=4;

    private static final List<File> imageFiles;
    private static final List<Integer> imageIndices;

    private final BufferedImage image;
    private final BufferedImage scaledImage;
    private boolean showScaled;
    private boolean scaled;
    private final int viewerHeight;
    private final int imageWidth;
    private final int imageHeight;
    private final int zoomX;
    private final int zoomY;
    private int sx1;
    private int sy1;
    private int sx2;
    private int sy2;
    private final Timer timer;

    static {
        final File imagePathFile=new File(MagicMain.getGamePath()+File.separator+"images");
        imageIndices=new ArrayList<Integer>();
        if (imagePathFile.exists()) {
            imageFiles=new ArrayList<File>();
            scanFiles(imageFiles,imagePathFile);
            System.err.println("Found "+imageFiles.size()+" user images.");
        } else {
            imageFiles=Collections.emptyList();
        }
    }

    private static void scanFiles(final List<File> aImageFiles,final File imagePathFile) {
        for (final File file : imagePathFile.listFiles()) {
            if (file.isDirectory()) {
                scanFiles(aImageFiles,file);
            } else {
                aImageFiles.add(file);
            }
        }
    }

    private static synchronized File rndFile() {
        assert !imageFiles.isEmpty() : "imageFiles is empty";

        if (imageIndices.size()==0) {
            for (int index=0;index<imageFiles.size();index++) {
                imageIndices.add(index);
            }
        }
        final Integer index=imageIndices.remove(MagicRandom.nextRNGInt(imageIndices.size()));
        return imageFiles.get(index);
    }

    public ImageViewer() {
        setOpaque(false);

        image = !imageFiles.isEmpty() ?
            magic.data.FileIO.toImg(rndFile(), ThemeFactory.getInstance().getCurrentTheme().getLogoTexture()) :
            ThemeFactory.getInstance().getCurrentTheme().getLogoTexture();

        imageWidth=image.getWidth();
        imageHeight=image.getHeight();
        viewerHeight=imageHeight*VIEWER_WIDTH/imageWidth;
        zoomX=imageWidth/ZOOM_FACTOR;
        zoomY=imageHeight/ZOOM_FACTOR;

        scaledImage=magic.ui.utility.GraphicsUtilities.scale(image,VIEWER_WIDTH,viewerHeight);

        timer = new Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showScaled=true;
                repaint();
            }
        });
        timer.setRepeats(false);

        final MouseAdapter mouseListener=new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent e) {
                showDelayed();
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                hideDelayed();
            }

            @Override
            public void mouseMoved(final MouseEvent e) {
                final int y=e.getY();
                if (y<=viewerHeight&&showScaled) {
                    final int x=e.getX();
                    int px=(x*imageWidth)/getWidth();
                    int py=(y*imageHeight)/viewerHeight;
                    if (px<zoomX) {
                        px=zoomX;
                    } else if (px+zoomX>=imageWidth) {
                        px=imageWidth-zoomX;
                    }
                    if (py<zoomY) {
                        py=zoomY;
                    } else if (py+zoomY>=imageHeight) {
                        py=imageHeight-zoomY;
                    }
                    scaled=true;
                    sx1=px-zoomX;
                    sy1=py-zoomY;
                    sx2=px+zoomX;
                    sy2=py+zoomY;
                } else {
                    scaled=false;
                }
                repaint();
            }
        };

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
    }

    private void showDelayed() {
        timer.restart();
    }

    private void hideDelayed() {
        timer.stop();
        showScaled=false;
        scaled=false;
        repaint();
    }

    @Override
    public void paintComponent(final Graphics g) {
        if (scaled) {
            final Graphics2D g2d=(Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(image,0,0,VIEWER_WIDTH,viewerHeight,sx1,sy1,sx2,sy2,this);
        } else {
            g.drawImage(scaledImage,0,0,this);
        }
        //g.setColor(Color.black);
        //g.drawRect(0,0,getWidth()-1,viewerHeight-1);
    }
}
