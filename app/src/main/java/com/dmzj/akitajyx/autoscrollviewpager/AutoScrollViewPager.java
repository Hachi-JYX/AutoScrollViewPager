package com.dmzj.akitajyx.autoscrollviewpager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akitajyx on 2016/5/24.
 */
public class AutoScrollViewPager extends ViewPager {

    private int pageCount = 2;//默认页数为2
    private List<ImageView> dotImg = new ArrayList<>();
    private List<String> title = null;
    private List<String> imgUrl = null;
    private TextView textView;
    private int currentPageIndex = 0;
    private boolean isLooping = false;//是否支持无限滑动，默认不支持
    private int delayDuration = 3000;
    private OnViewItemClickListener itemClickListener;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                Log.d("TAG","切换到下一页");
                //切换到下一页
                int curr = getCurrentItem();
                ++curr;
                setCurrentItem(curr);

                //循环发送消息实现自动轮播
                Message msg2 = Message.obtain();
                msg2.what = 1;
                mHandler.sendMessageDelayed(msg2,delayDuration);
            }
        }
    };
    private ImageCache imageCache;

    public void setItemClickListener(OnViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setDelayDuration(int delayDuration) {
        this.delayDuration = delayDuration;
    }

    public void setImgUrl(List<String> imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setTitle(List<String> title, TextView textView) {
        this.title = title;
        this.textView = textView;
        this.textView.setText(title.get(0));
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    public AutoScrollViewPager(Context context) {
        super(context);
    }

    public AutoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        imageCache = new ImageCache(getContext());
    }

    public void init(int pageNumber, LinearLayout layoutDot) {
        pageCount = pageNumber;

        //根据页面数动态创建指示器
        for (int i = 0; i < pageCount; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(R.drawable.dot_selector);
            //配置点的参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            //添加点与点之间的间距
            params.rightMargin = 6;
            layoutDot.addView(imageView,params);
            dotImg.add(imageView);
        }
        dotImg.get(0).setSelected(true);

        //设置适配器
        PagerAdapter adapter = new ImageViewAdapter();
        this.setAdapter(adapter);
        this.setOnPageChangeListener(new PageChangeListener());
    }

    //开启自动轮播
    public void startScroll(){
        Message message = Message.obtain();
        message.what = 1;
        mHandler.sendMessageDelayed(message,delayDuration);
    }
    //停止自动轮播
    public void stopscoll(){
        //清除所有消息
        mHandler.removeCallbacksAndMessages(null);
    }

    class PageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        //滑动到第i页
        @Override
        public void onPageSelected(int position) {
            //更新旧点的状态
            dotImg.get(currentPageIndex%pageCount).setSelected(false);
            currentPageIndex = position;
            //更新新点的状态
            dotImg.get(currentPageIndex%pageCount).setSelected(true);
            if (textView!=null && title!=null){
                textView.setText(title.get(currentPageIndex%pageCount));
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class ImageOnTouchListener implements OnTouchListener{

        private int down=0;
        private long downtime=0;
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    stopscoll();
                    down = (int) getX();
                    downtime = System.currentTimeMillis();
                    Log.d("TAG","ACTION_DOWN");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d("TAG","ACTION_MOVE");
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("TAG","ACTION_UP");
                    int up = (int) getX();
                    if (down == up && System.currentTimeMillis()-downtime<300){
                        if (itemClickListener!=null){
                            Log.d("TAG","点击了");
                            itemClickListener.onItemClick();
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    Log.d("TAG","ACTION_CANCEL");
                    startScroll();
                    break;
            }
            return true;
        }
    }

    private class ImageViewAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            if (isLooping){
                return Integer.MAX_VALUE;
            }else {
                return pageCount;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //创建显示的图片
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ViewPager.LayoutParams params = new ViewPager.LayoutParams();
            params.width = LayoutParams.MATCH_PARENT;
            params.height = LayoutParams.MATCH_PARENT;
            if (imgUrl!=null && imgUrl.size()>0){
                Log.d("TAG",imgUrl.get(position%pageCount));
                imageCache.disPlayImage(imageView,imgUrl.get(position%pageCount));
                //Glide.with(getContext()).load(imgUrl.get(position%pageCount)).into(imageView);
            }
            container.addView(imageView,params);

            imageView.setOnTouchListener(new ImageOnTouchListener());
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public static interface OnViewItemClickListener{
        void onItemClick();
    }
}
