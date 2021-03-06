package magic.ui.viewer;

import magic.data.CardImagesProvider;
import magic.data.GeneralConfig;
import magic.data.HighQualityCardImagesProvider;
import magic.data.IconImages;
import magic.model.MagicAbility;
import magic.ui.theme.Theme;
import magic.ui.theme.ThemeFactory;
import magic.ui.widget.FontsAndBorders;

import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ImagePermanentViewer extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int LOGICAL_X_MARGIN=50;
    private static final int LOGICAL_Y_MARGIN=70;

    private final ImagePermanentsViewer viewer;
    private final PermanentViewerInfo permanentInfo;
    private final List<PermanentViewerInfo> linkedInfos;
    private final Dimension logicalSize;
    private final List<Rectangle> linkedLogicalRectangles;
    private List<Rectangle> linkedScreenRectangles;
    private Point logicalPosition;
    private int logicalRow=1;

    public ImagePermanentViewer(final ImagePermanentsViewer viewer,final PermanentViewerInfo permanentInfo) {
        this.viewer=viewer;
        this.permanentInfo=permanentInfo;
        linkedInfos=new ArrayList<PermanentViewerInfo>();
        buildLinkedPermanents(linkedInfos,permanentInfo);
        linkedLogicalRectangles=new ArrayList<Rectangle>();
        logicalSize=calculateLogicalSize(linkedLogicalRectangles);
        linkedScreenRectangles=Collections.emptyList();

        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent event) {
                final int index=getPermanentInfoIndexAt(event.getX(),event.getY());
                if (index>=0) {
                    if (!GeneralConfig.getInstance().isTouchscreen()){
                        viewer.getController().processClick(linkedInfos.get(index).permanent);
                    }
                    else if (event.getClickCount() == 2) {
                        viewer.getController().processClick(linkedInfos.get(index).permanent);
                        viewer.getController().hideInfo();
                    }
                }
            }
            @Override
            public void mouseExited(final MouseEvent event) {
                viewer.getController().hideInfo();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(final MouseEvent event) {
                final int index=getPermanentInfoIndexAt(event.getX(),event.getY());
                if (index>=0) {
                    if (!GeneralConfig.getInstance().isMouseWheelPopup()) {
                        showCardPopup(index);
                    }
                } else {
                    viewer.getController().hideInfo();
                }
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent event) {
                if (GeneralConfig.getInstance().isMouseWheelPopup()) {
                    final int index=getPermanentInfoIndexAt(event.getX(),event.getY());
                    if (event.getWheelRotation() < 0) { // rotate mousewheel forward
                        if (index>=0) {
                            showCardPopup(index);
                        }
                    } else if (event.getWheelRotation() > 0) { // rotate mousewheel back
                        if (index>=0) {
                            viewer.getController().hideInfo();
                        }
                    }
                }
            }
        });

    }

    private void showCardPopup(int index) {
        final PermanentViewerInfo info=linkedInfos.get(index);
        final Point pointOnScreen=getLocationOnScreen();
        final Rectangle rect=new Rectangle(linkedScreenRectangles.get(index));
        rect.x+=pointOnScreen.x;
        rect.y+=pointOnScreen.y;
        viewer.getController().viewInfoAbove(info.cardDefinition,info.index,rect);
    }

    private int getPermanentInfoIndexAt(final int x,final int y) {
        for (int index=linkedScreenRectangles.size()-1;index>=0;index--) {
            final Rectangle rect=linkedScreenRectangles.get(index);
            if (x>=rect.x&&y>=rect.y&&x<rect.x+rect.width&&y<rect.y+rect.height) {
                return index;
            }
        }
        return -1;
    }

    private void buildLinkedPermanents(final List<PermanentViewerInfo> aLinkedInfos,final PermanentViewerInfo info) {
        for (final PermanentViewerInfo blocker : info.blockers) {
            buildLinkedPermanents(aLinkedInfos,blocker);
        }
        aLinkedInfos.addAll(info.linked);
        aLinkedInfos.add(info);
    }

    private Dimension calculateLogicalSize(final List<Rectangle> aLinkedLogicalRectangles) {
        int width=0;
        int height=0;
        int x=-LOGICAL_X_MARGIN;
        for (final PermanentViewerInfo linkedInfo : linkedInfos) {
            x+=LOGICAL_X_MARGIN;
            final int y=linkedInfo.lowered?LOGICAL_Y_MARGIN:0;
            final Rectangle rect;
            if (linkedInfo.tapped) {
                width=Math.max(width,x+CardImagesProvider.CARD_HEIGHT);
                height=Math.max(height,y+CardImagesProvider.CARD_WIDTH);
                rect=new Rectangle(x,y,CardImagesProvider.CARD_HEIGHT,CardImagesProvider.CARD_WIDTH);
            } else {
                width=Math.max(width,x+CardImagesProvider.CARD_WIDTH);
                height=Math.max(height,y+CardImagesProvider.CARD_HEIGHT);
                rect=new Rectangle(x,y,CardImagesProvider.CARD_WIDTH,CardImagesProvider.CARD_HEIGHT);
            }
            aLinkedLogicalRectangles.add(rect);
        }
        return new Dimension(width,height);
    }

    @Override
    public void setSize(final int width,final int height) {
        super.setSize(width,height);

        linkedScreenRectangles=new ArrayList<Rectangle>();
        for (final Rectangle logicalRect : linkedLogicalRectangles) {
            final Rectangle screenRect=new Rectangle();
            screenRect.x=(logicalRect.x*width)/logicalSize.width;
            screenRect.y=(logicalRect.y*height)/logicalSize.height;
            screenRect.width=(logicalRect.width*width)/logicalSize.width;
            screenRect.height=(logicalRect.height*height)/logicalSize.height;
            linkedScreenRectangles.add(screenRect);
        }
    }

    public int getPosition() {
        return permanentInfo.position;
    }

    public Dimension getLogicalSize() {
        return logicalSize;
    }

    public void setLogicalPosition(final Point logicalPosition) {
        this.logicalPosition=logicalPosition;
    }

    public Point getLogicalPosition() {
        return logicalPosition;
    }

    public void setLogicalRow(final int logicalRow) {
        this.logicalRow=logicalRow;
    }

    public int getLogicalRow() {
        return logicalRow;
    }

    @Override
    public void paintComponent(final Graphics g) {
        g.setFont(FontsAndBorders.FONT1);
        final FontMetrics metrics=g.getFontMetrics();
        final Graphics2D g2d=(Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        for (int index=0;index<linkedScreenRectangles.size();index++) {
            final PermanentViewerInfo linkedInfo=linkedInfos.get(index);
            final BufferedImage image=
                HighQualityCardImagesProvider.getInstance().getImage(linkedInfo.cardDefinition,linkedInfo.index,false);
            final Rectangle linkedRect=linkedScreenRectangles.get(index);
            final int x1=linkedRect.x;
            final int y1=linkedRect.y;
            final int x2=x1+linkedRect.width-1;
            final int y2=y1+linkedRect.height-1;

            if (linkedInfo.tapped) {
                final AffineTransform transform=new AffineTransform();
                final double scale = linkedRect.width * 1.0 / CardImagesProvider.CARD_HEIGHT;
                transform.translate(x1,y1);
                transform.scale(scale,scale);
                transform.translate(CardImagesProvider.CARD_HEIGHT/2,CardImagesProvider.CARD_WIDTH/2);
                transform.rotate(Math.PI/2);
                transform.translate(-CardImagesProvider.CARD_WIDTH/2,-CardImagesProvider.CARD_HEIGHT/2);
                g2d.drawImage(image,transform,this);
            } else {
                g.drawImage(image,x1,y1,x2,y2,0,0,CardImagesProvider.CARD_WIDTH,CardImagesProvider.CARD_HEIGHT,this);
            }

            int ax=x1+1;
            final int ay=y2-17;
            // Counters
            if (linkedInfo.permanent.hasCounters()) {
                ax=ImageDrawingUtils.drawCountersInfo(g,this,linkedInfo.permanent,ax,ay);
            }

            // Common combat ability icons.
            if (linkedInfo.creature) {
                if (linkedInfo.canNotTap) {
                    g.drawImage(IconImages.CANNOTTAP.getImage(),ax,ay,this);
                    ax+=16;
                }
                final Set<MagicAbility> abilityFlags=linkedInfo.abilityFlags;
                ax=ImageDrawingUtils.drawAbilityInfo(g,this, abilityFlags,ax,ay);
            }

            // Mana symbols
            if (linkedInfo.cardDefinition.getManaActivations().size() > 0) {
                ax=ImageDrawingUtils.drawManaInfo(g,this,linkedInfo.cardDefinition,ax,ay);
            }

            // Power, toughness, damage
            final String pt=linkedInfo.powerToughness;
            if (!pt.isEmpty()) {
                final String damage=linkedInfo.damage>0?String.valueOf(linkedInfo.damage):"";
                final boolean isDamage = damage.length() > 0;
                final int ptWidth=metrics.stringWidth(pt);
                if (linkedInfo.blocking) {
                    ImageDrawingUtils.drawCreatureInfo(g,metrics,pt,ptWidth,damage,x1,y1,false);
                } else {
                    ImageDrawingUtils.drawCreatureInfo(g,metrics,pt,ptWidth,damage,x2-ptWidth-4,y2-(isDamage?32:18),true);
                }
            }

            // Valid choice selection highlight
            if (viewer.isValidChoice(linkedInfo)) {
                if (GeneralConfig.getInstance().isHighlightOverlay() ||
                    (GeneralConfig.getInstance().isHighlightTheme() &&
                    ThemeFactory.getInstance().getCurrentTheme().getOptionUseOverlay())) {
                    final Color choiceColor = viewer.getController().isCombatChoice() ?
                        ThemeFactory.getInstance().getCurrentTheme().getColor(Theme.COLOR_COMBAT_CHOICE) :
                        ThemeFactory.getInstance().getCurrentTheme().getChoiceColor();

                    //draw a transparent overlay of choiceColor
                    g2d.setPaint(choiceColor);
                    g2d.fillRect(x1-1,y1-1,x2-x1+2,y2-y1+2);
                } else if (!GeneralConfig.getInstance().isHighlightNone()) {
                    final Color choiceColor = viewer.getController().isCombatChoice() ?
                        ThemeFactory.getInstance().getCurrentTheme().getColor(Theme.COLOR_COMBAT_CHOICE_BORDER) :
                        ThemeFactory.getInstance().getCurrentTheme().getColor(Theme.COLOR_CHOICE_BORDER);

                    //draw a one pixel border of choiceColor
                    g2d.setPaint(new Color(choiceColor.getRGB()));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(x1+1,y1+1,x2-x1-1,y2-y1-1);
                }
            }
        }
    }
}
