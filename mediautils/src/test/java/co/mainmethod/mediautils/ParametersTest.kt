package co.mainmethod.mediautils

import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ParametersTest {

    @Test
    @Throws(Exception::class)
    fun toArrayTest() {
        val parameters = Parameters(inputAudio = "audio.mp3", inputImage = "picture.png",
                outputVideo = "video.mp4")
        val array = parameters.toArray()

        Assert.assertEquals(true, array.contains("-i audio.mp3"))
        Assert.assertEquals(true, array.contains("-i picture.png"))
        Assert.assertEquals(true, array.contains("-shortest video.mp4"))
        Assert.assertEquals(false, array.contains("-ss 0"))
        Assert.assertEquals(false, array.contains("-t 0"))
    }

    @Test
    @Throws(Exception::class)
    fun toArrayTestWithDurationSeeking() {
        val parameters = Parameters(inputAudio = "audio.mp3", inputImage = "picture.png",
                outputVideo = "video.mp4", startSecs = 4510, durationSecs = 9020)
        val array = parameters.toArray()

        Assert.assertEquals(true, array.contains("-i audio.mp3"))
        Assert.assertEquals(true, array.contains("-i picture.png"))
        Assert.assertEquals(true, array.contains("-shortest video.mp4"))
        Assert.assertEquals(true, array.contains("-ss 01:15:10"))
        Assert.assertEquals(true, array.contains("-t 02:30:20"))
    }
}