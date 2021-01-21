package funny

import android.graphics.*
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URL

/**
 * android 使用
 *
 */
class ConvFaceCreator() {
    private var pics = listOf<String>()
    private val widthPadding = 6
    private var yPadding = 0 //Y外围padding
    private var width = 0 //小头像边长
    private var maxColumn = 3
    private val defaultWidth = 288
    private var covId: Long = 0

    constructor(id: Long) : this() {
        covId = id
    }

    fun setUrls(paths: List<String>): ConvFaceCreator?{
        pics = paths.subList(0, 9.coerceAtMost(paths.size))
        Log.e("draw pic", "set urls: ${paths.size}")
        val size = pics.size
        if (size == 0) {
            return null
        }
        maxColumn = when {
            size == 1 -> 1
            size <= 4 -> 2
            else -> 3
        }
        width = (defaultWidth - (maxColumn + 1) * widthPadding) / maxColumn
        val row: Int
        row = when {
            size <= 2 -> 1
            size <= 6 -> 2
            else -> 3
        }
        yPadding = (defaultWidth - row * width - (row - 1) * widthPadding) / 2
        return this
    }

    //当前行有几个图
    private fun thisColumn(i: Int): Int {
        return if (pics.size % maxColumn != 0 && i < pics.size % maxColumn) {
            pics.size % maxColumn
        } else maxColumn
    }

    //当前是第几行
    private fun getY(i: Int): Int {
        if (pics.size % maxColumn == 0) return i / maxColumn
        return if (i < pics.size % maxColumn) {
            0
        } else (i - pics.size % maxColumn) / maxColumn + 1
    }

    //当前是第几列
    private fun getX(i: Int): Int {
        if (pics.size % maxColumn == 0) return i % maxColumn
        return if (i < pics.size % maxColumn) {
            i
        } else (i - pics.size % maxColumn) % maxColumn
    }

    //放置位置
    private fun getDst(x: Int, y: Int, column: Int): Rect? {
        val padding = (defaultWidth - column * width - (column - 1) * widthPadding) / 2 //行外围padding
        val r = Rect()
        r.set(
            x * (width + widthPadding) + padding,
            y * (width + widthPadding) + yPadding,
            x * (width + widthPadding) + width + padding,
            y * (width + widthPadding) + width + yPadding
        )
        return r
    }

    private fun downloadPic(u: String): Bitmap? {
        var url: URL? = null
        try {
            url = URL(u)
            val ips = url.openStream()
            val origin = BitmapFactory.decodeStream(ips)
            ips.close()
            return origin
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    //画图
    fun draw(lazy: Boolean = true): String {
        val s = System.currentTimeMillis()
        val path = Utils.getSyncPathName("png", "cov_avatar_${covId}_${pics.size}")
        if (lazy && File(path).exists()){
            return path
        }
        val bitmap = Bitmap.createBitmap(defaultWidth, defaultWidth, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawColor(Color.rgb(218, 222, 222))
        for (url in pics) {
            val i = pics.indexOf(url)
            val column = thisColumn(i)
            val x = getX(i) //列
            val y = getY(i) //行
            val b = downloadPic(url) ?: downloadPic("https://ddmsgfile.oss-cn-beijing.aliyuncs.com/1826da7d652f83c01afb48eb43699b44.jpg")
            b ?:return ""
            canvas.drawBitmap(b, Rect(0, 0, b.width, b.height), getDst(x, y, column)!!, null)
        }
        canvas.save()
        canvas.restore() // 存储
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(File(path)))
            Log.d("outman-wwh", "draw header: $path, use time: ${System.currentTimeMillis() - s}ms")
            return path
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }
}