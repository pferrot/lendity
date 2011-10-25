package com.pferrot.lendity.document;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.Locale;

import com.pferrot.lendity.document.exception.DocumentException;
import com.pferrot.lendity.document.exception.PictureException;
import com.pferrot.lendity.document.exception.PictureTooSmallException;
import com.pferrot.lendity.i18n.I18nUtils;

public class DocumentUtils {

	/**
	 * Supported types for now: PNG, JPEG and GIF.
	 *
	 * @param pMimeType
	 * @return
	 */
	public static final boolean isSupportedImageMimeType(final String pMimeType) {
		return DocumentConsts.MIME_TYPE_IMAGE_GIF.equals(pMimeType)	||
			DocumentConsts.MIME_TYPE_IMAGE_JPEG.equals(pMimeType) ||
			DocumentConsts.MIME_TYPE_IMAGE_PNG.equals(pMimeType) ||
			DocumentConsts.MIME_TYPE_IMAGE_PJPEG.equals(pMimeType) ||
			DocumentConsts.MIME_TYPE_IMAGE_XPNG.equals(pMimeType);
	}
	
	public static final boolean isGif(String pMimeType) {
		return DocumentConsts.MIME_TYPE_IMAGE_GIF.equals(pMimeType);
	}
	
	public static final boolean isJpeg(String pMimeType) {
		return DocumentConsts.MIME_TYPE_IMAGE_JPEG.equals(pMimeType) ||
		DocumentConsts.MIME_TYPE_IMAGE_PJPEG.equals(pMimeType);
	}
	
	public static final boolean isPng(String pMimeType) {
		return DocumentConsts.MIME_TYPE_IMAGE_PNG.equals(pMimeType) ||
		DocumentConsts.MIME_TYPE_IMAGE_XPNG.equals(pMimeType);
	}
	
	public static final String getFormat(final String pMimeType) throws DocumentException {
		if (isJpeg(pMimeType)) {
			return "jpg";
		}
		else if (isPng(pMimeType)) {
			return "png";
		}
		else if (isGif(pMimeType)) {
			return "gif";
		}
		else {
			throw new DocumentException("Unsupported image mime type: " + pMimeType);
		}
		
		
	}
	
	public static String getImageValidationErrorMessage(final Locale pLocale) {
		String message = "";
		message = I18nUtils.getMessageResourceString("validation_imageIoError", pLocale);
		return message;
	}

	/**
	 * Convenience method that returns a HQ scaled instance of the
     * provided {@code BufferedImage}.
     * 
	 * @param img
	 * @param maxTargetWidth
	 * @param maxTargetHeight
	 * @return
	 * @throws PictureException 
	 */
	public static BufferedImage getHighQualityScaledInstance(BufferedImage img,
            int maxTargetWidth,
            int maxTargetHeight) throws PictureException {
		return getScaledInstance(img, maxTargetWidth, maxTargetHeight, -1, -1, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
	}
	
	public static BufferedImage getHighQualityScaledInstanceWithTotalSize(final BufferedImage pImg,
            final int pMaxTargetWidth,
            final int pMaxTargetHeight,
            final int pFinalWidth,
            final int pFinalHeight) throws PictureException {
		return getScaledInstance(pImg, pMaxTargetWidth, pMaxTargetHeight, pFinalWidth, pFinalHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
	}
	
	public static BufferedImage getHighQualityScaledInstanceWithMinSize(BufferedImage img,
            int minTargetWidth,
            int minTargetHeight) throws PictureTooSmallException {
		return getScaledInstanceMinSize(img, minTargetWidth, minTargetHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
	}
	
	public static BufferedImage getHighQualityCroppedInstance(BufferedImage pImg,
            int pX1, int pY1,
            int pWidth, int pHeight) throws PictureTooSmallException {
		return getCropedInstance(pImg, pX1, pY1, pWidth, pHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
	}
	
	/**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     *
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance,
     *    in pixels
     * @param targetHeight the desired height of the scaled instance,
     *    in pixels
     * @param hint one of the rendering hints that corresponds to
     *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *    scaling technique that provides higher quality than the usual
     *    one-step technique (only useful in downscaling cases, where
     *    {@code targetWidth} or {@code targetHeight} is
     *    smaller than the original dimensions, and generally only when
     *    the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
	 * @throws PictureException 
     */
    public static BufferedImage getScaledInstance(BufferedImage img,
                                           int maxTargetWidth,
                                           int maxTargetHeight,
                                           int pFinalTargetWidth,
                                           int pFinalTargetHeight,
                                           Object hint,
                                           boolean higherQuality) throws PictureException
    {
    	if (pFinalTargetWidth > 0 && maxTargetWidth > pFinalTargetWidth) {
    		throw new PictureException("Final target width is lower than max width");
    	}
    	else if (pFinalTargetHeight > 0 && maxTargetHeight > pFinalTargetHeight) {
    		throw new PictureException("Final target height is lower than max height");
    	}
    	
    	int currentWidth = img.getWidth();
    	int currentHeight = img.getHeight();
    	
    	if (currentWidth < maxTargetWidth && currentHeight < maxTargetHeight) {
    		return img;
    	}
    	
    	float widthRatio = ((float)currentWidth) / ((float)maxTargetWidth);
    	float heightRatio = ((float)currentHeight) / ((float)maxTargetHeight);
    	
    	int targetHeight;
    	int targetWidth;
    	if (widthRatio > heightRatio) {
    		targetWidth = maxTargetWidth;
    		targetHeight = (int) (((float)currentHeight) / widthRatio); 
    	}
    	else {
    		targetWidth = (int) (((float)currentWidth) / heightRatio);
    		targetHeight = maxTargetHeight;
    	}
    	
    	
    	
    	
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }
        
        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, Color.WHITE, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);
        
        // If the picture must fit in a different size.
        if (pFinalTargetWidth > 0 && pFinalTargetHeight > 0 &&
        	(pFinalTargetWidth != w || pFinalTargetHeight != h)) {
        	BufferedImage tmp = new BufferedImage(pFinalTargetWidth, pFinalTargetHeight, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, pFinalTargetWidth, pFinalTargetHeight);
            int x = 0;
            int y = 0;
            if (w < pFinalTargetWidth) {
            	x = (pFinalTargetWidth - w) / 2;
            }
            if (h < pFinalTargetHeight) {
            	y = (pFinalTargetHeight - h) / 2;
            }
            
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, x, y, w, h, Color.WHITE, null);
            g2.dispose();
            
            ret = tmp;
        }

        return ret;
    }
    
  
    public static BufferedImage getScaledInstanceMinSize(BufferedImage img,
                                           int minTargetWidth,
                                           int minTargetHeight,
                                           Object hint,
                                           boolean higherQuality) throws PictureTooSmallException
    {
    	int currentWidth = img.getWidth();
    	int currentHeight = img.getHeight();
    	
    	if (currentWidth < minTargetWidth || currentHeight < minTargetHeight) {
    		throw new PictureTooSmallException();
    	}
    	
    	float widthRatio = ((float)currentWidth) / ((float)minTargetWidth);
    	float heightRatio = ((float)currentHeight) / ((float)minTargetHeight);
    	
    	int targetHeight;
    	int targetWidth;
    	if (widthRatio < heightRatio) {
    		targetWidth = minTargetWidth;
    		targetHeight = (int) (((float)currentHeight) / widthRatio); 
    	}
    	else {
    		targetWidth = (int) (((float)currentWidth) / heightRatio);
    		targetHeight = minTargetHeight;
    	}
    	
    	
    	
    	
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }
        
        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, Color.WHITE, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
    
    
    
    public static BufferedImage getCropedInstance(BufferedImage pImg,
    									   int pX1,
    		                               int pY1,
                                           int pWidth,
                                           int pHeight,
                                           Object pHint,
                                           boolean pHigherQuality) throws PictureTooSmallException {
    	int currentWidth = pImg.getWidth();
    	int currentHeight = pImg.getHeight();
    	
    	if (pX1 == 0 && pY1 == 0 && pWidth == currentWidth && pHeight == currentHeight) {
    		return pImg;
    	}
    	
    	if (currentWidth < (pX1 + pWidth) || currentHeight < (pY1 + pHeight)) {
    		throw new PictureTooSmallException();
    	}
    	
        int type = (pImg.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

        BufferedImage tmp = new BufferedImage(pWidth, pHeight, type);
        Graphics2D g2 = tmp.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, pHint);
        g2.drawImage(pImg, 0, 0, pWidth, pHeight, pX1, pY1, pX1 + pWidth, pY1 + pHeight, Color.WHITE, null);
        g2.dispose();

        return tmp;
    }    
    

}
