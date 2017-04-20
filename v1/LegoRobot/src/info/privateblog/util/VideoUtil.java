package info.privateblog.util;

import java.awt.image.BufferedImage;

import org.opencv.core.Mat;

public class VideoUtil {
	public static BufferedImage convertMatToBufferedImage(Mat mat) {  
        byte[] data = new byte[mat.width() * mat.height() * (int)mat.elemSize()];  
        int type;  
        mat.get(0, 0, data);  
  
        switch (mat.channels()) {    
            case 1:    
                type = BufferedImage.TYPE_BYTE_GRAY;    
                break;    
            case 3:    
                type = BufferedImage.TYPE_3BYTE_BGR;    
                // bgr to rgb    
                byte b;    
                for(int i=0; i<data.length; i=i+3) {    
                    b = data[i];    
                    data[i] = data[i+2];    
                    data[i+2] = b;    
                }    
                break;    
            default:    
                throw new IllegalStateException("Unsupported number of channels");  
        }    
          
        BufferedImage out = new BufferedImage(mat.width(), mat.height(), type);  
  
        out.getRaster().setDataElements(0, 0, mat.width(), mat.height(), data);  
          
        return out;  
    }  
}
