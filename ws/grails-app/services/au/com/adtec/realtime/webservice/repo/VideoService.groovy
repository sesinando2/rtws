package au.com.adtec.realtime.webservice.repo

import com.xuggle.mediatool.IMediaReader
import com.xuggle.mediatool.MediaListenerAdapter
import com.xuggle.mediatool.ToolFactory
import com.xuggle.mediatool.event.IVideoPictureEvent
import com.xuggle.xuggler.Global
import com.xuggle.xuggler.IContainer
import grails.transaction.Transactional

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@Transactional
class VideoService {

    def createVideoThumbnail(VideoFileData videoFileData) {
        File file = new File("$videoFileData.id-$videoFileData.filename")
        FileOutputStream fos = new FileOutputStream(file)
        fos << videoFileData.data
        fos.close()
        IContainer container = IContainer.make()
        container.open(file.absolutePath, IContainer.Type.READ, null)
        IMediaReader reader = ToolFactory.makeReader(container)
        reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR)
        def snapListener = new ImageSnapListener((container.duration/3).longValue())
        reader.addListener(snapListener)
        while (reader.readPacket() == null);
        container.close()
        if (file.exists()) file.delete()
        videoFileData.thumbData = snapListener.thumbnailData
        videoFileData.thumbContentType = "image/jpg"
    }

    private class ImageSnapListener extends MediaListenerAdapter {

        private long snapInterval
        private int videoStreamIndex = -1
        private long lastPtsWrite = Global.NO_PTS
        private int writeCounter = 0
        private byte[] thumbnailData

        ImageSnapListener(long snapInterval) {
            this.snapInterval = snapInterval
        }

        public void onVideoPicture(IVideoPictureEvent event) {
            if (event.streamIndex != videoStreamIndex) {
                if (videoStreamIndex == -1)
                    videoStreamIndex = event.streamIndex
                else return
            }

            if (lastPtsWrite == Global.NO_PTS)
                lastPtsWrite = event.timeStamp - snapInterval

            if (event.timeStamp - lastPtsWrite >= snapInterval) {
                if (++writeCounter == 2)
                    snapFrame(event.image)
                lastPtsWrite += snapInterval
            }
        }

        private void snapFrame(BufferedImage image) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream()
            ImageIO.write(image, "jpg", bos)
            thumbnailData = bos.toByteArray()
        }

        def getThumbnailData() { thumbnailData }
    }
}
