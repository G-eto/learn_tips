package funny;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * android 使用
 *
 */
public class ImgUtil {
    private List<String> pics = new ArrayList<>();

    private int widthPadding = 3;
    private int yPadding;//Y外围padding
    private int width;//小头像边长
    private int maxColumn = 3;
    private int defaultWidth = 144;
    private boolean isUrl = false;

    public ImgUtil(){}

    /**
     * @param size 144的倍数为好，因为间隔设的整型，这样直接/一下，间隔就，哈哈哈
     */
    public ImgUtil(int size){
        widthPadding = size/defaultWidth * 3;
        defaultWidth = size;
    }

    public void setPics(List<String> pics, Boolean isUrl){
        this.isUrl = isUrl;
        setPics(pics);
    }

    private void setPics(List<String> paths){
        pics = paths.subList(0, Math.min(9, paths.size()));
        int size = pics.size();
        if (size == 0){
            return;
        }
        if (size == 1){
            maxColumn = 1;
        }else if (size <= 4){
            maxColumn = 2;
        }else {
            maxColumn = 3;
        }
        width = (defaultWidth - (maxColumn + 1) * widthPadding) / maxColumn;

        int row;
        if (size <= 2){
            row = 1;
        }else if (size <= 6){
            row = 2;
        }else {
            row = 3;
        }
        yPadding = (defaultWidth - row *width - (row - 1)*widthPadding)/2;
    }

    //这行有几个
    private int thisColumn(int i){
        if (pics.size()%maxColumn != 0 && i < pics.size() % maxColumn){
            return pics.size()%maxColumn;
        }
        return maxColumn;
    }

    //这是第几行
    private int getY(int i){
        if (pics.size()%maxColumn == 0)
            return i / maxColumn;
        if (i < pics.size() % maxColumn){
            return 0;
        }
        return (i - pics.size() % maxColumn) / maxColumn + 1;
    }

    //这是第几列
    private int getX(int i){
        if (pics.size()%maxColumn == 0)
            return i % maxColumn;
        if (i < pics.size() % maxColumn){
            return i;
        }
        return (i - pics.size() % maxColumn) % maxColumn;
    }

    //放在哪儿啊
    private Rect getLu(int x, int y, int column){
        int padding = (defaultWidth - column * width - (column - 1)*widthPadding) / 2;//行外围padding
        Rect r = new Rect();
        r.set(x * (width + widthPadding) + padding, y * (width + widthPadding) + yPadding, x * (width + widthPadding) + width + padding, y * (width + widthPadding) + width + yPadding);
        return r;
    }

    //from url
    private Bitmap getPic(String u){
        URL url = null;
        try {
            url = new URL(u);
            InputStream is = url.openStream();
            Bitmap origin = BitmapFactory.decodeStream(is);
            is.close();
            //缩放 不缩放貌似也行
//            float ratio = 1.0f*defaultWidth/origin.getWidth();
//            Matrix matrix = new Matrix();
//            matrix.preScale(ratio, ratio);
//            Log.d("draw", "wh:"+origin.getWidth()+" "+origin.getWidth()+" "+ratio);
//            Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, origin.getWidth(), origin.getHeight(), matrix, false);
//            if (newBM.equals(origin)) {
//                return newBM;
//            }
//            origin.recycle();
            return origin;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    from file
    private Bitmap getPic2(String u){
        try {
            Bitmap origin =  BitmapFactory.decodeFile(u);
            float ratio = 1.0f*defaultWidth/origin.getWidth();
            Matrix matrix = new Matrix();
            matrix.preScale(ratio, ratio);
            Log.d("draw", "wh:"+origin.getWidth()+" "+origin.getWidth()+" "+ratio);
            Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, origin.getWidth(), origin.getHeight(), matrix, false);
            if (newBM.equals(origin)) {
                return newBM;
            }
            origin.recycle();
            return newBM;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void draw(String path){
        if (pics.size() == 0){
            Log.e("draw", "这绝对是来捣乱的, not find any pics");
            return;
        }
        long s = System.currentTimeMillis();
        Bitmap bitmap = Bitmap.createBitmap(defaultWidth, defaultWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawColor(Color.rgb(218, 222, 222));
        for (int i = 0; i < 9 && i < pics.size(); i++){
            int column = thisColumn(i);//这行有几个
            int x = getX(i);//列
            int y = getY(i);//行
            Log.d("draw", x+", "+y + ", "+ column+" pic: "+ pics.get(i));
            Bitmap b;
            if (isUrl){
                b = getPic(pics.get(i));
            }else {
                b = getPic2(pics.get(i));
            }
            assert b != null;
            Log.d("draw", "wh:"+b.getWidth() + ","+b.getHeight());
            canvas.drawBitmap(b, new Rect(0, 0, b.getWidth(), b.getHeight()), getLu(x, y, column), null);
        }
        canvas.save();
        canvas.restore(); // 存储
        if (path.equals("")){
            path = "/sdcard/url-png288-"+pics.size()+"no.png";
        }
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File(path)));
            Log.d("draw", "save in "+ path +", use time: "+ (System.currentTimeMillis()-s));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
