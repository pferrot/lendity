package com.pferrot.lendity.document;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.Locale;

import com.pferrot.lendity.document.exception.DocumentException;
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
	 */
	public static BufferedImage getHighQualityScaledInstance(BufferedImage img,
            int maxTargetWidth,
            int maxTargetHeight) {
		return getScaledInstance(img, maxTargetWidth, maxTargetHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
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
     */
    public static BufferedImage getScaledInstance(BufferedImage img,
                                           int maxTargetWidth,
                                           int maxTargetHeight,
                                           Object hint,
                                           boolean higherQuality)
    {
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
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }

}
