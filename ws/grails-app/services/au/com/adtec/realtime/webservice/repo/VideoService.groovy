package au.com.adtec.realtime.webservice.repo

import com.xuggle.mediatool.IMediaReader
import com.xuggle.mediatool.MediaListenerAdapter
import com.xuggle.mediatool.ToolFactory
import com.xuggle.mediatool.event.IVideoPictureEvent
import com.xuggle.xuggler.Global
import grails.transaction.Transactional

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@Transactional
class VideoService {

    public static final double SECONDS_BETWEEN_FRAMES = 15;
    public static final long MICRO_SECONDS_BETWEEN_FRAMES = (long) (Global.DEFAULT_PTS_PER_SECOND * SECONDS_BETWEEN_FRAMES);

    def createVideoThumbnail(FileData videoFileData) {
        File file = new File("$videoFileData.id-$videoFileData.filename")
        FileOutputStream fos = new FileOutputStream(file)
        fos << videoFileData.data
        fos.flush()
        fos.close()

        IMediaReader reader = ToolFactory.makeReader(file.absoluteFile)
        reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR)
        reader.addListener(new ImageSnapListener())
        while (reader.readPacket() == null);
    }

    private static class ImageSnapListener extends MediaListenerAdapter {

        private static int videStreamIndex = -1;
        private static long lastPtsWrite = Global.NO_PTS;

        public void onVideoPicture(IVideoPictureEvent event) {
            if (event.streamIndex != videStreamIndex) {
                if (videStreamIndex == -1) {
                    videStreamIndex = event.streamIndex
                } else {
                    return;
                }
            }

            if (lastPtsWrite == Global.NO_PTS) {
                lastPtsWrite = event.timeStamp - MICRO_SECONDS_BETWEEN_FRAMES
            }

            if (event.timeStamp - lastPtsWrite >= MICRO_SECONDS_BETWEEN_FRAMES) {

            }
        }

        private String dumpImageToFile(BufferedImage image) {
            ImageIO.write(image, "png", System.currentTimeMillis())
        }
    }
}
